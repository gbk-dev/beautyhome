package com.example.beautyhome.di

import com.example.domain.repository.UserAuthRepository
import com.example.domain.usecase.SignInUseCase
import com.example.domain.usecase.SignUpUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule {

    @Provides
    fun provideSignUpUseCase(userAuthRepository: UserAuthRepository): SignUpUseCase{
        return SignUpUseCase(userAuthRepository = userAuthRepository)
    }

    @Provides
    fun provideSignInUseCase(userAuthRepository: UserAuthRepository): SignInUseCase{
        return SignInUseCase(userAuthRepository = userAuthRepository)
    }

}