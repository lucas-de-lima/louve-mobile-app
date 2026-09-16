package com.lucasdelima.louveapp.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityMonitorService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val syncScheduler: SyncScheduler
) {
    companion object {
        private const val TAG = "ConnectivityMonitor"
    }

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var isMonitoring = false
    private var lastNetworkState = false

    fun startMonitoring() {
        if (isMonitoring) return
        try {
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
            isMonitoring = true
            lastNetworkState = isNetworkAvailable()
            Log.d(TAG, "Monitoramento iniciado. Estado: ${if (lastNetworkState) "Online" else "Offline"}")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao iniciar monitoramento", e)
        }
    }

    fun stopMonitoring() {
        if (!isMonitoring) return
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            isMonitoring = false
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao parar monitoramento", e)
        }
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            handleConnectivityChange(true)
        }

        override fun onLost(network: Network) {
            handleConnectivityChange(false)
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val hasValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            if (hasInternet && hasValidated && !lastNetworkState) {
                handleConnectivityChange(true)
            }
        }
    }

    private fun handleConnectivityChange(isOnline: Boolean) {
        if (lastNetworkState == isOnline) return
        lastNetworkState = isOnline

        if (isOnline) {
            Log.d(TAG, "Conectividade restaurada. Agendando sincronização via WorkManager...")
            syncScheduler.scheduleSync()
        } else {
            Log.d(TAG, "Conectividade perdida. Modo offline ativado.")
        }
    }

    fun forceSync() {
        Log.d(TAG, "Sincronização forçada solicitada")
        syncScheduler.scheduleSync()
    }

    private fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    fun cleanup() {
        stopMonitoring()
        Log.d(TAG, "ConnectivityMonitorService finalizado")
    }
}