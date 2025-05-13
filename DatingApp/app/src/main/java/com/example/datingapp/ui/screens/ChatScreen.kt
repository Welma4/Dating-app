package com.example.datingapp.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.datingapp.R
import com.example.datingapp.data.MessageEntity
import com.example.datingapp.data.Routes
import com.example.datingapp.data.UserEntity
import com.example.datingapp.ui.components.MessageCard
import com.example.datingapp.ui.theme.GrayBlue
import com.example.datingapp.ui.theme.MediumPink
import com.example.datingapp.ui.utils.formatTimeTodMy
import com.example.datingapp.ui.utils.getCurrentDateTime
import com.example.datingapp.viewmodel.ChatViewModel
import com.example.datingapp.viewmodel.MessageViewModel
import com.example.datingapp.viewmodel.PhotoViewModel
import com.example.datingapp.viewmodel.ProfileViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChatScreen(
    interlocutorId: String,
    navController: NavController,
    chatViewModel: ChatViewModel,
    profileViewModel: ProfileViewModel,
    photoViewModel: PhotoViewModel,
    messageViewModel: MessageViewModel,
    currentUserId: String,
) {
    var text by remember { mutableStateOf("") }
    val interlocutor = remember { mutableStateOf(UserEntity()) }
    val interlocutorPhoto = remember { mutableStateOf<Bitmap?>(null) }
    var currentChatId by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf<List<MessageEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        photoViewModel.getPhotoBitmap(
            idUser = interlocutorId,
            onSuccess = { bitmap ->
                interlocutorPhoto.value = bitmap
                isLoading = false
            },
            onFailure = { error ->
                Log.d("MyTag", "Error loading interlocutor photo: $error")
                isLoading = false
            },
        )

        profileViewModel.fetchUserFromFirestore(
            interlocutorId,
            onSuccess = { user ->
                interlocutor.value = user
                isLoading = false
            },
            onFailure = { error ->
                Log.d("MyTag", error)
                isLoading = false
            },
        )

        chatViewModel.fetchChatId(
            currentUserId,
            interlocutorId,
            onSuccess = { id ->
                currentChatId = id
                messageViewModel.fetchMessagesForChatRealtime(
                    currentChatId,
                    onSuccess = { result ->
                        messages = result
                        isLoading = false
                    },
                    onFailure = { error ->
                        Log.d("MyTag", error)
                        isLoading = false
                    },
                )
            },
            onFailure = { error ->
                Log.d("MyTag", error)
                isLoading = false
            },
        )
    }

    Scaffold(
        modifier =
        Modifier
            .fillMaxSize()
            .imePadding(),
        content = {
            Box(
                modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Box(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 0.dp, end = 0.dp, top = 24.dp, bottom = 8.dp),
                    ) {
                        IconButton(
                            onClick = { navController.navigate(Routes.Messages) },
                            modifier = Modifier.align(Alignment.CenterStart),
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_back_arrow),
                                contentDescription = "Back Arrow",
                                tint = Color.Black,
                                modifier = Modifier.size(32.dp),
                            )
                        }
                        Row(
                            modifier =
                            Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 70.dp),
                        ) {
                            interlocutorPhoto.value?.let { bitmap ->
                                Image(
                                    bitmap =
                                    Bitmap
                                        .createScaledBitmap(bitmap, 45, 45, false)
                                        .asImageBitmap(),
                                    contentDescription = "likedUserPhoto",
                                    modifier =
                                    Modifier
                                        .size(45.dp)
                                        .clip(CircleShape),
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                modifier = Modifier.align(Alignment.CenterVertically),
                                text = interlocutor.value.firstName,
                                fontSize = 22.sp,
                                style = TextStyle(letterSpacing = 0.2.sp),
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }

                    val listState = rememberLazyListState()

                    LaunchedEffect(messages) {
                        if (messages.isNotEmpty()) {
                            listState.scrollToItem(messages.size - 1)
                        }
                    }
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(200.dp),
                                color = MediumPink,
                            )
                        }
                    } else if (messages.isEmpty()) {
                        Box(
                            modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "There are no messages here yet. Write first!",
                                color = Color.Gray,
                                fontSize = 16.sp,
                            )
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp, bottom = 80.dp, top = 0.dp),
                        ) {
                            var previousDate: String? = null
                            items(messages) { message ->
                                val currentDate = formatTimeTodMy(message.sendTime)
                                if (previousDate != currentDate) {
                                    Box(
                                        modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(top = 36.dp, bottom = 10.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = currentDate,
                                            fontSize = 14.sp,
                                            color = Color.Gray,
                                        )
                                    }
                                    previousDate = currentDate
                                }
                                MessageCard(message, message.idUser == currentUserId)
                            }
                        }
                    }
                }

                Row(
                    modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextField(
                        modifier =
                        Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(shape = RoundedCornerShape(24.dp)),
                        value = text,
                        placeholder = {
                            Text(
                                text = "Type Something...",
                                color = Color.Gray,
                                fontSize = 16.sp,
                            )
                        },
                        textStyle = TextStyle(fontSize = 16.sp),
                        colors =
                        TextFieldDefaults.colors(
                            unfocusedContainerColor = GrayBlue,
                            focusedContainerColor = GrayBlue,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        onValueChange = { text = it },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        modifier =
                        Modifier
                            .height(52.dp)
                            .clip(shape = RoundedCornerShape(24.dp)),
                        onClick = {
                            if (text.isNotBlank()) {
                                messageViewModel.saveMessageToFirestore(
                                    currentUserId,
                                    currentChatId,
                                    text,
                                    onSuccess = {
                                        Log.d("MyTag", "Message sent successfully")
                                        chatViewModel.updateChat(
                                            currentChatId,
                                            text,
                                            getCurrentDateTime(),
                                            onSuccess = {
                                                text = ""
                                                Log.d("MyTag", "Chat updated successfully")
                                            },
                                            onFailure = { error ->
                                                Log.d("MyTag", error)
                                            },
                                        )
                                    },
                                    onFailure = { error ->
                                        Log.d("MyTag", error)
                                    },
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (text.isEmpty()) GrayBlue else MediumPink),
                    ) {
                        Text(
                            text = ">",
                            color = if (text.isEmpty()) Color.Gray else Color.White,
                            fontSize = 24.sp,
                        )
                    }
                }
            }
        },
    )
}
