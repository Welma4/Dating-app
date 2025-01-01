package com.example.datingapp.ui.utils

import java.text.SimpleDateFormat
import java.util.Date

fun getCurrentDateTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return dateFormat.format(Date())
}
