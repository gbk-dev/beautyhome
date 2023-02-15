package com.example.domain.usecase

import com.example.domain.models.Record
import com.example.domain.repository.RecordRepository
import kotlinx.coroutines.flow.Flow

class GetAllActiveRecordsUseCase(private val recordRepository: RecordRepository) {

    fun getAllActiveRecords(): Flow<Result<List<Record>>>{
        return recordRepository.getAllActiveRecords()
    }

}