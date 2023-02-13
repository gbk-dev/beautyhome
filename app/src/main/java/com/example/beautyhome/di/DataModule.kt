package com.example.beautyhome.di

import com.example.data.repository.RecordRepositoryImpl
import com.example.data.repository.UserAuthRepositoryImpl
import com.example.data.repository.UserRepositoryImpl
import com.example.domain.repository.RecordRepository
import com.example.domain.repository.UserAuthRepository
import com.example.domain.repository.UserRepository
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

    @Provides
    @Singleton
    fun provideRecordRepository(): RecordRepository{
        return RecordRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository{
        return UserRepositoryImpl()
    }

}