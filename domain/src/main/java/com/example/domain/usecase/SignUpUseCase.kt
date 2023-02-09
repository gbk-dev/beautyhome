package com.example.domain.usecase

import com.example.domain.models.Resource
import com.example.domain.models.User
import com.example.domain.repository.UserAuthRepository
import kotlinx.coroutines.flow.Flow

class SignUpUseCase(private val userAuthRepository: UserAuthRepository) {

    suspend fun signUp(user: User, password: String): Flow<Resource<Boolean>> {
        return userAuthRepository.signUp(user = user, password = password)
    }

}