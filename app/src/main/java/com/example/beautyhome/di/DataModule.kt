package com.example.beautyhome.di

import com.example.data.repository.UserAuthRepositoryImpl
import com.example.domain.repository.UserAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideUserAuthRepository(): UserAuthRepository{
        return UserAuthRepositoryImpl()
    }


}