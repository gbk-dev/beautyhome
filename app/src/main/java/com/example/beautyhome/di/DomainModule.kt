package com.example.beautyhome.di

import com.example.domain.repository.RecordRepository
import com.example.domain.repository.TimeScheduleRepository
import com.example.domain.repository.UserAuthRepository
import com.example.domain.repository.UserRepository
import com.example.domain.usecase.*
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

    @Provides
    fun provideLoginUseCase(userAuthRepository: UserAuthRepository): LoginUseCase{
        return LoginUseCase(userAuthRepository = userAuthRepository)
    }

    @Provides
    fun provideClientRecordUseCase(recordRepository: RecordRepository): ClientRecordUseCase{
        return ClientRecordUseCase(recordRepository = recordRepository)
    }

    @Provides
    fun provideSignOutUseCase(userAuthRepository: UserAuthRepository): SignOutUseCase{
        return SignOutUseCase(userAuthRepository = userAuthRepository)
    }

    @Provides
    fun provideGetUserUseCase(userRepository: UserRepository): GetUserUseCase{
        return GetUserUseCase(userRepository = userRepository)
    }

    @Provides
    fun provideGetRecordUseCase(recordRepository: RecordRepository): GetRecordUseCase{
        return GetRecordUseCase(recordRepository = recordRepository)
    }

    @Provides
    fun provideUploadImgUseCase(userRepository: UserRepository): UploadImgUseCase{
        return UploadImgUseCase(userRepository = userRepository)
    }

    @Provides
    fun provideGetImgUserUseCase(userRepository: UserRepository): GetImgUserUseCase{
        return GetImgUserUseCase(userRepository = userRepository)
    }

    @Provides
    fun provideGetAllActiveRecords(recordRepository: RecordRepository): GetAllActiveRecordsUseCase{
        return GetAllActiveRecordsUseCase(recordRepository = recordRepository)
    }

    @Provides
    fun provideGetTimeScheduleUseCase(timeScheduleRepository: TimeScheduleRepository): GetTimeScheduleUseCase{
        return GetTimeScheduleUseCase(timeScheduleRepository = timeScheduleRepository)
    }

    @Provides
    fun provideSetTimeScheduleUseCase(timeScheduleRepository: TimeScheduleRepository): SetTimeScheduleUseCase{
        return SetTimeScheduleUseCase(timeScheduleRepository = timeScheduleRepository)
    }
}