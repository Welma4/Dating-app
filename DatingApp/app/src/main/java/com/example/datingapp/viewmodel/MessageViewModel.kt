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
        onFailure: (String) -> Unit,
    ) {
        val message = MessageEntity(
            idUser,
            getCurrentDateTime(),
            idChat,
            messageText,
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

    fun fetchMessagesForChatRealtime(
        chatId: String,
        onSuccess: (List<MessageEntity>) -> Unit,
        onFailure: (String) -> Unit,
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

    fun fetchMessageById(
        messageId: String,
        onSuccess: (MessageEntity) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        messageCol
            .document(messageId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.exists()) {
                    val message = querySnapshot.toObject(MessageEntity::class.java)
                    if (message != null) {
                        onSuccess(message)
                    } else {
                        onFailure("Message is null")
                    }
                }
            }
            .addOnFailureListener { error ->
                onFailure(error.message ?: "Error finding message document")
            }
    }
}
