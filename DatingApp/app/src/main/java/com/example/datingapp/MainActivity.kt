package com.example.datingapp

import ProfileViewModel
import android.app.DatePickerDialog
import android.health.connect.datatypes.HeightRecord
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.datingapp.navigation.DatingApp
import com.example.datingapp.ui.theme.DatingAppTheme
import com.example.datingapp.ui.theme.poppinsFontFamily
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Outline
import com.example.datingapp.data.UserEntity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.sql.RowSetWriter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DatingAppTheme {
                DatingApp()
            }
        }
    }
}

@Composable
fun GradientBackground() {
    val gradientColors = listOf(
        Color(0xFFBC7BE4),
        Color(0xFFA020F0)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = gradientColors))
    )
}


@Composable
fun LoginScreen(
    navController: NavController,
) {

    MaterialTheme {
        GradientBackground()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "login_logo",
            modifier = Modifier
                .padding(top = 80.dp)
                .size(250.dp)
        )
        Spacer(modifier = Modifier.weight(1f))

        Column {
            Text(
                text = "Find your partner in life",
                style = TextStyle(
                    fontSize = 46.sp,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "We created to bring together amazing singles who want to find love, laughter and happily even after!",
                style = TextStyle(fontSize = 18.sp, color = Color.White),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = poppinsFontFamily,
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Column {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable { },
                colors = ButtonDefaults.buttonColors(Color.White),
                onClick = { navController.navigate("ProfileScreen") },
                border = BorderStroke(2.dp, Color(0xFFEBEAEC))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Google logo",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Continue with Google",
                        style = TextStyle(fontSize = 18.sp, color = Color.Black),
                        fontFamily = poppinsFontFamily,
                    )
                }

            }

            Spacer(modifier = Modifier.height(25.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable { },
                colors = ButtonDefaults.buttonColors(Color.White),
                onClick = { navController.navigate("SignInScreen") },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.email_logo),
                        contentDescription = "Email logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Continue with email",
                        style = TextStyle(fontSize = 18.sp, color = Color.Black),
                        fontFamily = poppinsFontFamily,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Don't have account? ",
                fontFamily = poppinsFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
            Text(
                modifier = Modifier.clickable { navController.navigate("SignUpScreen") },
                text = "Sign up",
                fontFamily = poppinsFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {

    val age = calculateAge(viewModel.user.value.birthDate)
    val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val formattedDate = dateFormatter.format(viewModel.user.value.birthDate)

    val gradientColors = listOf(
        Color(0xFFA020F0),
        Color(0xFFBC7BE4)
    )
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
                    text = "${viewModel.user.value.firstName}, ${age}",
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
                    modifier = Modifier.clickable { navController.navigate("EditProfileScreen") },
                    fontFamily = poppinsFontFamily
                )
            }

            ProfileField(label = "First name", value = viewModel.user.value.firstName)
            ProfileField(label = "Second name", value = viewModel.user.value.secondName)
            ProfileField(label = "Date of birth", value = formattedDate)
            ProfileField(label = "Email", value = viewModel.user.value.emailAddress)
            ProfileField(label = "Location", value = viewModel.user.value.location)
            ProfileField(label = "Gender", viewModel.user.value.gender)

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
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

@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel,
    onSave: (UserEntity) -> Unit
) {
    var firstName by remember { mutableStateOf(viewModel.user.value.firstName) }
    var secondName by remember { mutableStateOf(viewModel.user.value.secondName) }
    val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    var birthDate by remember { mutableStateOf(dateFormatter.format(viewModel.user.value.birthDate)) }
    var location by remember { mutableStateOf(viewModel.user.value.location) }
    var gender by remember { mutableStateOf(viewModel.user.value.gender.toString()) }


    val calendar = remember { Calendar.getInstance() }
    var selectedDate by remember { mutableStateOf(viewModel.user.value.birthDate) }

    val datePickerDialog = DatePickerDialog(
        navController.context, { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.time // Обновляем дату
            birthDate = dateFormatter.format(selectedDate) // Форматируем дату в нужный формат
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
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
                onClick = { navController.navigate("ProfileScreen") },
                modifier = Modifier.background(Color.Transparent)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Return to ProfileScreen",
                    modifier = Modifier.size(30.dp)
                )
            }
            Text(
                text = "Edit Profile",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        EditableTextField(
            fieldValue = firstName,
            onValueChange = { firstName = it },
            fieldName = "First name"
        )
        EditableTextField(
            fieldValue = secondName,
            onValueChange = { secondName = it },
            fieldName = "Second name"
        )
        OutlinedTextField(
            modifier = Modifier
                .clickable { datePickerDialog.show() }
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            value = birthDate,
            onValueChange = { birthDate = it },
            label = { Text("Date of birth") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select date")
                }
            },
        )
        EditableTextField(
            fieldValue = location,
            onValueChange = { location = it },
            fieldName = "Location"
        )

        GenderMenu(onValueChange = {gender = it})

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val updatedUser = UserEntity(
                    firstName = firstName,
                    secondName = secondName,
                    birthDate = selectedDate,
                    location = location,
                    gender = gender
                )
                onSave(updatedUser)
                navController.navigate("ProfileScreen")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAA3FEC)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Save",
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun EditableTextField(
    fieldValue: String,
    onValueChange: (String) -> Unit,
    fieldName: String
) {
    OutlinedTextField(
        value = fieldValue,
        onValueChange = onValueChange,
        label = { Text(fieldName) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderMenu(onValueChange: (String) -> Unit) {
    val options = listOf("Male", "Female")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = {},
            label = { Text("Gender") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(text = selectionOption) },
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                        onValueChange(selectedOptionText)
                    }
                )
            }
        }
    }
}

fun calculateAge(birthDate: Date): Int {
    val currentCalendar = Calendar.getInstance()
    val birthCalendar = Calendar.getInstance()
    birthCalendar.time = birthDate

    var age = currentCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

    if (currentCalendar.get(Calendar.MONTH) < birthCalendar.get(Calendar.MONTH) ||
        (currentCalendar.get(Calendar.MONTH) == birthCalendar.get(Calendar.MONTH) &&
                currentCalendar.get(Calendar.DAY_OF_MONTH) < birthCalendar.get(Calendar.DAY_OF_MONTH))) {
        age--
    }

    return age
}