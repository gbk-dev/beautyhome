package com.example.beautyhome.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Record
import com.example.domain.models.User
import com.example.domain.usecase.GetRecordUseCase
import com.example.domain.usecase.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getRecordUseCase: GetRecordUseCase
) : ViewModel() {

    private val _userList = MutableLiveData<User>()
    val userList : LiveData<User> by lazy {
        _userList
    }

    private val _recordList = MutableLiveData<Record>()
    val recordList : LiveData<Record> by lazy {
        _recordList
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

    fun getRecord(){
        viewModelScope.launch {
            getRecordUseCase.getRecord().collect{
                when{
                    it.isSuccess -> {
                        val getRecordList = it.getOrNull()
                        _recordList.postValue(getRecordList!!)
                    }
                    it.isFailure -> {
                        it.exceptionOrNull()?.printStackTrace()
                    }
                }
            }
        }
    }
}