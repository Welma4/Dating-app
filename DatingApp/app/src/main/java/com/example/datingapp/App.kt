package com.example.datingapp

import android.app.Application
import com.example.datingapp.data.MainDb

class App : Application() {
    val database by lazy { MainDb.createDatabase(this) }
}