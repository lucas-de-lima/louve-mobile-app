package com.lucasdelima.louveapp.data.repository

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import com.lucasdelima.louveapp.domain.repository.FavoritesRepository
import com.lucasdelima.louveapp.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncWorkerFactory @Inject constructor(
    private val authRepository: AuthRepository,
    private val favoritesRepository: FavoritesRepository,
    private val settingsRepository: SettingsRepository,
    private val bidirectionalSyncService: BidirectionalSyncService
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SyncWorker::class.java.name -> SyncWorker(
                appContext,
                workerParameters,
                authRepository,
                favoritesRepository,
                settingsRepository,
                bidirectionalSyncService
            )
            else -> null
        }
    }
}