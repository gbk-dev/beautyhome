package com.example.beautyhome.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Record
import com.example.domain.models.User
import com.example.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getRecordUseCase: GetRecordUseCase,
    private val uploadImgUseCase: UploadImgUseCase,
    private val getImgUserUseCase: GetImgUserUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user : LiveData<User> by lazy {
        _user
    }

    private val _record = MutableLiveData<Record>()
    val record : LiveData<Record> by lazy {
        _record
    }

    private val _img = MutableLiveData<String>()
    val img : LiveData<String> by lazy {
        _img
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

    fun getRecord(){
        viewModelScope.launch(Dispatchers.IO) {
            getRecordUseCase.getRecord().collect{
                when{
                    it.isSuccess -> {
                        val getRecordList = it.getOrNull()
                        _record.postValue(getRecordList!!)
                    }
                    it.isFailure -> {
                        it.exceptionOrNull()?.printStackTrace()
                    }
                }
            }
        }
    }

    fun uploadImg(imgUri: String){
        viewModelScope.launch {
            uploadImgUseCase.uploadImg(imgUri = imgUri)
        }
    }

    fun signOut(){
        signOutUseCase.signOut()
    }

    fun getImg(){
        viewModelScope.launch(Dispatchers.IO) {
            getImgUserUseCase.getImgUser().collect{
                when{
                    it.isSuccess -> {
                        val img = it.getOrNull()
                        _img.postValue(img!!)
                    }
                    it.isFailure -> {
                        it.exceptionOrNull()?.printStackTrace()
                    }
                }
            }
        }
    }
}