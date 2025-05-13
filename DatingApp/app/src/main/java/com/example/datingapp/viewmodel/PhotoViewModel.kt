package com.example.datingapp.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.ViewModel
import com.example.datingapp.data.PhotoEntity
import com.google.firebase.firestore.FirebaseFirestore

class PhotoViewModel : ViewModel() {

    fun savePhotoToFirestore(
        idUser: String,
        photoBase64: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val db = FirebaseFirestore.getInstance()
        val photoEntity = PhotoEntity(
            idUser = idUser,
            photoPath = photoBase64,
        )

        db.collection("photo")
            .whereEqualTo("idUser", idUser)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    val documentId = querySnapshot.documents.first().id
                    db.collection("photo")
                        .document(documentId)
                        .update("photoPath", photoBase64)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onFailure(e.message ?: "Error updating photo") }
                } else {
                    db.collection("photo")
                        .add(photoEntity)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onFailure(e.message ?: "Error saving photo") }
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error checking existing photo")
            }
    }

    fun fetchPhotoFromFirestore(
        idUser: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("photo")
            .whereEqualTo("idUser", idUser)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val photoEntity = querySnapshot.documents
                    .firstOrNull()
                    ?.toObject(PhotoEntity::class.java)

                if (photoEntity != null) {
                    onSuccess(photoEntity.photoPath)
                } else {
                    onFailure("No photo found for user")
                }
            }
            .addOnFailureListener { e -> onFailure(e.message ?: "Error fetching photo") }
    }

    fun getPhotoBitmap(idUser: String, onSuccess: (Bitmap) -> Unit, onFailure: (String) -> Unit) {
        fetchPhotoFromFirestore(
            idUser,
            onSuccess = { base64Photo ->
                try {
                    val decodedBytes = Base64.decode(base64Photo, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    onSuccess(bitmap)
                } catch (e: Exception) {
                    onFailure("Error decoding photo")
                }
            },
            onFailure = {
                onFailure("No photo found")
            },
        )
    }
}
