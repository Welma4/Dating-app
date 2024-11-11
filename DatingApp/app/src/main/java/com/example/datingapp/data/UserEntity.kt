package com.example.datingapp.data

import java.util.Calendar
import java.util.Date

data class UserEntity(
    val firstName: String = "First name",
    val secondName: String = "Second name",
    val emailAddress: String = "example@gmail.com",
    val password: String = "example",
    val location: String = "City",
    val birthDate: Date = Calendar.getInstance().apply { set(1990, Calendar.JANUARY, 1)}.time,
    val gender: String = "Female",
)
