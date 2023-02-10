package com.example.domain.usecase

import com.example.domain.repository.UserAuthRepository

class LoginUseCase(private val userAuthRepository: UserAuthRepository) {

    fun login(): Boolean{
        return userAuthRepository.login()
    }

}