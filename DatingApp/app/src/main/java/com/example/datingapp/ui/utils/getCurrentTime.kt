package com.example.datingapp.ui.utils

import java.text.SimpleDateFormat
import java.util.Date

fun getCurrentTime(): String {
    val dateFormat = SimpleDateFormat("HH:mm")
    return dateFormat.format(Date())
}
