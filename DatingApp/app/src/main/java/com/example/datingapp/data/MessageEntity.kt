package com.example.datingapp.data

import com.example.datingapp.ui.utils.getCurrentTime

data class MessageEntity(
    val idUser: String = "",
    val sendTime: String = getCurrentTime(),
    val idChat: String = "",
    val messageText: String = ""
)
