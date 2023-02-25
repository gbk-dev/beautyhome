package com.example.data.repository

import android.util.Log
import com.example.domain.models.TimeSchedule
import com.example.domain.repository.TimeScheduleRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TimeScheduleRepositoryImpl : TimeScheduleRepository {

    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var listener: ValueEventListener

    override suspend fun setTimeSchedule(timeSchedule: TimeSchedule) {

        val date = timeSchedule.date
        val time = timeSchedule.time
        db.reference.child("TimeSchedule/$date").setValue(timeSchedule).await()

    }

    override fun getTimeSchedule(): Flow<Result<List<TimeSchedule>>> = callbackFlow {

        listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val timeSchedule = snapshot.children.map { ds ->
                    ds.getValue(TimeSchedule::class.java)
                }
                Log.e("TimeScheduleRepository", timeSchedule.toString())
                this@callbackFlow.trySendBlocking(Result.success(timeSchedule.filterNotNull()))
            }

            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.trySendBlocking(Result.failure(error.toException()))
            }

        }

        db.reference
            .child("TimeSchedule")
            .addValueEventListener(listener)

        awaitClose {
            db.reference
                .child("TimeSchedule")
                .removeEventListener(listener)
        }

    }

}