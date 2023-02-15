package com.example.data.repository

import android.util.Log
import androidx.core.net.toUri
import com.example.domain.models.User
import com.example.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl : UserRepository {

    private lateinit var listener: ValueEventListener
    private val dbAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val uid = dbAuth.currentUser?.uid
    private val storage = FirebaseStorage.getInstance()

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

    override fun uploadImage(imgUri: String) {
        val img = imgUri.toUri()
        storage.reference.child(uid!!).putFile(img)
    }

    override suspend fun getImage(): Flow<Result<String>> = callbackFlow{
        val reference = storage.getReference(uid!!)
        reference.downloadUrl.addOnSuccessListener { uri ->
            this@callbackFlow.trySendBlocking(Result.success(uri.toString()))
        }.addOnFailureListener {
            this@callbackFlow.trySendBlocking(Result.failure(it))
        }.await()

        awaitClose {

        }
    }
}