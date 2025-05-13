package com.example.datingapp.data

import com.example.datingapp.ui.utils.getCurrentTime

data class ChatEntity(
    val idFirstUser: String = "",
    val idSecondUser: String = "",
    val lastMessageId: String = "",
    val lastUpdateTime: String = getCurrentTime(),
)
