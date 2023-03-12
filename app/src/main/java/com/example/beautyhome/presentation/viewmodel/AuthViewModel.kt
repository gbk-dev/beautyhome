package com.example.beautyhome.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Resource
import com.example.domain.models.User
import com.example.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val signInUseCase: SignInUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user : LiveData<User> by lazy {
        _user
    }

    private val _signUpState = mutableStateOf<Resource<Boolean>>(Resource.Success(false))
    val signUp: State<Resource<Boolean>> by lazy {
        _signUpState
    }

    private val _signInState = mutableStateOf<Resource<Boolean>>(Resource.Success(false))
    val signIn: State<Resource<Boolean>> by lazy {
        _signInState
    }

    fun getUser(){
        viewModelScope.launch(Dispatchers.IO) {
            getUserUseCase.getUser().collect{
                when{
                    it.isSuccess -> {
                        val getUserList = it.getOrNull()
                        _user.postValue(getUserList!!)
                    }

                    it.isFailure -> {
                        it.exceptionOrNull()?.printStackTrace()
                    }
                }
            }
        }
    }

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