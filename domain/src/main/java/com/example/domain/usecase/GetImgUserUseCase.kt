package com.example.domain.usecase

import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetImgUserUseCase(private val userRepository: UserRepository) {

    suspend fun getImgUser(): Flow<Result<String>>{
        return userRepository.getImage()
    }
}