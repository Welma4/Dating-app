package com.example.datingapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class
    ],
    version = 1
)
abstract class MainDb : RoomDatabase() {

    abstract val userDao: UserDao

    companion object{
        fun createDatabase(context: Context): MainDb{
            return Room.databaseBuilder(
                context,
                MainDb::class.java,
                "app.db"
                ).build()
        }
    }
}