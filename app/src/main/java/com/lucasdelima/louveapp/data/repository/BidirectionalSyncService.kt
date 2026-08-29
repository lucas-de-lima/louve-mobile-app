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
 * Serviço responsável por sincronização bidirecional entre dados locais e remotos.
 * Este serviço é usado para manter dados sincronizados quando o usuário está offline
 * e depois sincroniza quando volta a ficar online.
 */
@Singleton
class BidirectionalSyncService @Inject constructor(
    private val localFavoritesRepository: LocalFavoritesRepository,
    private val localSettingsRepository: LocalSettingsRepository,
    private val userRepository: UserRepository
) {
    
    companion object {
        private const val TAG = "BidirectionalSyncService"
    }
    
    /**
     * Sincroniza dados da nuvem para o local quando o usuário volta a ficar online.
     * Útil para casos onde o usuário fez mudanças em outro dispositivo.
     */
    suspend fun syncRemoteToLocal(): Result<Unit> {
        return try {
            Log.d(TAG, "Iniciando sincronização remoto -> local...")
            
            // 1. Sincronizar favoritos
            val favoritesResult = syncFavoritesRemoteToLocal()
            if (favoritesResult is Result.Error) {
                Log.w(TAG, "Falha na sincronização de favoritos: ${favoritesResult.message}")
            }
            
            // 2. Sincronizar configurações
            val settingsResult = syncSettingsRemoteToLocal()
            if (settingsResult is Result.Error) {
                Log.w(TAG, "Falha na sincronização de configurações: ${settingsResult.message}")
            }
            
            Log.d(TAG, "Sincronização remoto -> local concluída")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Erro durante sincronização remoto -> local", e)
            Result.Error("Falha na sincronização: ${e.message}", e)
        }
    }
    
    /**
     * Sincroniza favoritos da nuvem para o local.
     */
    private suspend fun syncFavoritesRemoteToLocal(): Result<Unit> {
        return try {
            val remoteFavorites = userRepository.getFavoriteHymnIds().first()
            
            when (remoteFavorites) {
                is Result.Success -> {
                    val remoteIds = remoteFavorites.data
                    val localIds = localFavoritesRepository.getFavorites().first()
                    
                    // Adicionar favoritos remotos que não existem localmente
                    val newFavorites = remoteIds - localIds
                    newFavorites.forEach { hymnId ->
                        localFavoritesRepository.addFavorite(hymnId)
                    }
                    
                    // Remover favoritos locais que não existem remotamente
                    val removedFavorites = localIds - remoteIds
                    removedFavorites.forEach { hymnId ->
                        localFavoritesRepository.removeFavorite(hymnId)
                    }
                    
                    if (newFavorites.isNotEmpty() || removedFavorites.isNotEmpty()) {
                        Log.d(TAG, "Favoritos sincronizados: +${newFavorites.size} -${removedFavorites.size}")
                    }
                }
                is Result.Error -> {
                    Log.w(TAG, "Erro ao obter favoritos remotos: ${remoteFavorites.message}")
                    return Result.Error("Falha ao obter favoritos remotos")
                }
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Erro na sincronização de favoritos", e)
            Result.Error("Falha na sincronização de favoritos: ${e.message}", e)
        }
    }
    
    /**
     * Sincroniza configurações da nuvem para o local.
     */
    private suspend fun syncSettingsRemoteToLocal(): Result<Unit> {
        return try {
            val remoteSettings = userRepository.getUserSettings().first()
            
            when (remoteSettings) {
                is Result.Success -> {
                    val remoteTheme = remoteSettings.data.themeId
                    val localTheme = localSettingsRepository.theme.first()
                    
                    // Só atualiza se o tema remoto for diferente do local
                    if (remoteTheme != localTheme) {
                        localSettingsRepository.saveTheme(remoteTheme)
                        Log.d(TAG, "Tema sincronizado: $localTheme -> $remoteTheme")
                    }
                }
                is Result.Error -> {
                    Log.w(TAG, "Erro ao obter configurações remotas: ${remoteSettings.message}")
                    return Result.Error("Falha ao obter configurações remotas")
                }
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Erro na sincronização de configurações", e)
            Result.Error("Falha na sincronização de configurações: ${e.message}", e)
        }
    }
    
    /**
     * Verifica se há conflitos entre dados locais e remotos.
     * Retorna true se houver conflitos que precisam ser resolvidos.
     */
    suspend fun hasConflicts(): Boolean {
        return try {
            val remoteFavorites = userRepository.getFavoriteHymnIds().first()
            val localFavorites = localFavoritesRepository.getFavorites().first()
            
            if (remoteFavorites is Result.Success) {
                val hasFavoriteConflicts = remoteFavorites.data != localFavorites
                if (hasFavoriteConflicts) {
                    Log.d(TAG, "Conflito detectado em favoritos: local(${localFavorites.size}) != remoto(${remoteFavorites.data.size})")
                }
                return hasFavoriteConflicts
            }
            
            false
        } catch (e: Exception) {
            Log.w(TAG, "Erro ao verificar conflitos", e)
            false
        }
    }
    
    /**
     * Resolve conflitos usando estratégia de merge inteligente.
     */
    suspend fun resolveConflicts(): Result<Unit> {
        return try {
            Log.d(TAG, "Resolvendo conflitos entre dados locais e remotos...")
            
            // 1. Fazer backup dos dados locais
            val localBackup = LocalDataBackup(
                favorites = localFavoritesRepository.getFavorites().first(),
                theme = localSettingsRepository.theme.first(),
                timestamp = System.currentTimeMillis()
            )
            
            // 2. Obter dados remotos
            val remoteFavorites = userRepository.getFavoriteHymnIds().first()
            val remoteSettings = userRepository.getUserSettings().first()
            
            // 3. Aplicar estratégia de merge
            when {
                remoteFavorites is Result.Success && remoteSettings is Result.Success -> {
                    // Merge inteligente: união de favoritos, preferir tema local se for personalizado
                    val mergedFavorites = localBackup.favorites.union(remoteFavorites.data)
                    val mergedTheme = if (localBackup.theme != ThemeDefaults.THEME_ID) localBackup.theme else remoteSettings.data.themeId
                    
                    // 4. Salvar dados mesclados na nuvem
                    mergedFavorites.forEach { hymnId ->
                        userRepository.addFavorite(hymnId)
                    }
                    userRepository.updateUserSettings(remoteSettings.data.copy(themeId = mergedTheme))
                    
                    // 5. Atualizar dados locais
                    localFavoritesRepository.getFavorites().first().forEach { hymnId ->
                        if (!mergedFavorites.contains(hymnId)) {
                            localFavoritesRepository.removeFavorite(hymnId)
                        }
                    }
                    mergedFavorites.forEach { hymnId ->
                        if (!localBackup.favorites.contains(hymnId)) {
                            localFavoritesRepository.addFavorite(hymnId)
                        }
                    }
                    localSettingsRepository.saveTheme(mergedTheme)
                    
                    Log.d(TAG, "Conflitos resolvidos: favoritos=${mergedFavorites.size}, tema=$mergedTheme")
                    Result.Success(Unit)
                }
                else -> {
                    Log.w(TAG, "Não foi possível resolver conflitos devido a erro na obtenção de dados remotos")
                    Result.Error("Falha ao obter dados remotos para resolução de conflitos")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao resolver conflitos", e)
            Result.Error("Falha na resolução de conflitos: ${e.message}", e)
        }
    }
    
    /**
     * Classe interna para backup de dados locais.
     */
    private data class LocalDataBackup(
        val favorites: Set<String>,
        val theme: String,
        val timestamp: Long
    )
}
