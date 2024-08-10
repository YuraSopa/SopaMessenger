package com.example.sopamessenger.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sopamessenger.presentation.login_screen.SignInScreen
import com.example.sopamessenger.presentation.signup_screen.SignUpScreen

@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.SignUpScreen.route
    ) {
        composable(route = ScreenRoutes.SignInScreen.route) {
            SignInScreen(navController)
        }
        composable(route = ScreenRoutes.SignUpScreen.route) {
            SignUpScreen(navController)
        }

    }
}