package com.example.sopamessenger.presentation.signup_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sopamessenger.data.AuthRepositoryImpl
import com.example.sopamessenger.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingUpViewModel @Inject constructor(
    private val repository: AuthRepositoryImpl
) : ViewModel() {

    private val _singUpState = Channel<SignUpState>()
    val signUpState = _singUpState.receiveAsFlow()

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