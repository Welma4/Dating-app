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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.datingapp.viewmodel.GenderViewModel
import com.example.datingapp.viewmodel.LikeViewModel
import com.example.datingapp.viewmodel.PhotoViewModel
import com.example.datingapp.viewmodel.PreferencesViewModel


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

    var genderIdPreference by remember { mutableStateOf("") }
    var ageRangePreference by remember { mutableStateOf(0 to 100) }


    LaunchedEffect(Unit, genderIdPreference, ageRangePreference) {
        preferencesViewModel.fetchPreferencesForUser(
            idUser = currentUserId,
            onSuccess = { preferencesEntity ->
                genderIdPreference = preferencesEntity.gender
                ageRangePreference = preferencesEntity.startAgeRange to preferencesEntity.endAgeRange

                profileViewModel.fetchFilteredUsers(
                    currentUserUid = currentUserId,
                    startAgeRange = ageRangePreference.first,
                    endAgeRange = ageRangePreference.second,
                    gender = genderIdPreference,
                    onSuccess = { fetchedUsers ->
                        usersList.value = fetchedUsers
                    },
                    onFailure = { error ->
                        Log.e("HomeScreen", "Error loading users: $error")
                    }
                )
            },
            onFailure = { error ->
                Log.e("HomeScreen", "Error loading preferences: $error")
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

                if (usersList.value.isNotEmpty()) {
                    VerticalSwipeFeed(
                        items = usersList.value.map { it },
                        photoViewModel = photoViewModel,
                        likeViewModel = likeViewModel,
                        currentUserId = currentUserId,
                    )
                } else {
                    LoadingState()
                }
            }
        }

        FiltersDialog(
            preferencesViewModel = preferencesViewModel,
            genderViewModel = genderViewModel,
            currentUserId = currentUserId,
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




