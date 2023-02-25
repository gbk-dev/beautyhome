package com.example.domain.repository

import com.example.domain.models.TimeSchedule
import kotlinx.coroutines.flow.Flow

interface TimeScheduleRepository {

    suspend fun setTimeSchedule(timeSchedule: TimeSchedule)

    fun getTimeSchedule(): Flow<Result<List<TimeSchedule>>>

}