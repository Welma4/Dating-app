package com.example.datingapp.ui.screens

import ProfileViewModel
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.datingapp.R
import com.example.datingapp.data.Routes
import com.example.datingapp.ui.components.NavigationMenu
import com.example.datingapp.ui.theme.poppinsFontFamily
import com.example.datingapp.ui.utils.calculateAge
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.text.SimpleDateFormat
import java.util.Locale
import coil.compose.rememberAsyncImagePainter
import com.example.datingapp.ui.components.ProfileField
import com.example.datingapp.ui.utils.imageToBase64
import com.example.datingapp.viewmodel.GenderViewModel
import com.example.datingapp.viewmodel.PhotoViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    photoViewModel: PhotoViewModel,
    genderViewModel: GenderViewModel
) {

    val cr = LocalContext.current.contentResolver

    val auth = Firebase.auth
    val currentUser = FirebaseAuth.getInstance().currentUser

    var bitmapImage by remember { mutableStateOf<Bitmap?>(null) }
    val defaultImage = BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.default_user_photo)

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            profileViewModel.loadUser(user.uid)
            photoViewModel.getPhotoBitmap(
                user.uid,
                onSuccess = {
                    bitmapImage = it
                },
                onFailure = {
                    Log.d("MyLog", "Error fetching photo")
                    bitmapImage = defaultImage
                }
            )
        }
    }

    val user = profileViewModel.user.value

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isImageLoaded by remember { mutableStateOf(false) }
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
        isImageLoaded = (uri != null)
    }

    LaunchedEffect(isImageLoaded) {
        if (isImageLoaded) {
            photoViewModel.savePhotoToFirestore(
                currentUser!!.uid,
                imageToBase64(
                    selectedImageUri!!,
                    cr
                ),
                onSuccess = {
                    Log.d("MyLog", "Image uploaded successfully")
                    photoViewModel.getPhotoBitmap(
                        user.uid,
                        onSuccess = {
                            bitmapImage = it
                        },
                        onFailure = {
                            Log.d("MyLog", "Error fetching photo")
                        }
                    )
                },
                onFailure = {
                    Log.d("MyLog", "Image uploading error!")
                }
            )
        }
    }

    val age = calculateAge(profileViewModel.user.value.birthDate!!)
    val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val formattedDate = dateFormatter.format(profileViewModel.user.value.birthDate!!)

    val gradientColors = listOf(
        Color(0xFFA020F0),
        Color(0xFFBC7BE4)
    )

    var genderName by remember { mutableStateOf("") }
    genderViewModel.getGenderNameById(
        idGender = user.gender,
        onSuccess = { result ->
            genderName = result
        },
        onFailure = { error ->
            Log.d("MyTag", error)
        }
    )

    Scaffold(bottomBar = { NavigationMenu(navController) }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB09DFF))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(brush = Brush.horizontalGradient(colors = gradientColors)),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        text = "Profile",
                        textAlign = TextAlign.Center,
                        fontSize = 22.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(Color.White, CircleShape)
                    ) {

                        Image(
                            painter = rememberAsyncImagePainter(
                                model = bitmapImage
                            ),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            IconButton(
                                onClick = { imageLauncher.launch("image/*") },
                                modifier = Modifier
                                    .size(34.dp)
                            ) {
                                Image(
                                    modifier = Modifier.size(34.dp),
                                    painter = painterResource(id = R.drawable.ic_edit),
                                    contentDescription = "edit"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${user.firstName}, ${age}",
                        fontSize = 16.sp,
                        color = Color.White,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Account Settings",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Edit",
                        fontSize = 14.sp,
                        color = Color.Blue,
                        modifier = Modifier.clickable { navController.navigate(Routes.EditProfile) },
                    )
                }

                ProfileField(label = "First name", value = user.firstName)
                ProfileField(label = "Second name", value = user.secondName)
                ProfileField(label = "Date of birth", value = formattedDate)
                ProfileField(label = "Email", value = user.emailAddress)
                ProfileField(label = "Location", value = user.location)
                ProfileField(label = "Gender", value = genderName)

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { signOut(auth)
                        navController.navigate(Routes.Login) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAA3FEC)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp)
                        .padding(bottom = 10.dp)
                ) {
                    Text(
                        text = "SIGN OUT",
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

private fun signOut(auth: FirebaseAuth) {
    auth.signOut()
}




