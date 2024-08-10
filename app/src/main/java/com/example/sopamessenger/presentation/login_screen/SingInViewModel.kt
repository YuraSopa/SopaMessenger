package com.example.sopamessenger.presentation.login_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sopamessenger.data.AuthRepositoryImpl
import com.example.sopamessenger.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingInViewModel @Inject constructor(
    private val repository: AuthRepositoryImpl
) : ViewModel() {

    private val _singInState = Channel<SignInState>()
    val signInState = _singInState.receiveAsFlow()

    fun loginUser(email: String, password: String) = viewModelScope.launch {
        repository.loginUser(email, password).collectLatest { result ->
            when (result) {
                is Resource.Success -> {
                    _singInState.send(SignInState(isSuccess = "Sign In Success"))
                }

                is Resource.Error -> {
                    _singInState.send(SignInState(isError = result.message))
                }

                is Resource.Loading -> {
                    _singInState.send(SignInState(isLoading = true))
                }
            }
        }
    }

}