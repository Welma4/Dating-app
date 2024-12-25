package com.example.datingapp.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.datingapp.data.ChatEntity
import com.example.datingapp.ui.utils.getCurrentTime
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class ChatViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _chatList = MutableLiveData<List<ChatEntity>>()
    val chatList: LiveData<List<ChatEntity>> get() = _chatList


    fun createChat(
        firstUserId: String,
        secondUserId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val chatCol = db.collection("chat")

        chatCol
            .whereIn("idFirstUser", listOf(firstUserId, secondUserId))
            .whereIn("idSecondUser", listOf(firstUserId, secondUserId))
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    val chatData = hashMapOf(
                        "idFirstUser" to firstUserId,
                        "idSecondUser" to secondUserId,
                        "lastMessageId" to "",
                        "lastUpdateTime" to getCurrentTime()
                    )
                    chatCol
                        .add(chatData)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onFailure(e.message ?: "Error adding new chat")
                        }
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error checking existing chat")
            }
    }

    fun fetchChatsForUser(
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val chatCol = db.collection("chat")
        val firstUserQuery = chatCol.whereEqualTo("idFirstUser", userId)
        val secondUserQuery = chatCol.whereEqualTo("idSecondUser", userId)

        firstUserQuery.get()
            .addOnSuccessListener { firstUserSnapshot ->
                secondUserQuery.get()
                    .addOnSuccessListener { secondUserSnapshot ->
                        val firstUserChats = firstUserSnapshot.documents.mapNotNull { it.toObject<ChatEntity>() }
                        val secondUserChats = secondUserSnapshot.documents.mapNotNull { it.toObject<ChatEntity>() }

                        val allChats = (firstUserChats + secondUserChats).distinctBy { it.hashCode() }

                        _chatList.value = allChats.sortedByDescending { it.lastUpdateTime }
                        onSuccess()
                    }
                    .addOnFailureListener { error ->
                        onFailure(error.message ?: "Error fetching second user chats")
                    }
            }
            .addOnFailureListener { error ->
                onFailure(error.message ?: "Error fetching first user chats")
            }
    }


}