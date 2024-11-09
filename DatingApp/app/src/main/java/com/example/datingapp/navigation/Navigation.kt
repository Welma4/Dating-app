package com.example.datingapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.datingapp.LoginScreen
import com.example.datingapp.ProfileScreen

@Composable
fun DatingApp()
{
    val navController = rememberNavController()
    NavHost(navController, startDestination = "LoginScreen") {
        composable("LoginScreen") {
            LoginScreen(navController)
        }
        composable("ProfileScreen") {
            ProfileScreen()
        }
    }
}