package com.example.datingapp.ui.screens

import ProfileViewModel
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.datingapp.R
import com.example.datingapp.viewmodel.ChatViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.text.TextStyle
import com.example.datingapp.data.MessageEntity
import com.example.datingapp.data.UserEntity
import com.example.datingapp.ui.theme.GrayBlue
import com.example.datingapp.ui.theme.MediumPink
import com.example.datingapp.ui.utils.formatTime
import com.example.datingapp.ui.utils.getCurrentDateTime
import com.example.datingapp.viewmodel.MessageViewModel
import com.example.datingapp.viewmodel.PhotoViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChatScreen(
    interlocutorId: String,
    navController: NavController,
    chatViewModel: ChatViewModel,
    profileViewModel: ProfileViewModel,
    photoViewModel: PhotoViewModel,
    messageViewModel: MessageViewModel,
    currentUserId: String
) {
    var text by remember { mutableStateOf("") }
    val interlocutor = remember { mutableStateOf(UserEntity()) }
    val interlocutorPhoto = remember { mutableStateOf<Bitmap?>(null) }
    var currentChatId by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf<List<MessageEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        photoViewModel.getPhotoBitmap(
            idUser = interlocutorId,
            onSuccess = { bitmap ->
                interlocutorPhoto.value = bitmap
            },
            onFailure = { error ->
                Log.d("MyTag", "Error loading interlocutor photo: $error")
            }
        )

        profileViewModel.fetchUserFromFirestore(
            interlocutorId,
            onSuccess = { user ->
                interlocutor.value = user
            },
            onFailure = { error ->
                Log.d("MyTag", error)
            }
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
                    },
                    onFailure = { error ->
                        Log.d("MyTag", error)
                    }
                )
            },
            onFailure = { error ->
                Log.d("MyTag", error)
            }
        )
    }


    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 0.dp, end = 0.dp, top = 24.dp, bottom = 8.dp)
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
                        Row(modifier = Modifier.align(Alignment.CenterStart).padding(start = 70.dp)) {
                            interlocutorPhoto.value?.let { bitmap ->
                                Image(
                                    bitmap = Bitmap.createScaledBitmap(bitmap, 45, 45, false).asImageBitmap(),
                                    contentDescription = "likedUserPhoto",
                                    modifier = Modifier
                                        .size(45.dp)
                                        .clip(CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                modifier = Modifier.align(Alignment.CenterVertically),
                                text = interlocutor.value.firstName,
                                fontSize = 22.sp,
                                style = TextStyle(letterSpacing = 0.2.sp,),
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

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, bottom = 80.dp, top = 0.dp)
                    ) {
                        items(messages) { message ->
                            MessageCard(message, if (message.idUser == currentUserId) true else false)
                        }
                    }

                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextField(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(shape = RoundedCornerShape(24.dp)),
                        value = text,
                        placeholder = {
                            Text(
                                text = "Type Something...",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        },
                        textStyle = TextStyle(fontSize = 16.sp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = GrayBlue,
                            focusedContainerColor = GrayBlue,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        onValueChange = { text = it },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        modifier = Modifier
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
                                            }
                                        )
                                    },
                                    onFailure = { error ->
                                        Log.d("MyTag", error)
                                    }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (text.isEmpty()) GrayBlue else MediumPink),
                    ) {
                        Text(
                            text = ">",
                            color = if (text.isEmpty()) Color.Gray else Color.White,
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun MessageCard(
    message: MessageEntity,
    isFromCurrent: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = if (isFromCurrent) Arrangement.End else Arrangement.Start
    ) {
        if (isFromCurrent) {
            Text(
                text = formatTime(message.sendTime),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(end = 8.dp)
            )
        }

        Card(
            modifier = Modifier
                .weight(1f, false)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isFromCurrent) MediumPink else GrayBlue
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.messageText,
                    fontSize = 14.sp,
                    color = if (isFromCurrent) Color.White else Color.Black
                )
            }
        }

        if (!isFromCurrent) {
            Text(
                text = formatTime(message.sendTime),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(start = 8.dp)
            )
        }
    }
}

