package com.example.datingapp.navigation

import androidx.compose.runtime.Composable
import ProfileViewModel
import SignUpScreen
import android.util.Log
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.datingapp.data.Routes
import com.example.datingapp.ui.screens.EditProfileScreen
import com.example.datingapp.ui.screens.HomeScreen
import com.example.datingapp.ui.screens.LikeScreen
import com.example.datingapp.ui.screens.LoginScreen
import com.example.datingapp.ui.screens.MessagesScreen
import com.example.datingapp.ui.screens.PhotoRequestScreen
import com.example.datingapp.ui.screens.ProfileScreen
import com.example.datingapp.ui.screens.SignInScreen
import com.example.datingapp.viewmodel.ChatViewModel
import com.example.datingapp.viewmodel.GenderViewModel
import com.example.datingapp.viewmodel.LikeViewModel
import com.example.datingapp.viewmodel.MatchViewModel
import com.example.datingapp.viewmodel.PhotoViewModel
import com.example.datingapp.viewmodel.PreferencesViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

@Composable
fun DatingApp() {
    val navController = rememberNavController()
    val profileViewModel: ProfileViewModel = viewModel()
    val photoViewModel: PhotoViewModel = viewModel()
    val preferencesViewModel: PreferencesViewModel = viewModel()
    val genderViewModel: GenderViewModel = viewModel()
    LaunchedEffect(Unit) {
        genderViewModel.getGendersFromFirestore()
    }
    val likeViewModel: LikeViewModel = viewModel()
    val matchViewModel: MatchViewModel = viewModel()
    val chatViewModel: ChatViewModel = viewModel()
    val auth = Firebase.auth

    var currentUserId by remember { mutableStateOf(auth.currentUser?.uid ?: "")  }
    DisposableEffect(Unit) {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            currentUserId = firebaseAuth.currentUser?.uid ?: ""
            Log.d("MyTag", "Updated currentUserId: $currentUserId")
        }
        auth.addAuthStateListener(authStateListener)

        onDispose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    NavHost(navController, startDestination = if (auth.currentUser != null) Routes.Home else Routes.Login) {

        composable(Routes.SignUp) {
            SignUpScreen(
                navController,
                profileViewModel,
                photoViewModel,
                genderViewModel,
                preferencesViewModel
            )
        }

        composable(Routes.SignIn) {
            SignInScreen(navController)
        }

        composable(Routes.Login) {
            LoginScreen(navController)
        }

        composable(Routes.Home) {
            HomeScreen(
                navController,
                profileViewModel,
                photoViewModel,
                preferencesViewModel,
                genderViewModel,
                likeViewModel,
                matchViewModel,
                chatViewModel,
                currentUserId
            )
        }

        composable(Routes.Profile) {
            ProfileScreen(
                navController,
                profileViewModel,
                photoViewModel,
                genderViewModel
            )
        }
        composable(Routes.EditProfile) {
            EditProfileScreen(
                profileViewModel = profileViewModel,
                genderViewModel = GenderViewModel(),
                navController = navController,
                onSave = { updatedUser ->
                    profileViewModel.saveUserToFirestore(
                        updatedUser.uid,
                        updatedUser,
                        onSuccess = {
                            navController.navigate(Routes.Profile)
                        },
                        onFailure = { error ->
                            Log.d("MyLog", error)
                        }
                    )
                    profileViewModel.updateUser(updatedUser)
                }
            )
        }

        composable(Routes.Likes) {
            LikeScreen(
                navController,
                currentUserId,
                profileViewModel,
                likeViewModel,
                photoViewModel
            )
        }

        composable(Routes.Messages) {
            MessagesScreen(
                navController,
                chatViewModel,
                profileViewModel,
                photoViewModel,
                currentUserId
            )
        }

        composable(Routes.PhotoRequest) {
            PhotoRequestScreen(navController, photoViewModel)
        }
    }
}
