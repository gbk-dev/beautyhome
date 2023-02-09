package com.example.domain.usecase

import com.example.domain.models.Resource
import com.example.domain.models.User
import com.example.domain.repository.UserAuthRepository
import kotlinx.coroutines.flow.Flow

class SignInUseCase(private val userAuthRepository: UserAuthRepository) {

    suspend fun signIn(user: User, password: String): Flow<Resource<Boolean>>{
        return userAuthRepository.signIn(user = user, password = password)
    }

}