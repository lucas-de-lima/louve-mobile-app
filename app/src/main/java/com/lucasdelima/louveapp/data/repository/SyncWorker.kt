package com.lucasdelima.louveapp.data.repository

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.lucasdelima.louveapp.domain.model.Result as DomainResult
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import com.lucasdelima.louveapp.domain.repository.FavoritesRepository
import com.lucasdelima.louveapp.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val authRepository: AuthRepository,
    private val favoritesRepository: FavoritesRepository,
    private val settingsRepository: SettingsRepository,
    private val bidirectionalSyncService: BidirectionalSyncService
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "SyncWorker"
    }

    override suspend fun doWork(): ListenableWorker.Result {
        return try {
            val user = authRepository.getCurrentUser().first()
            if (user == null) {
                Log.d(TAG, "Usuário não logado. Pulando sincronização.")
                return ListenableWorker.Result.success()
            }

            Log.d(TAG, "Usuário logado. Iniciando sincronização bidirecional...")

            when (val result = bidirectionalSyncService.syncRemoteToLocal()) {
                is DomainResult.Success -> Log.d(TAG, "Sincronização bidirecional concluída")
                is DomainResult.Error -> {
                    Log.w(TAG, "Falha na sincronização: ${result.message}")
                    return ListenableWorker.Result.retry()
                }
            }

            if (bidirectionalSyncService.hasConflicts()) {
                Log.d(TAG, "Conflitos detectados. Resolvendo...")
                when (bidirectionalSyncService.resolveConflicts()) {
                    is DomainResult.Success -> Log.d(TAG, "Conflitos resolvidos")
                    is DomainResult.Error -> Log.w(TAG, "Falha ao resolver conflitos")
                }
            }

            Log.d(TAG, "Sincronização concluída")
            ListenableWorker.Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Erro na sincronização", e)
            ListenableWorker.Result.retry()
        }
    }
}