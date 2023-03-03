package com.example.data.repository

import com.example.domain.models.Record
import com.example.domain.repository.RecordRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RecordRepositoryImpl : RecordRepository {

    private val dbAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var listenerRecords: ValueEventListener
    private lateinit var listenerAllRecords: ValueEventListener

    override suspend fun clientRecord(record: Record) {

        val uid = dbAuth.currentUser?.uid

        try {
            db.getReference("ActiveRecords/$uid").setValue(record).await()
        }catch (e: Exception){

        }

    }

    override fun getRecords(): Flow<Result<Record>> = callbackFlow{

        val uid = dbAuth.currentUser?.uid

        listenerRecords = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val record = snapshot.getValue(Record::class.java)
                if (record != null){
                    this@callbackFlow.trySendBlocking(Result.success(record))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.trySendBlocking(Result.failure(error.toException()))
            }
        }

        db.reference
            .child("ActiveRecords/$uid")
            .addValueEventListener(listenerRecords)

        awaitClose {

        }
    }

    override fun getAllActiveRecords(): Flow<Result<List<Record>>> = callbackFlow {
        listenerAllRecords = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allRecord = snapshot.children.map { ds ->
                    ds.getValue(Record::class.java)
                }
                this@callbackFlow.trySendBlocking(Result.success(allRecord.filterNotNull()))
            }

            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.trySendBlocking(Result.failure(error.toException()))
            }
        }

        db.reference
            .child("ActiveRecords")
            .addValueEventListener(listenerAllRecords)

        awaitClose {

        }
    }
}