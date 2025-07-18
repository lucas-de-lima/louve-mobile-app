package com.lucasdelima.louveapp.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lucasdelima.louveapp.data.repository.FirebaseAuthRepositoryImpl
import com.lucasdelima.louveapp.data.repository.LocalSettingsRepository
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import com.lucasdelima.louveapp.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // Quando alguém pedir um AuthRepository, o Hilt fornecerá uma instância de FirebaseAuthRepositoryImpl
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl
    ): AuthRepository
}


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Ensina o Hilt a criar e fornecer uma instância única do FirebaseAuth
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    // Ensina o Hilt a criar e fornecer uma instância única do FirebaseFirestore
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository {
        return LocalSettingsRepository(context)
    }
}
