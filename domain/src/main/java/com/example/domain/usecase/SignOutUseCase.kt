package com.example.domain.usecase

import com.example.domain.repository.UserAuthRepository

class SignOutUseCase(private val userAuthRepository: UserAuthRepository) {

    fun signOut(){
        userAuthRepository.signOut()
    }

}