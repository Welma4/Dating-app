import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.datingapp.R
import com.example.datingapp.data.GenderEntity
import com.example.datingapp.data.PreferencesEntity
import com.example.datingapp.data.Routes
import com.example.datingapp.data.UserEntity
import com.example.datingapp.ui.components.BirthDateField
import com.example.datingapp.ui.components.GenderDropMenu
import com.example.datingapp.ui.components.LoginTextField
import com.example.datingapp.ui.components.PasswordField
import com.example.datingapp.ui.theme.poppinsFontFamily
import com.example.datingapp.ui.utils.bitmapToBase64
import com.example.datingapp.viewmodel.GenderViewModel
import com.example.datingapp.viewmodel.PhotoViewModel
import com.example.datingapp.viewmodel.PreferencesViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    photoViewModel: PhotoViewModel,
    genderViewModel: GenderViewModel,
    preferencesViewModel: PreferencesViewModel
) {

    val auth = Firebase.auth

    val genders by genderViewModel.genders.observeAsState(emptyList())

    var errorState by remember { mutableStateOf("") }
    var isFirstNameError by remember { mutableStateOf(false) }
    var isSecondNameError by remember { mutableStateOf(false) }
    var isBirthDateError by remember { mutableStateOf(false) }
    var isLocationError by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }

    var firstName by remember { mutableStateOf("") }
    var secondName by remember { mutableStateOf("") }
    var genderName by remember { mutableStateOf("Male") }
    var birthDate by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var genderId by remember { mutableStateOf("") }
    genderViewModel.getIdByGenderName(
        genderName,
        onSuccess = { result ->
            genderId = result
        },
        onFailure = { error ->
            Log.d("MyTag", error)
        }
    )

    val context = LocalContext.current
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            IconButton(
                onClick = { navController.navigate(Routes.Login) },
                modifier = Modifier.background(Color.Transparent)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Return to LoginScreen",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Sign Up",
                fontFamily = poppinsFontFamily,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(30.dp))

            Column(modifier = Modifier.padding(horizontal = 30.dp)) {
                LoginTextField(value = firstName, label = "First name", isFirstNameError) { firstName = it }

                LoginTextField(value = secondName, label = "Second name", isSecondNameError) { secondName = it }

                GenderDropMenu(options = genders) { selectedGender -> genderName = selectedGender }

                BirthDateField(birthDate = birthDate, isBirthDateError, onBirthDateChange = { birthDate = it }, navController)

                LoginTextField(value = location, label = "Location", isLocationError) { location = it }

                LoginTextField(value = email, label = "Email", isEmailError) { email = it }

                PasswordField(password = password, isPasswordError, onPasswordChange = { password = it })

                Spacer(modifier = Modifier.height(10.dp))

                if (errorState.isNotEmpty()) {
                    Text(
                        text = errorState,
                        fontFamily = poppinsFontFamily,
                        fontSize = 14.sp,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))

                Button(
                    onClick = {
                        isFirstNameError = firstName.isBlank()
                        isSecondNameError = secondName.isBlank()
                        isBirthDateError = birthDate.isBlank()
                        isLocationError = location.isBlank()
                        isEmailError = email.isBlank()
                        isPasswordError = password.isBlank() || (password.length < 6)

                        val isError: Boolean = (isFirstNameError || isSecondNameError || isBirthDateError || isLocationError || isEmailError || isPasswordError)

                        if (!isError) {
                            signUp(
                                profileViewModel,
                                photoViewModel,
                                preferencesViewModel,
                                auth,
                                email,
                                password,
                                firstName,
                                secondName,
                                genderId,
                                birthDate,
                                location,
                                context,
                                onSignUpSuccess = {
                                    navController.navigate(Routes.PhotoRequest)
                                },
                                onSignUpFailure = { error ->
                                    errorState = error
                                }
                            )
                        } else {
                            errorState = "Please fill all required fields!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAA3FEC)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .padding(bottom = 10.dp)
                ) {
                    Text(
                        text = "SIGN UP",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }

}




private fun signUp(
    profileViewModel: ProfileViewModel,
    photoViewModel: PhotoViewModel,
    preferencesViewModel: PreferencesViewModel,
    auth: FirebaseAuth,
    email: String,
    password: String,
    firstName: String,
    secondName: String,
    gender: String,
    birthDate: String,
    location: String,
    context: Context,
    onSignUpSuccess: () -> Unit,
    onSignUpFailure: (String) -> Unit
) {
    if (email.isBlank() || password.isBlank()) {
        onSignUpFailure("Email and password cannot be empty!")
        return
    }

    val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val birthDateAsDate: Date = try {
        dateFormatter.parse(birthDate)!!
    } catch (e: Exception) {
        onSignUpFailure("Invalid birth date format!")
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = task.result?.user?.uid ?: return@addOnCompleteListener
                val userEntity = UserEntity(
                    uid = uid,
                    firstName = firstName,
                    secondName = secondName,
                    emailAddress = email,
                    password = password,
                    location = location,
                    birthDate = birthDateAsDate,
                    gender = gender
                )
                profileViewModel.saveUserToFirestore(
                    uid,
                    userEntity,
                    onSuccess = {
                        val defaultImage = BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.default_icon
                        )
                        val photoBase64 = bitmapToBase64(defaultImage)
                        photoViewModel.savePhotoToFirestore(
                            idUser = uid,
                            photoBase64 = photoBase64,
                            onSuccess = {
                                preferencesViewModel.savePreferencesToFirestore(
                                    PreferencesEntity(
                                        idUser = uid,
                                        gender = maleGender,
                                        startAgeRange = 0,
                                        endAgeRange = 100
                                    ),
                                    onSuccess = {
                                        onSignUpSuccess()
                                    },
                                    onFailure = { error ->
                                        onSignUpFailure("User created, but preferences error: $error")
                                    }
                                )

                            },
                            onFailure = { error ->
                                onSignUpFailure("User created, but photo error: $error")
                            }
                        )

                    },
                    onFailure = { error ->
                        onSignUpFailure(error)
                    }
                )


            } else {
                onSignUpFailure(task.exception?.message ?: "Sign Up Error!")
            }
        }
        .addOnFailureListener { error ->
            onSignUpFailure(error.message ?: "Sign Up Error!")
        }
}

                                                                                                                                                                                                                  const val maleGender = "7QcWbM1utFFkcr0l808a"