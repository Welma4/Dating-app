package com.example.datingapp.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.datingapp.data.ChatEntity

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