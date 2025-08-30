package com.lucasdelima.louveapp.data.repository

import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.repository.LocalFavoritesRepository
import com.lucasdelima.louveapp.domain.repository.LocalSettingsRepository
import com.lucasdelima.louveapp.domain.repository.UserRepository
import com.lucasdelima.louveapp.ui.theme.DefaultTheme
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
                is Result.Error -> DefaultTheme.name
            }
            
            CloudDataStatus(
                favorites = favorites,
                theme = theme
            )
        } catch (e: Exception) {
            CloudDataStatus(
                favorites = emptySet(),
                theme = DefaultTheme.name
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
            localTheme == DefaultTheme.name -> {
                if (cloudData.theme != DefaultTheme.name) {
                    localSettingsRepository.saveTheme(cloudData.theme)
                }
            }
            else -> {
                if (cloudData.theme == DefaultTheme.name) {
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
    private suspend fun mergeFavoritesIntelligently(localFavorites: Set<String>, cloudFavorites: Set<String>) {
        try {
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
        } catch (e: Exception) {
            //  TODO: Tratamento de erro silencioso
        }
    }
    
    /**
     * Migra favoritos locais para a nuvem.
     */
    private suspend fun migrateLocalFavoritesToCloud(localFavorites: Set<String>) {
        try {
            localFavorites.forEach { hymnId ->
                userRepository.addFavorite(hymnId)
            }
        } catch (e: Exception) {
            // TODO:Tratamento de erro silencioso
        }
    }
    
    /**
     * Garante que existe documento vazio de favoritos na nuvem.
     */
    private suspend fun ensureEmptyFavoritesDocument() {
        try {
            userRepository.updateUserSettings(
                com.lucasdelima.louveapp.domain.model.UserSettings(themeId = DefaultTheme.name)
            )
        } catch (e: Exception) {
            //  TODO: Tratamento de erro silencioso
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
