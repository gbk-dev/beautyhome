package com.example.domain.usecase

import com.example.domain.models.Record
import com.example.domain.repository.RecordRepository

class ClientRecordUseCase(private val recordRepository: RecordRepository) {

    suspend fun clientRecord(record: Record){
        recordRepository.clientRecord(record = record)
    }

}