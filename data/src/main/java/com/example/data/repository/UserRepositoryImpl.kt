package com.example.data.repository

import android.util.Log
import com.example.domain.models.User
import com.example.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserRepositoryImpl : UserRepository {

    private lateinit var listener: ValueEventListener
    private val dbAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val uid = dbAuth.currentUser?.uid

    override fun getUser(): Flow<Result<User>> = callbackFlow {

        listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                this@callbackFlow.trySendBlocking(Result.success(user!!))
            }

            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.trySendBlocking(Result.failure(error.toException()))
            }
        }

        db.getReference("Users/$uid")
            .addValueEventListener(listener)

        awaitClose {

        }
    }
}