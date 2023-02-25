package com.example.data.repository

import com.example.domain.models.Resource
import com.example.domain.models.User
import com.example.domain.repository.UserAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class UserAuthRepositoryImpl: UserAuthRepository {

    private val dbAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()

    override suspend fun signUp(user: User, password: String): Flow<Resource<Boolean>> = flow{

        var signUpSuccess = false

        emit(Resource.Loading)

        try {

            dbAuth.createUserWithEmailAndPassword(user.email.toString(), password).addOnSuccessListener {
                signUpSuccess = true
            }.await()

            if (signUpSuccess){
                val userId = dbAuth.currentUser?.uid
                val userDb = User(firstName = user.firstName, lastName = user.lastName, email = user.email, phone = user.phone, uid = userId, master = false)
                db.reference.child("Users/$userId").setValue(userDb).addOnCompleteListener {

                }.await()

                emit(Resource.Success(true))

            }else{
                emit(Resource.Failure("Failed to sign up"))
            }

        } catch (e: Exception){
            emit(Resource.Failure(e.localizedMessage?: "An unexpected error"))
        }

    }

    override suspend fun signIn(user: User, password: String): Flow<Resource<Boolean>> = flow{

        var signInSuccess = false

        emit(Resource.Loading)

        try {

            dbAuth.signInWithEmailAndPassword(user.email.toString(), password).addOnCompleteListener {
                signInSuccess = true
            }.await()

            if (signInSuccess){
                emit(Resource.Success(true))
            }else{
                emit(Resource.Failure("Failed to sign in"))
            }

        }catch (e: Exception){
            emit(Resource.Failure(e.localizedMessage?: "An unexpected error"))
        }
    }

    override fun login(): Boolean {
        return dbAuth.currentUser != null
    }

    override fun signOut() {
        return dbAuth.signOut()
    }
}