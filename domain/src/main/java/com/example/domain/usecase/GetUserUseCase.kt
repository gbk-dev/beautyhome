package com.example.domain.usecase

import com.example.domain.models.User
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUserUseCase(private val userRepository: UserRepository) {

    fun getUser(): Flow<Result<User>>{
        return userRepository.getUser()
    }

}