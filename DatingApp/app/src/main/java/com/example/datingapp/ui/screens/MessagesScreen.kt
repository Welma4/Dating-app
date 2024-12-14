package com.example.datingapp.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.datingapp.data.ChatEntity
import com.example.datingapp.ui.components.NavigationMenu
import com.example.datingapp.viewmodel.ChatViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MessagesScreen(
    navController: NavController,
    chatViewModel: ChatViewModel,
    currentUserId: String
) {

    val chatList = chatViewModel.chatList.observeAsState(emptyList())

    LaunchedEffect(currentUserId) {
        chatViewModel.fetchChatsForUser(
            userId = currentUserId,
            onSuccess = {},
            onFailure = { error -> Log.d("MyTag", error) }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { NavigationMenu(navController) }
    ) {
        if (chatList.value.isNotEmpty()) {
            ChatList(chatList.value)
        } else {
            Text("No chats available.", modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun ChatList(chatList: List<ChatEntity>) {
    LazyColumn(
        modifier = Modifier.padding(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(chatList) { chat ->
            ChatItem(chat)
        }
    }
}

@Composable
fun ChatItem(chat: ChatEntity) {
    // Карточка для каждого чата
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // idSecondUser - Верхняя строка
            Text(
                text = "Chat with: ${chat.idSecondUser}",
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )

            // lastMessage - под idSecondUser
            Text(
                text = chat.lastMessageId.ifEmpty { "No messages yet" },
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Last update: ${chat.lastUpdateTime}",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
