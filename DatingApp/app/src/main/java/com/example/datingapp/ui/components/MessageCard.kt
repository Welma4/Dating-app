package com.example.datingapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datingapp.data.MessageEntity
import com.example.datingapp.ui.theme.GrayBlue
import com.example.datingapp.ui.theme.MediumPink
import com.example.datingapp.ui.utils.formatTimeToHHmm
import com.example.datingapp.ui.utils.formatTimeToMdHm

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
                text = formatTimeToHHmm(message.sendTime),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .align(Alignment.Bottom)
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
                text = formatTimeToHHmm(message.sendTime),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(start = 8.dp)
            )
        }
    }
}
