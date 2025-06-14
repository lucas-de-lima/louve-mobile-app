package com.lucasdelima.louveapp.di

import android.content.Context
import com.lucasdelima.louveapp.data.repository.LocalSettingsRepository
import com.lucasdelima.louveapp.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository {
        return LocalSettingsRepository(context)
    }
}