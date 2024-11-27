package com.example.datingapp.ui.screens

import ProfileViewModel
import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.datingapp.data.Routes
import com.example.datingapp.data.UserEntity
import com.example.datingapp.ui.components.EditableTextField
import com.example.datingapp.ui.components.GenderMenu
import com.example.datingapp.ui.theme.poppinsFontFamily
import com.example.datingapp.viewmodel.GenderViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun EditProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    genderViewModel: GenderViewModel,
    onSave: (UserEntity) -> Unit
) {

    val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    var firstName by remember { mutableStateOf(profileViewModel.user.value.firstName) }
    var secondName by remember { mutableStateOf(profileViewModel.user.value.secondName) }
    var birthDate by remember { mutableStateOf(dateFormatter.format(profileViewModel.user.value.birthDate!!)) }
    var location by remember { mutableStateOf(profileViewModel.user.value.location) }
    var gender by remember { mutableStateOf(profileViewModel.user.value.gender.toString()) }

    LaunchedEffect(Unit) {
        genderViewModel.getGendersFromFirestore()
    }
    val genders by genderViewModel.genders.observeAsState(emptyList())

    val calendar = remember { Calendar.getInstance() }
    var selectedDate by remember { mutableStateOf(profileViewModel.user.value.birthDate ?: Date()) }

    val datePickerDialog = DatePickerDialog(
        navController.context, { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.time
            birthDate = dateFormatter.format(selectedDate)
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
                onClick = { navController.navigate(Routes.Profile) },
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

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Account Settings",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(10.dp))

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

        GenderMenu(options = genders, selectedGender = gender) { selectedGender -> gender = selectedGender }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val updatedUser = UserEntity(
                    uid = profileViewModel.user.value.uid,
                    firstName = firstName,
                    secondName = secondName,
                    emailAddress = profileViewModel.user.value.emailAddress,
                    password = profileViewModel.user.value.password,
                    location = location,
                    birthDate = selectedDate,
                    gender = gender
                )
                onSave(updatedUser)
                navController.navigate(Routes.Profile)
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

