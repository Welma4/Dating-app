import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.datingapp.data.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {
    var user = mutableStateOf(UserEntity())
        private set

    fun updateUser(updatedUser: UserEntity) {
        user.value = updatedUser
    }

    fun saveUserToFirestore(
        uid: String,
        userEntity: UserEntity,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
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
        val db = FirebaseFirestore.getInstance()
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
        val db = FirebaseFirestore.getInstance()
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

}
