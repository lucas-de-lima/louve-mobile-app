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
    private val settingsRepository: SettingsRepository
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

            Log.d(TAG, "Usuário logado. Iniciando sincronização sequencial...")

            val favResult = favoritesRepository.syncWhenOnline()
            when (favResult) {
                is DomainResult.Success -> Log.d(TAG, "Favoritos sincronizados")
                is DomainResult.Error -> Log.w(TAG, "Falha nos favoritos: ${favResult.message}")
            }

            val settingsResult = settingsRepository.syncWhenOnline()
            when (settingsResult) {
                is DomainResult.Success -> Log.d(TAG, "Configurações sincronizadas")
                is DomainResult.Error -> Log.w(TAG, "Falha nas config: ${settingsResult.message}")
            }

            if (favoritesRepository.checkForConflicts()) {
                Log.d(TAG, "Conflitos detectados. Resolvendo...")
                when (favoritesRepository.resolveConflicts()) {
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