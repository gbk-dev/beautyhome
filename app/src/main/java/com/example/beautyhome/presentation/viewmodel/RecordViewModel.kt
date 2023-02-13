package com.example.beautyhome.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Record
import com.example.domain.models.User
import com.example.domain.usecase.ClientRecordUseCase
import com.example.domain.usecase.GetUserUseCase
import com.example.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val clientRecordUseCase: ClientRecordUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _userList = MutableLiveData<User>()
    val userList : LiveData<User> by lazy {
        _userList
    }

    fun clientRecord(record: Record){
        viewModelScope.launch {
            clientRecordUseCase.clientRecord(record = record)
            Log.e("ViewModel", record.toString())
        }
    }

    fun getUser(){
        viewModelScope.launch {
            getUserUseCase.getUser().collect{
                when{
                    it.isSuccess -> {
                        val getUserList = it.getOrNull()
                        _userList.postValue(getUserList!!)
                    }

                    it.isFailure -> {
                        it.exceptionOrNull()?.printStackTrace()
                    }
                }
            }
        }
    }

    fun signOut(){
        signOutUseCase.signOut()
    }
}