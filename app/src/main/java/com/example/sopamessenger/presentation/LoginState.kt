package com.example.sopamessenger.presentation

sealed class LoginState {
    object Idle : LoginState()
    object NotLoggedIn : LoginState()
    data class Success(val userId: String?) : LoginState()
    data class Error(val error: String?) : LoginState()
    object Cancelled : LoginState()
}