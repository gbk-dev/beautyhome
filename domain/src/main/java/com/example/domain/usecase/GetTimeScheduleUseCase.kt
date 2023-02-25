package com.example.domain.usecase

import com.example.domain.models.TimeSchedule
import com.example.domain.repository.TimeScheduleRepository
import kotlinx.coroutines.flow.Flow

class GetTimeScheduleUseCase(private val timeScheduleRepository: TimeScheduleRepository) {

    fun getTimeSchedule(): Flow<Result<List<TimeSchedule>>>{
        return timeScheduleRepository.getTimeSchedule()
    }

}