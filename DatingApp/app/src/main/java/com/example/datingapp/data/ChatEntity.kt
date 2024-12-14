package com.example.datingapp.data

data class ChatEntity(
    val idFirstUser: String = "",
    val idSecondUser: String = "",
    val lastMessageId: String = "",
    val lastUpdateTime: Long = 0L
)
