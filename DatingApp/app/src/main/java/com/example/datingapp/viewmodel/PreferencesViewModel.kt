package com.example.datingapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.datingapp.data.PreferencesEntity
import com.google.firebase.firestore.FirebaseFirestore

class PreferencesViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _preferences = MutableLiveData<PreferencesEntity>()
    val preferences: LiveData<PreferencesEntity> get() = _preferences

    fun savePreferencesToFirestore(
        preferencesEntity: PreferencesEntity,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val prefCol = db.collection("preferences")

        prefCol
            .whereEqualTo("idUser", preferencesEntity.idUser)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    val documentId = querySnapshot.documents.first().id
                    prefCol
                        .document(documentId)
                        .set(preferencesEntity)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onFailure(e.message ?: "Error updating preferences") }
                } else {
                    prefCol
                        .add(preferencesEntity)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onFailure(e.message ?: "Error adding preferences") }
                }
            }.addOnFailureListener { e ->
                onFailure(e.message ?: "Error checking existing preferences")
            }
    }

    fun fetchPreferencesForUser(
        idUser: String,
        onSuccess: (PreferencesEntity) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val prefCol = db.collection("preferences")

        prefCol
            .whereEqualTo("idUser", idUser)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    val document = querySnapshot.documents.first()
                    val preferences = document.toObject(PreferencesEntity::class.java)
                    if (preferences != null) {
                        _preferences!!.value = preferences
                        onSuccess(preferences)
                    } else {
                        onFailure("Preferences not found")
                    }
                } else {
                    onFailure("Preferences not found")
                }
            }.addOnFailureListener { e ->
                onFailure(e.message ?: "Error fetching preferences")
            }
    }
}
