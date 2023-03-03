package com.example.data.repository

import com.example.domain.models.Resource
import com.example.domain.models.User
import com.example.domain.repository.UserAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class UserAuthRepositoryImpl: UserAuthRepository {

    private val dbAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()

    override suspend fun signUp(user: User, password: String): Flow<Resource<Boolean>> = callbackFlow{

        this@callbackFlow.trySendBlocking(Resource.Loading)

        try {

            dbAuth.createUserWithEmailAndPassword(user.email.toString(), password).addOnCompleteListener { createUser ->
                if (createUser.isSuccessful) {
                    val userId = dbAuth.currentUser?.uid
                    val userDb = User(
                        firstName = user.firstName,
                        lastName = user.lastName,
                        email = user.email,
                        phone = user.phone,
                        uid = userId,
                        master = false
                    )
                    db.reference.child("Users/$userId").setValue(userDb).addOnCompleteListener {
                        if (it.isSuccessful){
                            this@callbackFlow.trySendBlocking(Resource.Success(true))
                        }
                    }

                } else {
                    this@callbackFlow.trySendBlocking(Resource.Failure("Failed to sign up"))
                }
            }.await()

        } catch (e: Exception){
            this@callbackFlow.trySendBlocking(Resource.Failure(e.localizedMessage?: "An unexpected error"))
        }

        awaitClose {

        }

    }

    override suspend fun signIn(user: User, password: String): Flow<Resource<Boolean>> = callbackFlow{

        this@callbackFlow.trySendBlocking(Resource.Loading)

        try {

            dbAuth.signInWithEmailAndPassword(user.email.toString(), password).addOnCompleteListener {
                if (it.isSuccessful) {
                    this@callbackFlow.trySendBlocking(Resource.Success(true))
                } else {
                    this@callbackFlow.trySendBlocking(Resource.Failure("Failed to sign in"))
                }
            }.await()

        } catch (e: Exception) {
            this@callbackFlow.trySendBlocking(Resource.Failure(e.localizedMessage?: "An unexpected error"))
        }

        awaitClose {

        }
    }

    override fun login(): Boolean {
        return dbAuth.currentUser != null
    }

    override fun signOut() {
        return dbAuth.signOut()
    }
}