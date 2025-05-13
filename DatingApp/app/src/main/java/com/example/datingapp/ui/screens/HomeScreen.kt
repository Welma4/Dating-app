package com.example.datingapp.ui.screens

import VerticalSwipeFeed
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.datingapp.data.UserEntity
import com.example.datingapp.ui.components.FiltersDialog
import com.example.datingapp.ui.components.HomeHeaderSection
import com.example.datingapp.ui.components.MatchDialog
import com.example.datingapp.ui.components.NavigationMenu
import com.example.datingapp.ui.theme.MediumPink
import com.example.datingapp.viewmodel.ChatViewModel
import com.example.datingapp.viewmodel.GenderViewModel
import com.example.datingapp.viewmodel.LikeViewModel
import com.example.datingapp.viewmodel.MatchViewModel
import com.example.datingapp.viewmodel.PhotoViewModel
import com.example.datingapp.viewmodel.PreferencesViewModel
import com.example.datingapp.viewmodel.ProfileViewModel
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
    matchViewModel: MatchViewModel,
    chatViewModel: ChatViewModel,
    currentUserId: String,
) {
    val usersList = remember { mutableStateOf<List<UserEntity>>(emptyList()) }
    val isFilterMenuVisible = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(true) }

    var genderIdPreference by remember { mutableStateOf("") }
    var ageRangePreference by remember { mutableStateOf(0 to 100) }

    val showMatchDialog = remember { mutableStateOf(false) }
    val matchedUserId = remember { mutableStateOf("") }

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
                    },
                )
            },
            onFailure = { error ->
                Log.e("HomeScreen", "Error loading preferences: $error")
                isLoading.value = false
            },
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
                            modifier =
                            Modifier
                                .size(200.dp)
                                .align(Alignment.Center),
                            color = MediumPink,
                        )
                    }
                } else {
                    if (usersList.value.isNotEmpty()) {
                        VerticalSwipeFeed(
                            items = usersList.value.map { it },
                            photoViewModel = photoViewModel,
                            likeViewModel = likeViewModel,
                            matchViewModel = matchViewModel,
                            chatViewModel = chatViewModel,
                            currentUserId = currentUserId,
                            onMatch = { id ->
                                matchedUserId.value = id
                                showMatchDialog.value = true
                            },
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
            },
            onAgeRangeChange = { newAgeRange ->
                ageRangePreference = newAgeRange
            },
        )
        if (showMatchDialog.value) {
            MatchDialog(
                navController = navController,
                currentUserId = currentUserId,
                likedUserId = matchedUserId.value,
                onDismiss = { showMatchDialog.value = false },
                photoViewModel = photoViewModel,
                profileViewModel = profileViewModel,
            )
        }
    }
}
