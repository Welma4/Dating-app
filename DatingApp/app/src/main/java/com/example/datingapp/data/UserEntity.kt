package com.example.datingapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "USER",
//    foreignKeys = [
//        ForeignKey(
//            entity = GenderEntity::class,
//            parentColumns = ["id_gender"],
//            childColumns = ["id_gender"],
//            onDelete = ForeignKey.CASCADE
//        )
//    ],
    indices = [
        Index(value = ["id_gender"])
    ]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_user")
    val idUser: Int? = null,
    @ColumnInfo(name = "first_name")
    val firstName: String,
    @ColumnInfo(name = "second_name")
    val secondName: String,
    @ColumnInfo(name = "email_address")
    val emailAddress: String,
    @ColumnInfo(name = "location")
    val location: String,
    @ColumnInfo(name = "id_gender")
    val idGender: Int
)
