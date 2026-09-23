package com.lucasdelima.louveapp.data.repository

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
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
    private val bidirectionalSyncService: BidirectionalSyncService,
    private val hymnListRepository: com.lucasdelima.louveapp.domain.repository.HymnListRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "SyncWorker"
        private const val KEY_RETRY_COUNT = "retry_count"
        private const val MAX_RETRIES = 5
    }

    override suspend fun doWork(): ListenableWorker.Result {
        val attemptCount = runAttemptCount

        return try {
            val user = authRepository.getCurrentUser().first()
            if (user == null) {
                Log.d(TAG, "Usuário não logado. Pulando sincronização.")
                return ListenableWorker.Result.success()
            }

            Log.d(TAG, "Usuário logado. Iniciando sincronização bidirecional (tentativa $attemptCount)...")

            when (val result = bidirectionalSyncService.syncRemoteToLocal()) {
                is DomainResult.Success -> Log.d(TAG, "Sincronização bidirecional concluída")
                is DomainResult.Error -> {
                    if (attemptCount >= MAX_RETRIES) {
                        Log.e(TAG, "Sincronização falhou após $MAX_RETRIES tentativas: ${result.message}")
                        return ListenableWorker.Result.success()
                    }
                    Log.w(TAG, "Falha na sincronização (tentativa $attemptCount): ${result.message}")
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
            if (attemptCount >= MAX_RETRIES) {
                Log.e(TAG, "Erro persistente: sincronização falhou após $MAX_RETRIES tentativas")
                return ListenableWorker.Result.success()
            }
            ListenableWorker.Result.retry()
        }
    }
}