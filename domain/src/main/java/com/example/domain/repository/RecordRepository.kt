package com.example.domain.repository

import com.example.domain.models.Record
import kotlinx.coroutines.flow.Flow

interface RecordRepository {

    suspend fun clientRecord(record: Record)

    fun getRecords(): Flow<Result<Record>>

    fun getAllActiveRecords(): Flow<Result<List<Record>>>
}