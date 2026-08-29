package com.lucasdelima.louveapp.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import com.lucasdelima.louveapp.domain.repository.FavoritesRepository
import com.lucasdelima.louveapp.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Serviço responsável por monitorar mudanças de conectividade e automatizar
 * a sincronização de dados quando o usuário volta a ficar online.
 */
@Singleton
class ConnectivityMonitorService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: AuthRepository,
    private val favoritesRepository: FavoritesRepository,
    private val settingsRepository: SettingsRepository
) {
    companion object {
        private const val TAG = "ConnectivityMonitor"
    }
    
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private var isMonitoring = false
    private var lastNetworkState = false
    
    /**
     * Inicia o monitoramento de conectividade.
     */
    fun startMonitoring() {
        if (isMonitoring) {
            Log.d(TAG, "Monitoramento já está ativo")
            return
        }
        
        try {
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
            isMonitoring = true
            
            // Verificar estado inicial
            val currentState = isNetworkAvailable()
            lastNetworkState = currentState
            Log.d(TAG, "Monitoramento de conectividade iniciado. Estado atual: ${if (currentState) "Online" else "Offline"}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao iniciar monitoramento de conectividade", e)
        }
    }
    
    /**
     * Para o monitoramento de conectividade.
     */
    fun stopMonitoring() {
        if (!isMonitoring) {
            return
        }
        
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            isMonitoring = false
            Log.d(TAG, "Monitoramento de conectividade parado")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao parar monitoramento de conectividade", e)
        }
    }
    
    /**
     * Callback para mudanças de conectividade.
     */
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Log.d(TAG, "Rede disponível")
            handleConnectivityChange(true)
        }
        
        override fun onLost(network: Network) {
            Log.d(TAG, "Rede perdida")
            handleConnectivityChange(false)
        }
        
        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val hasValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            
            if (hasInternet && hasValidated && !lastNetworkState) {
                Log.d(TAG, "Conectividade de internet validada")
                handleConnectivityChange(true)
            }
        }
    }
    
    /**
     * Processa mudanças de conectividade.
     */
    private fun handleConnectivityChange(isOnline: Boolean) {
        if (lastNetworkState == isOnline) {
            return // Estado não mudou
        }
        
        lastNetworkState = isOnline
        
        if (isOnline) {
            Log.d(TAG, "Conectividade restaurada. Iniciando sincronização...")
            // Executar sincronização em background
            coroutineScope.launch {
                try {
                    // Verificar se o usuário está logado antes de sincronizar
                    val user = authRepository.getCurrentUser().first()
                    if (user != null) {
                        Log.d(TAG, "Usuário logado. Sincronizando dados...")
                        
                        // Sincronizar favoritos
                        val favoritesResult = favoritesRepository.syncWhenOnline()
                        when (favoritesResult) {
                            is com.lucasdelima.louveapp.domain.model.Result.Success<*> -> {
                                Log.d(TAG, "Favoritos sincronizados com sucesso")
                            }
                            is com.lucasdelima.louveapp.domain.model.Result.Error -> {
                                Log.w(TAG, "Falha na sincronização de favoritos: ${favoritesResult.message}")
                            }
                        }
                        
                        // Sincronizar configurações
                        val settingsResult = settingsRepository.syncWhenOnline()
                        when (settingsResult) {
                            is com.lucasdelima.louveapp.domain.model.Result.Success<*> -> {
                                Log.d(TAG, "Configurações sincronizadas com sucesso")
                            }
                            is com.lucasdelima.louveapp.domain.model.Result.Error -> {
                                Log.w(TAG, "Falha na sincronização de configurações: ${settingsResult.message}")
                            }
                        }
                        
                        // Verificar e resolver conflitos se necessário
                        if (favoritesRepository.checkForConflicts()) {
                            Log.d(TAG, "Conflitos detectados. Resolvendo...")
                            val conflictResult = favoritesRepository.resolveConflicts()
                            when (conflictResult) {
                                is com.lucasdelima.louveapp.domain.model.Result.Success<*> -> {
                                    Log.d(TAG, "Conflitos resolvidos com sucesso")
                                }
                                is com.lucasdelima.louveapp.domain.model.Result.Error -> {
                                    Log.w(TAG, "Falha na resolução de conflitos: ${conflictResult.message}")
                                }
                            }
                        }
                        
                        Log.d(TAG, "Sincronização completa concluída")
                    } else {
                        Log.d(TAG, "Usuário não logado. Pulando sincronização.")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Erro durante sincronização automática", e)
                }
            }
        } else {
            Log.d(TAG, "Conectividade perdida. Modo offline ativado.")
        }
    }
    
    /**
     * Verifica se a rede está disponível.
     */
    private fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    /**
     * Cleanup quando o serviço não for mais necessário.
     * Para monitoramento e cancela o escopo de coroutines.
     */
    fun cleanup() {
        stopMonitoring()
        coroutineScope.cancel()
        Log.d(TAG, "ConnectivityMonitorService finalizado")
    }
    
    /**
     * Força uma verificação de conectividade e sincronização.
     */
    fun forceSync() {
        coroutineScope.launch {
            try {
                val user = authRepository.getCurrentUser().first()
                if (user != null && isNetworkAvailable()) {
                    Log.d(TAG, "Sincronização forçada iniciada...")
                    
                    favoritesRepository.syncWhenOnline()
                    settingsRepository.syncWhenOnline()
                    
                    if (favoritesRepository.checkForConflicts()) {
                        favoritesRepository.resolveConflicts()
                    }
                    
                    Log.d(TAG, "Sincronização forçada concluída")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro durante sincronização forçada", e)
            }
        }
    }
}
