package com.example.domain.usecase

import com.example.domain.models.TimeSchedule
import com.example.domain.repository.TimeScheduleRepository

class SetTimeScheduleUseCase(private val timeScheduleRepository: TimeScheduleRepository) {

    suspend fun setTimeSchedule(timeSchedule: TimeSchedule){
        timeScheduleRepository.setTimeSchedule(timeSchedule = timeSchedule)
    }

}