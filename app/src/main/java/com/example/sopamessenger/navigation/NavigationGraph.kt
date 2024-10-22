package com.example.sopamessenger.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sopamessenger.presentation.chat_screen.ChatScreen
import com.example.sopamessenger.presentation.home_screen.HomeScreen
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
        composable(route = ScreenRoutes.HomeScreen.route){
            HomeScreen(navController)
        }
        
        composable(route = "chat/{channelId}&{channelName}", arguments = listOf(
            navArgument("channelId") {
                type = NavType.StringType
            },
            navArgument("channelName") {
                type = NavType.StringType
            }
        )) {
            val channelId = it.arguments?.getString("channelId") ?: ""
            val channelName = it.arguments?.getString("channelName") ?: ""
            ChatScreen(navController, channelId, channelName)
        }
    }
}