package com.example.datingapp.ui.screens

import ProfileViewModel
import VerticalSwipeFeed
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.datingapp.ui.components.NavigationMenu
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.datingapp.R
import com.example.datingapp.data.UserEntity
import com.example.datingapp.ui.components.FiltersDialog
import com.example.datingapp.ui.components.HomeHeaderSection
import com.example.datingapp.ui.components.LoadingState
import com.example.datingapp.ui.theme.MediumPink
import com.example.datingapp.viewmodel.GenderViewModel
import com.example.datingapp.viewmodel.LikeViewModel
import com.example.datingapp.viewmodel.PhotoViewModel
import com.example.datingapp.viewmodel.PreferencesViewModel
import com.google.firebase.auth.FirebaseAuth


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    photoViewModel: PhotoViewModel,
    preferencesViewModel: PreferencesViewModel,
    genderViewModel: GenderViewModel,
    likeViewModel: LikeViewModel,
    currentUserId: String
) {
    val usersList = remember { mutableStateOf<List<UserEntity>>(emptyList()) }
    val isFilterMenuVisible = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(true) }

    var genderIdPreference by remember { mutableStateOf("") }
    var ageRangePreference by remember { mutableStateOf(0 to 100) }


    LaunchedEffect(Unit, genderIdPreference, ageRangePreference) {
        isLoading.value = true
        preferencesViewModel.fetchPreferencesForUser(
            idUser = currentUserId,
            onSuccess = { preferencesEntity ->
                genderIdPreference = preferencesEntity.gender
                ageRangePreference = preferencesEntity.startAgeRange to preferencesEntity.endAgeRange

                profileViewModel.fetchFilteredUsers(
                    currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    startAgeRange = ageRangePreference.first,
                    endAgeRange = ageRangePreference.second,
                    gender = genderIdPreference,
                    onSuccess = { fetchedUsers ->
                        usersList.value = fetchedUsers
                        isLoading.value = false
                    },
                    onFailure = { error ->
                        Log.e("HomeScreen", "Error loading users: $error")
                        isLoading.value = false
                    }
                )
            },
            onFailure = { error ->
                Log.e("HomeScreen", "Error loading preferences: $error")
                isLoading.value = false
            }
        )
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = { NavigationMenu(navController) },
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                HomeHeaderSection(isFilterMenuVisible)

                if (isLoading.value) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(200.dp)
                                .align(Alignment.Center),
                            color = MediumPink
                        )
                    }
                } else {
                    if (usersList.value.isNotEmpty()) {
                        VerticalSwipeFeed(
                            items = usersList.value.map { it },
                            photoViewModel = photoViewModel,
                            likeViewModel = likeViewModel,
                            currentUserId = currentUserId,
                        )
                    } else {
                        Text("No users available")
                    }
                }
            }
        }

        FiltersDialog(
            preferencesViewModel = preferencesViewModel,
            genderViewModel = genderViewModel,
            isDialog = isFilterMenuVisible.value,
            onDialogChange = { isFilterMenuVisible.value = it },
            onGenderChange = { newGenderId ->
                genderIdPreference = newGenderId
                Log.d("MyTag", "Updated Gender: $newGenderId")
            },
            onAgeRangeChange = { newAgeRange ->
                ageRangePreference = newAgeRange
                Log.d("MyTag", "Updated Age Range: ${newAgeRange.first}, ${newAgeRange.second}")
            }
        )

    }
}




