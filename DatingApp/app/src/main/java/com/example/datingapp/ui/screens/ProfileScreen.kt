package com.example.datingapp.ui.screens

import ProfileViewModel
import android.annotation.SuppressLint
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {

    val auth = Firebase.auth
    val currentUser = FirebaseAuth.getInstance().currentUser
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            viewModel.loadUser(user.uid)
        }
    }
    val user = viewModel.user.value

    val age = calculateAge(viewModel.user.value.birthDate!!)
    val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val formattedDate = dateFormatter.format(viewModel.user.value.birthDate!!)

    val gradientColors = listOf(
        Color(0xFFA020F0),
        Color(0xFFBC7BE4)
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
                    Spacer(modifier = Modifier.height(60.dp))
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.White, CircleShape)
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.osel2),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${user.firstName}, ${age}",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontFamily = poppinsFontFamily
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
                        fontFamily = poppinsFontFamily
                    )
                    Text(
                        text = "Edit",
                        fontSize = 14.sp,
                        color = Color.Blue,
                        modifier = Modifier.clickable { navController.navigate(Routes.EditProfile) },
                        fontFamily = poppinsFontFamily
                    )
                }

                ProfileField(label = "First name", value = user.firstName)
                ProfileField(label = "Second name", value = user.secondName)
                ProfileField(label = "Date of birth", value = formattedDate)
                ProfileField(label = "Email", value = user.emailAddress)
                ProfileField(label = "Location", value = user.location)
                ProfileField(label = "Gender", user.gender)

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 3.dp),
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = poppinsFontFamily
            ),
            label = {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontFamily = poppinsFontFamily
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedLabelColor = Color.Gray,
                focusedLabelColor = Color.Gray,
                disabledTextColor = Color.Black,
                disabledBorderColor = Color.Gray
            )
        )
    }
}

private fun signOut(auth: FirebaseAuth) {
    auth.signOut()
}


