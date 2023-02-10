package com.example.domain.repository

import com.example.domain.models.Resource
import com.example.domain.models.User
import kotlinx.coroutines.flow.Flow

interface UserAuthRepository {

    suspend fun signUp(
        user: User,
        password: String
    ): Flow<Resource<Boolean>>

    suspend fun signIn(
        user: User,
        password: String
    ): Flow<Resource<Boolean>>

    fun login(): Boolean
}