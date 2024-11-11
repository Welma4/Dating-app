import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.datingapp.data.UserEntity

class ProfileViewModel : ViewModel() {
    var user = mutableStateOf(UserEntity())
        private set

    fun updateUser(updatedUser: UserEntity) {
        user.value = updatedUser
    }
}
