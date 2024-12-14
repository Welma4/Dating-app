package com.example.datingapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.datingapp.data.StatusEntity
import com.google.firebase.firestore.FirebaseFirestore


class StatusViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _statuses = MutableLiveData<List<StatusEntity>>()
    val statuses: LiveData<List<StatusEntity>> get() = _statuses

    fun saveStatusesToFirestore() {
        val statusData = listOf(
            StatusEntity(statusName = "accepted"),
            StatusEntity(statusName = "waiting")
        )

        db.collection("status").get()
            .addOnSuccessListener { result ->
                val existingStatuses = result.mapNotNull { document ->
                    document.toObject(StatusEntity::class.java).statusName
                }

                val statusesToAdd = statusData.filter { status ->
                    status.statusName !in existingStatuses
                }

                for (status in statusesToAdd) {
                    db.collection("status").add(status)
                        .addOnSuccessListener { document ->
                            Log.d("MyTag", "Document saved with ID: ${document.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w("MyTag", "Error adding document", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("MyTag", "Error getting documents", e)
            }
    }

    fun getStatusesFromFirestore() {
        db.collection("status").get()
            .addOnSuccessListener { result ->
                val statusList = mutableListOf<StatusEntity>()
                for (document in result) {
                    document.toObject(StatusEntity::class.java)?.let { status ->
                        statusList.add(status)
                    }
                }
                _statuses.value = statusList
                Log.d("MyTag", "Statuses successfully retrieved!")
            }
            .addOnFailureListener { e ->
                Log.w("MyTag", "Error getting documents", e)
            }
    }

    fun getIdByStatusName(
        statusName: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("status")
            .whereEqualTo("statusName", statusName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    onSuccess(querySnapshot.documents.first().id)
                } else {
                    onFailure("Status not found")
                }
            }
            .addOnFailureListener { error ->
                onFailure(error.message ?: "Error fetching status")
            }
    }

    fun getStatusNameById(
        idStatus: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("status")
            .document(idStatus)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val statusName = documentSnapshot.getString("statusName")
                    if (statusName != null) {
                        onSuccess(statusName)
                    } else {
                        onFailure("Status name was not found")
                    }
                } else {
                    onFailure("Document with ID $idStatus does not exist")
                }
            }
            .addOnFailureListener { error ->
                onFailure(error.message ?: "Error fetching document")
            }
    }
}
