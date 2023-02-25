package com.example.domain.repository

import com.example.domain.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUser(): Flow<Result<User>>

    fun uploadImage(imgUri: String)

    fun getImage(): Flow<Result<String>>

}