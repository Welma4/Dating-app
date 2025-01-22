package com.example.datingapp.ui.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun formatTimeToMdHm(dateTime: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MM.dd  HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateTime)
        date?.let { outputFormat.format(it) } ?: dateTime
    } catch (e: Exception) {
        dateTime
    }
}
