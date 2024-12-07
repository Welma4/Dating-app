package com.example.datingapp.ui.utils

import android.content.ContentResolver
import android.net.Uri
import android.util.Base64

fun imageToBase64(uri: Uri, contentResolver: ContentResolver): String {
    val inputStream = contentResolver.openInputStream(uri)

    val bytes = inputStream?.readBytes()
    return bytes?.let {
        Base64.encodeToString(it, Base64.DEFAULT)
    } ?: ""
}