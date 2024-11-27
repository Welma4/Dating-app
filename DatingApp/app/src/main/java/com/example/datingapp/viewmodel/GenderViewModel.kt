package com.example.datingapp.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.datingapp.data.GenderEntity
import com.google.firebase.firestore.FirebaseFirestore

class GenderViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _genders = MutableLiveData<List<GenderEntity>>()
    val genders: LiveData<List<GenderEntity>> get() = _genders

    fun saveGendersToFirestore() {
        val genderData = listOf(
            GenderEntity(genderName = "Male"),
            GenderEntity(genderName = "Female")
        )

        db.collection("gender").get()
            .addOnSuccessListener { result ->
                val existingGenders = result.mapNotNull { document ->
                    document.toObject(GenderEntity::class.java).genderName
                }

                val gendersToAdd = genderData.filter { gender ->
                    gender.genderName !in existingGenders
                }

                for (gender in gendersToAdd) {
                    db.collection("gender").add(gender)
                        .addOnSuccessListener { document ->
                            Log.d(TAG, "Document saved with ID: ${document.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting documents", e)
            }
    }

    fun getGendersFromFirestore() {
        db.collection("gender").get()
            .addOnSuccessListener { result ->
                val genderList = mutableListOf<GenderEntity>()
                for (document in result) {
                    document.toObject(GenderEntity::class.java)?.let { gender ->
                        genderList.add(gender)
                    }
                }
                _genders.value = genderList
                Log.d(TAG, "Genders successfully retrieved!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting documents", e)
            }
    }

    fun getIdByGenderName(
        genderName: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("gender")
            .whereEqualTo("genderName", genderName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    onSuccess(querySnapshot.documents.first().id)
                } else {
                    onFailure("Gender not found")
                }
            }
            .addOnFailureListener { error ->
                onFailure(error.message ?: "Error fetching gender")
            }
    }

    fun getGenderNameById(
        idGender: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        Log.d("MyTag", idGender)
        db.collection("gender")
            .document(idGender)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val genderName = documentSnapshot.getString("genderName")
                    if (genderName != null) {
                        onSuccess(genderName)
                    } else {
                        onFailure("Gender name was not found")
                    }
                } else {
                    onFailure("Document with ID $idGender does not exist")
                }
            }
            .addOnFailureListener { error ->
                onFailure(error.message ?: "Error fetching document")
            }
    }
}
