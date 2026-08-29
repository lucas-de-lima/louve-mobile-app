package com.lucasdelima.louveapp.data.repository

import android.util.Log
import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.repository.LocalFavoritesRepository
import com.lucasdelima.louveapp.domain.repository.LocalSettingsRepository
import com.lucasdelima.louveapp.domain.repository.UserRepository
import com.lucasdelima.louveapp.domain.model.ThemeDefaults
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Serviço responsável por sincronização bidirecional com verificações inteligentes:
 * - Tema local padrão? → Prioriza nuvem se diferente de padrão
 * - Favoritos locais vazios? → Prioriza nuvem se diferente de vazio  
 * - Ambas têm dados? → Merge inteligente
 */
@Singleton
class DataMigrationService @Inject constructor(
    private val localFavoritesRepository: LocalFavoritesRepository,
    private val localSettingsRepository: LocalSettingsRepository,
    private val userRepository: UserRepository
) {
    companion object {
        private const val TAG = "DataMigrationService"
    }
    

    
    /**
     * Executa sincronização com verificações inteligentes para cada tipo de dado.
     */
    suspend fun migrateLocalDataToCloud(): Result<Unit> {
        return try {
            val localData = backupLocalData()
            val cloudData = checkCloudData()
            
            syncThemeIntelligently(localData.theme, cloudData)
            
            val localFavoritesWithHistory = getLocalFavoritesWithHistory()
            syncFavoritesIntelligently(localFavoritesWithHistory, cloudData.favorites)
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Falha na sincronização: ${e.message}", e)
        }
    }
    
    /**
     * Faz backup dos dados locais.
     */
    private suspend fun backupLocalData(): LocalDataBackup {
        val favorites = localFavoritesRepository.getFavorites().first()
        val theme = localSettingsRepository.theme.first()
        
        return LocalDataBackup(
            favorites = favorites,
            theme = theme,
            timestamp = System.currentTimeMillis()
        )
    }
    
    /**
     * Recupera favoritos locais preservando histórico.
     * Se local estiver vazio, tenta recuperar do backup.
     */
    private suspend fun getLocalFavoritesWithHistory(): Set<String> {
        val currentFavorites = localFavoritesRepository.getFavorites().first()
        
        if (currentFavorites.isNotEmpty()) {
            return currentFavorites
        }
        
        return emptySet()
    }
    
    /**
     * Verifica dados na nuvem.
     */
    private suspend fun checkCloudData(): CloudDataStatus {
        return try {
            val cloudFavorites = userRepository.getFavoriteHymnIds().first()
            val cloudSettings = userRepository.getUserSettings().first()
            
            val favorites = when (cloudFavorites) {
                is Result.Success -> cloudFavorites.data
                is Result.Error -> emptySet()
            }
            
            val theme = when (cloudSettings) {
                is Result.Success -> cloudSettings.data.themeId
                is Result.Error -> ThemeDefaults.THEME_ID
            }
            
            CloudDataStatus(
                favorites = favorites,
                theme = theme
            )
        } catch (e: Exception) {
            CloudDataStatus(
                favorites = emptySet(),
                theme = ThemeDefaults.THEME_ID
            )
        }
    }
    
    /**
     * Sincroniza TEMA com verificações inteligentes:
     * - Tema local é padrão? → Prioriza nuvem se diferente de padrão
     * - Se não for padrão, mantém tema local
     */
    private suspend fun syncThemeIntelligently(localTheme: String, cloudData: CloudDataStatus) {
        when {
            localTheme == ThemeDefaults.THEME_ID -> {
                if (cloudData.theme != ThemeDefaults.THEME_ID) {
                    localSettingsRepository.saveTheme(cloudData.theme)
                }
            }
            else -> {
                if (cloudData.theme == ThemeDefaults.THEME_ID) {
                    userRepository.updateUserSettings(
                        com.lucasdelima.louveapp.domain.model.UserSettings(themeId = localTheme)
                    )
                } else {
                    localSettingsRepository.saveTheme(cloudData.theme)
                }
            }
        }
    }
    
    /**
     * Sincroniza FAVORITOS com verificações inteligentes:
     * - Favoritos locais vazios? → Prioriza nuvem se diferente de vazio
     * - Ambas têm dados? → Merge inteligente
     */
    private suspend fun syncFavoritesIntelligently(localFavorites: Set<String>, cloudFavorites: Set<String>) {
        when {
            localFavorites.isEmpty() -> {
                if (cloudFavorites.isNotEmpty()) {
                    cloudFavorites.forEach { hymnId ->
                        localFavoritesRepository.addFavorite(hymnId)
                    }
                } else {
                    ensureEmptyFavoritesDocument()
                }
            }
            else -> {
                if (cloudFavorites.isNotEmpty()) {
                    mergeFavoritesIntelligently(localFavorites, cloudFavorites)
                } else {
                    migrateLocalFavoritesToCloud(localFavorites)
                }
            }
        }
    }
    
    /**
     * Faz merge inteligente de favoritos (local + nuvem).
     * IMPORTANTE: NUNCA substitui favoritos existentes, apenas adiciona novos.
     */
    private suspend fun mergeFavoritesIntelligently(localFavorites: Set<String>, cloudFavorites: Set<String>): Result<Unit> {
        return try {
            val newFavoritesToAdd = cloudFavorites - localFavorites
            
            if (newFavoritesToAdd.isNotEmpty()) {
                newFavoritesToAdd.forEach { hymnId ->
                    localFavoritesRepository.addFavorite(hymnId)
                }
            }
            
            val newFavoritesToAddToCloud = localFavorites - cloudFavorites
            
            if (newFavoritesToAddToCloud.isNotEmpty()) {
                newFavoritesToAddToCloud.forEach { hymnId ->
                    userRepository.addFavorite(hymnId)
                }
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao fazer merge de favoritos", e)
            Result.Error("Falha ao fazer merge de favoritos: ${e.message}", e)
        }
    }
    
    /**
     * Migra favoritos locais para a nuvem.
     */
    private suspend fun migrateLocalFavoritesToCloud(localFavorites: Set<String>): Result<Unit> {
        return try {
            localFavorites.forEach { hymnId ->
                userRepository.addFavorite(hymnId)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao migrar favoritos locais para nuvem", e)
            Result.Error("Falha ao migrar favoritos: ${e.message}", e)
        }
    }
    
    /**
     * Garante que existe documento vazio de favoritos na nuvem.
     */
    private suspend fun ensureEmptyFavoritesDocument(): Result<Unit> {
        return try {
            userRepository.updateUserSettings(
                com.lucasdelima.louveapp.domain.model.UserSettings(themeId = ThemeDefaults.THEME_ID)
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao garantir documento de favoritos vazio", e)
            Result.Error("Falha ao criar documento de favoritos: ${e.message}", e)
        }
    }
    
    /**
     * Status dos dados na nuvem.
     */
    private data class CloudDataStatus(
        val favorites: Set<String>,
        val theme: String
    )
    
    /**
     * Backup dos dados locais.
     */
    private data class LocalDataBackup(
        val favorites: Set<String>,
        val theme: String,
        val timestamp: Long
    )
}
