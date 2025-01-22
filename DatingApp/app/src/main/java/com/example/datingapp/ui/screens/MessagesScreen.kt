package com.example.datingapp.ui.screens

import ProfileViewModel
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.datingapp.ui.components.NavigationMenu
import com.example.datingapp.viewmodel.ChatViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.datingapp.R
import com.example.datingapp.data.Routes
import com.example.datingapp.ui.components.ChatList
import com.example.datingapp.ui.theme.MediumPink
import com.example.datingapp.viewmodel.PhotoViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MessagesScreen(
    navController: NavController,
    chatViewModel: ChatViewModel,
    profileViewModel: ProfileViewModel,
    photoViewModel: PhotoViewModel,
    currentUserId: String
) {
    val chatList = chatViewModel.chatList.observeAsState(emptyList())
    val userNameMap = remember { mutableStateMapOf<String, String>() }
    val userPhotoMap = remember { mutableStateMapOf<String, Bitmap?>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(currentUserId) {
        chatViewModel.fetchChatsForUser(
            userId = currentUserId,
            onSuccess = {
                chatList.value.forEach { chat ->
                    val userIdToFetch =
                        if (chat.idFirstUser == currentUserId) chat.idSecondUser else chat.idFirstUser
                    if (userIdToFetch !in userNameMap) {
                        profileViewModel.fetchUserFromFirestore(
                            uid = userIdToFetch,
                            onSuccess = { user ->
                                userNameMap[userIdToFetch] = user.firstName
                                if (userNameMap.size == chatList.value.size) {
                                    isLoading = false
                                }
                            },
                            onFailure = { error ->
                                Log.e("MessagesScreen", "Error fetching user: $error")
                                isLoading = false
                            }
                        )
                    }

                    if (userIdToFetch !in userPhotoMap) {
                        photoViewModel.getPhotoBitmap(
                            idUser = userIdToFetch,
                            onSuccess = { bitmap ->
                                userPhotoMap[userIdToFetch] = bitmap
                            },
                            onFailure = { error ->
                                Log.e("MessagesScreen", "Error fetching photo: $error")
                                userPhotoMap[userIdToFetch] = null
                            }
                        )
                    }
                }
                if (chatList.value.isEmpty()) {
                    isLoading = false
                }
            },
            onFailure = { error ->
                Log.e("MessagesScreen", "Error fetching chats: $error")
                isLoading = false
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { NavigationMenu(navController) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 4.dp)
            ) {
                IconButton(
                    onClick = { navController.navigate(Routes.Home) },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back_arrow),
                        contentDescription = "Back Arrow",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "Chat",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(200.dp),
                        color = MediumPink
                    )
                }
            } else {
                if (chatList.value.isNotEmpty()) {
                    ChatList(navController, chatList.value, currentUserId, userNameMap, userPhotoMap)
                } else {
                    Text("No chats available.", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}






