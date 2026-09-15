package com.lucasdelima.louveapp

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import com.lucasdelima.louveapp.data.repository.ConnectivityMonitorService
import com.lucasdelima.louveapp.data.repository.SyncWorkerFactory
import com.lucasdelima.louveapp.domain.repository.HymnListRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class LouveApp : Application(), Configuration.Provider {

    @Inject
    lateinit var connectivityMonitor: ConnectivityMonitorService

    @Inject
    lateinit var hymnListRepository: HymnListRepository

    @Inject
    lateinit var syncWorkerFactory: SyncWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(syncWorkerFactory)
            .setMinimumLoggingLevel(Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        WorkManager.initialize(this, workManagerConfiguration)
        connectivityMonitor.startMonitoring()
        CoroutineScope(Dispatchers.IO).launch {
            hymnListRepository.cleanupExpiredLists()
        }
    }

    override fun onTerminate() {
        connectivityMonitor.cleanup()
        super.onTerminate()
    }
}