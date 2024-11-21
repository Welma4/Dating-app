package com.example.datingapp.navigation

import androidx.compose.runtime.Composable
import ProfileViewModel
import SignUpScreen
import android.util.Log
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
import com.example.datingapp.ui.screens.ProfileScreen
import com.example.datingapp.ui.screens.SignInScreen
import com.example.datingapp.viewmodel.PhotoViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun DatingApp() {
    val navController = rememberNavController()
    val profileViewModel: ProfileViewModel = viewModel()
    val photoViewModel: PhotoViewModel = viewModel()
    val auth = Firebase.auth

    NavHost(navController, startDestination = if (auth.currentUser != null) Routes.Profile else Routes.Login) {
//    NavHost(navController, startDestination = Routes.Login) {

        composable(Routes.SignUp) {
            SignUpScreen(navController, profileViewModel, photoViewModel)
        }

        composable(Routes.SignIn) {
            SignInScreen(navController)
        }

        composable(Routes.Login) {
            LoginScreen(navController)
        }

        composable(Routes.Home) {
            HomeScreen(navController)
        }

        composable(Routes.Profile) {
            ProfileScreen(
                navController,
                profileViewModel,
                photoViewModel
            )
        }
        composable(Routes.EditProfile) {
            EditProfileScreen(
                viewModel = profileViewModel,
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
            LikeScreen(navController)
        }

        composable(Routes.Messages) {
            MessagesScreen(navController)
        }
    }
}
