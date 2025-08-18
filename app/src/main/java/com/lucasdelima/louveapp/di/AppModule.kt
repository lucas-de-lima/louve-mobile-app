package com.lucasdelima.louveapp.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lucasdelima.louveapp.data.repository.DataStoreLocalFavoritesRepository
import com.lucasdelima.louveapp.data.repository.DefaultFavoritesRepository
import com.lucasdelima.louveapp.data.repository.DefaultSettingsRepository
import com.lucasdelima.louveapp.data.repository.FirebaseAuthRepositoryImpl
import com.lucasdelima.louveapp.data.repository.FirebaseAnalyticsService
import com.lucasdelima.louveapp.data.repository.FirestoreUserRepositoryImpl
import com.lucasdelima.louveapp.data.repository.HymnRepositoryImpl
import com.lucasdelima.louveapp.data.repository.LocalSettingsRepository
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import com.lucasdelima.louveapp.domain.repository.FavoritesRepository
import com.lucasdelima.louveapp.domain.repository.HymnRepository
import com.lucasdelima.louveapp.domain.repository.LocalFavoritesRepository
import com.lucasdelima.louveapp.domain.repository.AnalyticsService
import com.lucasdelima.louveapp.domain.repository.SettingsRepository
import com.lucasdelima.louveapp.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt que ensina ao Dagger/Hilt como prover as implementações
 * para as interfaces dos nossos repositórios e outras dependências do app.
 * Este arquivo centraliza toda a lógica de injeção de dependência.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsService(impl: FirebaseAnalyticsService): AnalyticsService

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FirebaseAuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: FirestoreUserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindLocalFavoritesRepository(impl: DataStoreLocalFavoritesRepository): LocalFavoritesRepository

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(impl: DefaultFavoritesRepository): FavoritesRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: DefaultSettingsRepository): SettingsRepository
}

// Módulo para provisão de classes que não podemos injetar via construtor
// (libs externas como Firebase, ou classes que precisam de construção manual).
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideHymnRepository(): HymnRepository {
        return HymnRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }
}
