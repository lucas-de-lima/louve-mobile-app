package com.lucasdelima.louveapp

import android.app.Application
import com.lucasdelima.louveapp.data.repository.ConnectivityMonitorService
import com.lucasdelima.louveapp.domain.repository.HymnListRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class LouveApp : Application() {

    @Inject
    lateinit var connectivityMonitor: ConnectivityMonitorService

    @Inject
    lateinit var hymnListRepository: HymnListRepository

    override fun onCreate() {
        super.onCreate()
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