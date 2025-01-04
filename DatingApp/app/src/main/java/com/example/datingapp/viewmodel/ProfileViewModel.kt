import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.datingapp.data.UserEntity
import com.example.datingapp.ui.utils.calculateAge
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileViewModel : ViewModel() {
    var user = mutableStateOf(UserEntity())
        private set

    fun updateUser(updatedUser: UserEntity) {
        user.value = updatedUser
    }

    private val db = FirebaseFirestore.getInstance()

    fun saveUserToFirestore(
        uid: String,
        userEntity: UserEntity,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("user").document(uid)
            .set(userEntity)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error saving user")
            }
    }

    fun fetchUserFromFirestore(
        uid: String,
        onSuccess: (UserEntity) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("user").document(uid)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(UserEntity::class.java)
                if (user != null) {
                    onSuccess(user)
                } else {
                    onFailure("User not found")
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error fetching user")
            }
    }

    fun loadUser(uid: String) {
        fetchUserFromFirestore(
            uid = uid,
            onSuccess = { fetchedUser ->
                updateUser(fetchedUser)
            },
            onFailure = { error ->
                Log.e("ProfileViewModel", "Failed to load user: $error")
            }
        )
    }

    fun fetchUsersExceptCurrent(
        currentUserUid: String,
        onSuccess: (List<UserEntity>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("user")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val users = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(UserEntity::class.java)
                }.filter { it.uid != currentUserUid }
                onSuccess(users)
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Error fetching users")
            }
    }

    fun fetchFilteredUsers(
        currentUserUid: String,
        startAgeRange: Int,
        endAgeRange: Int,
        gender: String,
        onSuccess: (List<UserEntity>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("user")
            .get()
            .addOnSuccessListener { result ->
                val filteredUsers = result.documents.mapNotNull { document ->
                    val user = document.toObject(UserEntity::class.java)
                    user?.let {
                        val age = it.birthDate?.let { birthDate -> calculateAge(birthDate) }
                        if ((age in startAgeRange..endAgeRange) && (it.uid != currentUserUid) && (it.gender == gender)) {
                            user
                        } else {
                            null
                        }
                    }
                }
                onSuccess(filteredUsers)
            }
            .addOnFailureListener { error ->
                onFailure(error.message ?: "Failed to get filtered by gender users")
            }
    }

    suspend fun deleteUser(userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        val collections = listOf(
            "chat" to listOf("idFirstUser", "idSecondUser"),
            "like" to listOf("idLikedUser", "idUser"),
            "match" to listOf("idFirstUser", "idSecondUser"),
            "message" to listOf("idUser"),
            "photo" to listOf("idUser"),
            "preferences" to listOf("idUser"),
            "user" to listOf()
        )

        try {
            withContext(Dispatchers.IO) {
                db.runTransaction { transaction ->
                    for ((collectionName, fieldNames) in collections) {
                        if (collectionName == "user") {
                            val userRef = db.collection("user").document(userId)
                            transaction.delete(userRef)
                        } else {
                            for (fieldName in fieldNames) {
                                val documents = runBlocking {
                                    db.collection(collectionName)
                                        .whereEqualTo(fieldName, userId)
                                        .get()
                                        .await()
                                }

                                for (document in documents) {
                                    val docRef = db.collection(collectionName).document(document.id)
                                    transaction.delete(docRef)
                                }
                            }
                        }
                    }
                    null
                }.addOnSuccessListener {
                    onSuccess()
                }.addOnFailureListener { e ->
                    onFailure(e)
                }
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

}
