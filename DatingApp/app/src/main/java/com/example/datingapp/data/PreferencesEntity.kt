package com.example.datingapp.data

data class PreferencesEntity(
    val idUser: String = "",
    val gender: String = "",
    val startAgeRange: Int = 0,
    val endAgeRange: Int = 100,
)
