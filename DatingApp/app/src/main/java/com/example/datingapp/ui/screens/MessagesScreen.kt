package com.example.datingapp.ui.screens

import ProfileViewModel
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
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
import com.example.datingapp.data.ChatEntity
import com.example.datingapp.ui.components.NavigationMenu
import com.example.datingapp.viewmodel.ChatViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.datingapp.R
import com.example.datingapp.ui.theme.MediumPink
import com.example.datingapp.ui.utils.formatTime
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
                    onClick = { navController.popBackStack() },
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

@Composable
fun ChatList(
    navController: NavController,
    chatList: List<ChatEntity>,
    currentUserId: String,
    userNameMap: Map<String, String>,
    userPhotoMap: Map<String, Bitmap?>
) {
    LazyColumn(
        modifier = Modifier.padding(8.dp).background(color = Color.Transparent),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(chatList) { chat ->
            ChatItem(navController, chat, currentUserId, userNameMap, userPhotoMap)
        }
    }
}

@Composable
fun ChatItem(
    navController: NavController,
    chat: ChatEntity,
    currentUserId: String,
    userNameMap: Map<String, String>,
    userPhotoMap: Map<String, Bitmap?>
) {
    val userIdToShow = if (chat.idFirstUser == currentUserId) chat.idSecondUser else chat.idFirstUser
    val userName = userNameMap[userIdToShow] ?: "Loading..."
    val userPhoto = userPhotoMap[userIdToShow]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(65.dp)
            .clickable { navController.navigate("Chat/${userIdToShow}") },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (userPhoto != null) {
                Image(
                    bitmap = userPhoto.asImageBitmap(),
                    contentDescription = "User photo",
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("N/A", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.fillMaxHeight(0.8f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = userName,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.lastMessageId.ifEmpty { "Type your first message!" },
                        color = Color.Gray,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatTime(chat.lastUpdateTime),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

            }
        }
    }
}


