package com.example.sopamessenger.presentation.signup_screen

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sopamessenger.BuildConfig
import com.example.sopamessenger.data.AuthRepositoryImpl
import com.example.sopamessenger.presentation.GoogleSignInState
import com.example.sopamessenger.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SingUpViewModel @Inject constructor(
    private val repository: AuthRepositoryImpl
) : ViewModel() {

    private val _singUpState = Channel<SignUpState>()
    val signUpState = _singUpState.receiveAsFlow()

    private val _googleState = mutableStateOf(GoogleSignInState())
    val googleState: State<GoogleSignInState> = _googleState


    fun googleSignIn(context: Context) = viewModelScope.launch {

        repository.googleSignIn(context).collect { result ->
            when (result) {
                is Resource.Success -> {
                    _googleState.value = GoogleSignInState(success = result.data)
                }
                is Resource.Loading -> {
                    _googleState.value = GoogleSignInState(loading = true)
                }
                is Resource.Error -> {
                    _googleState.value = GoogleSignInState(error = result.message!!)
                    Timber.d(BuildConfig.WEB_SERVER_CLIENT_ID)
                }
            }
        }
    }

    fun registerUser(email: String, password: String) = viewModelScope.launch {
        repository.registerUser(email, password).collect { result ->
            when (result) {
                is Resource.Success -> {
                    _singUpState.send(SignUpState(isSuccess = "Sign Up Success"))
                }

                is Resource.Error -> {
                    _singUpState.send(SignUpState(isError = result.message))
                }

                is Resource.Loading -> {
                    _singUpState.send(SignUpState(isLoading = true))
                }
            }
        }
    }

}