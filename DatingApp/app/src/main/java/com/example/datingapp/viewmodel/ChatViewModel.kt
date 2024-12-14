package com.example.datingapp.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.datingapp.data.ChatEntity
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
                        "lastUpdateTime" to System.currentTimeMillis()
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
        chatCol
            .whereEqualTo("idFirstUser", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val chats = querySnapshot.mapNotNull { document ->
                        document.toObject<ChatEntity>()
                    }
                    Log.d("MyTag", "Fetched chats: ${chats.size}")
                    _chatList.value = chats
                    onSuccess()
                } else {
                    Log.d("MyTag", "No chats found")
                    _chatList.value = emptyList()
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error fetching chats")
            }
    }

}