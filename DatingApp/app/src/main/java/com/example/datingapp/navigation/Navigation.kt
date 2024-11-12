package com.example.datingapp.navigation

import androidx.compose.runtime.Composable
import ProfileViewModel
import SignUpScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.datingapp.EditProfileScreen
import com.example.datingapp.LoginScreen
import com.example.datingapp.ProfileScreen
import com.example.datingapp.ui.screens.SignInScreen

@Composable
fun DatingApp() {
    val navController = rememberNavController()
    val profileViewModel: ProfileViewModel = viewModel()

    NavHost(navController, startDestination = "LoginScreen") {

        composable("SignUpScreen") {
            SignUpScreen(navController)
        }

        composable("SignInScreen") {
            SignInScreen(navController)
        }

        composable("LoginScreen") {
            LoginScreen(navController)
        }
        composable("ProfileScreen") {
            ProfileScreen(
                navController,
                profileViewModel
            )
        }
        composable("EditProfileScreen") {
            EditProfileScreen(
                viewModel = profileViewModel,
                navController = navController,
                onSave = { updatedUser ->
                    profileViewModel.updateUser(updatedUser)
                    navController.popBackStack()
                }
            )
        }
    }
}
