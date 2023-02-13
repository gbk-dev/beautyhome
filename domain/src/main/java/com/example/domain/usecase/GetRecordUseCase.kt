package com.example.domain.usecase

import com.example.domain.models.Record
import com.example.domain.repository.RecordRepository
import kotlinx.coroutines.flow.Flow

class GetRecordUseCase(private val recordRepository: RecordRepository) {

    fun getRecord(): Flow<Result<Record>>{
        return recordRepository.getRecords()
    }

}