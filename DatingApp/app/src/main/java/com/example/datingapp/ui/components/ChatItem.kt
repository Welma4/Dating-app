package com.example.datingapp.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.datingapp.data.ChatEntity
import com.example.datingapp.ui.utils.formatTime

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