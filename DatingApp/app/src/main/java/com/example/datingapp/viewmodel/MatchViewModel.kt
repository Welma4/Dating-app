package com.example.datingapp.viewmodel

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class MatchViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val statusViewModel = StatusViewModel()

    fun checkForMatchAndCreate(
        currentUserId: String,
        likedUserId: String,
        onMatchFound: () -> Unit,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val likeCol = db.collection("like")
        val matchCol = db.collection("match")
        val defaultStatusName = "waiting"

        statusViewModel.getIdByStatusName(
            defaultStatusName,
            onSuccess = { statusId ->
                matchCol
                    .whereIn("idFirstUser", listOf(currentUserId, likedUserId))
                    .whereIn("idSecondUser", listOf(currentUserId, likedUserId))
                    .get()
                    .addOnSuccessListener { matchSnapshot ->
                        if (matchSnapshot.isEmpty) {
                            likeCol
                                .whereEqualTo("idUser", likedUserId)
                                .whereEqualTo("idLikedUser", currentUserId)
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    if (!querySnapshot.isEmpty) {
                                        val matchData = hashMapOf(
                                            "idFirstUser" to currentUserId,
                                            "idSecondUser" to likedUserId,
                                            "status" to statusId
                                        )
                                        matchCol
                                            .add(matchData)
                                            .addOnSuccessListener {
                                                onMatchFound()
                                                onSuccess()
                                            }
                                            .addOnFailureListener { error ->
                                                onFailure(error.message ?: "Error creating match")
                                            }
                                    } else {
                                        onSuccess()
                                    }
                                }
                                .addOnFailureListener { error ->
                                    onFailure(error.message ?: "Error checking for mutual like")
                                }
                        } else {
                            Log.d("MyTag", "Match already exists")
                            onSuccess()
                        }
                    }
                    .addOnFailureListener { error ->
                        onFailure(error.message ?: "Error checking for existing match")
                    }
            },
            onFailure = { error ->
                onFailure(error)
            }
        )
    }

}