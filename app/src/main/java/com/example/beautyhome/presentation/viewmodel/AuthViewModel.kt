package com.example.beautyhome.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Resource
import com.example.domain.models.User
import com.example.domain.usecase.LoginUseCase
import com.example.domain.usecase.SignInUseCase
import com.example.domain.usecase.SignOutUseCase
import com.example.domain.usecase.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val signInUseCase: SignInUseCase,
    loginUseCase: LoginUseCase
) : ViewModel() {

    private val _signUpState = mutableStateOf<Resource<Boolean>>(Resource.Success(false))
    val signUp: State<Resource<Boolean>> = _signUpState

    private val _signInState = mutableStateOf<Resource<Boolean>>(Resource.Success(false))
    val signIn: State<Resource<Boolean>> = _signInState

    val login = loginUseCase.login()

    fun signUp(user: User, password: String){
        viewModelScope.launch {
            signUpUseCase.signUp(user = user, password = password).collect{
                _signUpState.value = it
            }
        }
    }

    fun signIn(user: User, password: String){
        viewModelScope.launch {
            signInUseCase.signIn(user = user, password = password).collect{
                _signInState.value = it
            }
        }
    }

}