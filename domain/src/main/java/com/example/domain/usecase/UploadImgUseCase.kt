package com.example.domain.usecase

import com.example.domain.repository.UserRepository

class UploadImgUseCase(private val userRepository: UserRepository) {

    fun uploadImg(imgUri: String){
        userRepository.uploadImage(imgUri = imgUri)
    }

}