package com.example.datingapp.ui.utils

import java.util.Calendar
import java.util.Date

fun calculateAge(birthDate: Date): Int {
    val currentCalendar = Calendar.getInstance()
    val birthCalendar = Calendar.getInstance()
    birthCalendar.time = birthDate

    var age = currentCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

    if (currentCalendar.get(Calendar.MONTH) < birthCalendar.get(Calendar.MONTH) ||
        (currentCalendar.get(Calendar.MONTH) == birthCalendar.get(Calendar.MONTH) &&
                currentCalendar.get(Calendar.DAY_OF_MONTH) < birthCalendar.get(Calendar.DAY_OF_MONTH))) {
        age--
    }

    return age
}