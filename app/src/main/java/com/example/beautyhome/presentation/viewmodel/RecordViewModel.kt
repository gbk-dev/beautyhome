package com.example.beautyhome.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Record
import com.example.domain.models.TimeSchedule
import com.example.domain.models.User
import com.example.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val clientRecordUseCase: ClientRecordUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getAllActiveRecordsUseCase: GetAllActiveRecordsUseCase,
    private val getTimeScheduleUseCase: GetTimeScheduleUseCase,
    private val setTimeScheduleUseCase: SetTimeScheduleUseCase
) : ViewModel() {

    private val _userList = MutableLiveData<User>()
    val userList : LiveData<User> by lazy {
        _userList
    }

    private val _allRecordsList = MutableLiveData<List<Record>>()
    val allRecordList : LiveData<List<Record>> by lazy {
        _allRecordsList
    }

    private val _timeScheduleList = MutableLiveData<List<TimeSchedule>>()
    val timeScheduleList : LiveData<List<TimeSchedule>> by lazy {
        _timeScheduleList
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

    fun getAllRecord(){
        viewModelScope.launch {
            getAllActiveRecordsUseCase.getAllActiveRecords().collect{
                when{
                    it.isSuccess -> {
                        val getAllRecordList = it.getOrNull()
                        _allRecordsList.postValue(getAllRecordList!!)
                    }
                    it.isFailure -> {
                        it.exceptionOrNull()?.printStackTrace()
                    }
                }
            }
        }
    }

    fun getTimeSchedule(){
        viewModelScope.launch {
            getTimeScheduleUseCase.getTimeSchedule().collect{
                when{
                    it.isSuccess -> {
                        val timeSchedule = it.getOrNull()
                        _timeScheduleList.postValue(timeSchedule!!)
                    }
                    it.isFailure -> {
                        it.exceptionOrNull()?.printStackTrace()
                    }
                }
            }
        }
    }

    fun setTimeSchedule(timeSchedule: TimeSchedule){
        viewModelScope.launch {
            setTimeScheduleUseCase.setTimeSchedule(timeSchedule = timeSchedule)
        }
    }

    fun signOut(){
        signOutUseCase.signOut()
    }
}