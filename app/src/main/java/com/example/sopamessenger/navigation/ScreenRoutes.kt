package com.example.sopamessenger.navigation

sealed class ScreenRoutes(val route: String) {
    object SignInScreen : ScreenRoutes(route = "SignIn_Screen")
    object SignUpScreen : ScreenRoutes(route = "SignUp_Screen")
    object MainScreen : ScreenRoutes(route = "Main_Screen")
}