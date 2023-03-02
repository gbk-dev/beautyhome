package com.example.beautyhome.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Record
import com.example.domain.models.TimeSchedule
import com.example.domain.models.User
import com.example.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val clientRecordUseCase: ClientRecordUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getAllActiveRecordsUseCase: GetAllActiveRecordsUseCase,
    private val getTimeScheduleUseCase: GetTimeScheduleUseCase,
    private val setTimeScheduleUseCase: SetTimeScheduleUseCase,
    private val getRecordUseCase: GetRecordUseCase
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user : LiveData<User> by lazy {
        _user
    }

    private val _record = MutableLiveData<Record>()
    val record : LiveData<Record> by lazy {
        _record
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
        }
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

    fun getAllRecord(){
        viewModelScope.launch(Dispatchers.IO) {
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

    fun getTimeScheduleList(){
        viewModelScope.launch(Dispatchers.IO) {
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
}