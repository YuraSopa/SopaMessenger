package com.example.sopamessenger.data

import android.content.Context
import com.example.sopamessenger.util.Resource
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun loginUser(email: String, password: String): Flow<Resource<AuthResult>>
    fun registerUser(email: String, password: String): Flow<Resource<AuthResult>>

    fun googleSignIn(context: Context): Flow<Resource<AuthResult>>
}