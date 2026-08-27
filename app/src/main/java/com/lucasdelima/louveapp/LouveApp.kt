package com.lucasdelima.louveapp

import android.app.Application
import com.lucasdelima.louveapp.data.repository.ConnectivityMonitorService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class LouveApp : Application() {

    @Inject
    lateinit var connectivityMonitor: ConnectivityMonitorService

    override fun onCreate() {
        super.onCreate()
        connectivityMonitor.startMonitoring()
    }

    override fun onTerminate() {
        connectivityMonitor.cleanup()
        super.onTerminate()
    }
}