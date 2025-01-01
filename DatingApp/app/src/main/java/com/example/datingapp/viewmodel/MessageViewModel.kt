package com.example.datingapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.datingapp.data.MessageEntity
import com.example.datingapp.ui.utils.getCurrentDateTime
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class MessageViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val messageCol = db.collection("message")

    fun saveMessageToFirestore(
        idUser: String,
        idChat: String,
        messageText: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val message = MessageEntity(
            idUser,
            getCurrentDateTime(),
            idChat,
            messageText
        )
        messageCol
            .add(message)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error saving message")
            }
    }

    fun fetchMessagesForChat(
        idChat: String,
        onSuccess: (List<MessageEntity>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        messageCol
            .whereEqualTo("idChat", idChat)
            .orderBy("sendTime")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val messages = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(MessageEntity::class.java)
                }
                onSuccess(messages)
            }
            .addOnFailureListener { error ->
                onFailure(("FETCH MES " + error.message) ?: "Error fetching messages")
            }
    }

    fun fetchMessagesForChatRealtime(
        chatId: String,
        onSuccess: (List<MessageEntity>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        messageCol
            .whereEqualTo("idChat", chatId)
            .orderBy("sendTime")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    onFailure(error.message ?: "Error fetching messages")
                    return@addSnapshotListener
                }
                if (snapshots != null && !snapshots.isEmpty) {
                    val messages = snapshots.documents.mapNotNull { it.toObject(MessageEntity::class.java) }
                    onSuccess(messages)
                }
            }
    }
}