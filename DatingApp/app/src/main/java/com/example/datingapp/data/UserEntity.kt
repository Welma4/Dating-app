package com.example.datingapp.data

import java.util.Date

data class UserEntity(
    val uid: String = "",
    val firstName: String = "Loading...",
    val secondName: String = "Loading...",
    val emailAddress: String = "Loading...",
    val password: String = "",
    val location: String = "Loading...",
    val birthDate: Date? = Date(),
    val gender: String = "Loading...",
)
