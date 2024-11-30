package com.example.datingapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.datingapp.data.LikeEntity
import com.google.firebase.firestore.FirebaseFirestore

class LikeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    fun saveLikeToFirestore(
        likeEntity: LikeEntity,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val likeCol = db.collection("like")

        likeCol
            .whereEqualTo("idUser", likeEntity.idUser)
            .whereEqualTo("idLikedUser", likeEntity.idLikedUser)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    likeCol
                        .add(likeEntity)
                        .addOnFailureListener() {
                            onSuccess()
                        }
                        .addOnFailureListener() { error ->
                            onFailure(error.message ?: "Error adding like")
                        }
                }
            }
            .addOnFailureListener { error ->
                onFailure(error.message ?: "Error checking existing like")
            }
    }

}