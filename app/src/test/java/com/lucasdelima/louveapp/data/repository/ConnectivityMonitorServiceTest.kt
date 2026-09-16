package com.lucasdelima.louveapp.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class ConnectivityMonitorServiceTest {

    private val context: Context = mockk(relaxed = true)
    private val connectivityManager: ConnectivityManager = mockk(relaxUnitFun = true)
    private val syncScheduler: SyncScheduler = mockk(relaxed = true)
    private lateinit var service: ConnectivityMonitorService

    @Before
    fun setup() {
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        service = ConnectivityMonitorService(context, syncScheduler)
    }

    @After
    fun tearDown() {
        service.cleanup()
    }

    @Test
    fun startMonitoring_doesNotThrow() {
        service.startMonitoring()
    }

    @Test
    fun startMonitoring_isIdempotent() {
        service.startMonitoring()
        service.startMonitoring()
    }

    @Test
    fun stopMonitoring_doesNotThrow() {
        service.stopMonitoring()
    }

    @Test
    fun stopMonitoring_afterStart_doesNotThrow() {
        service.startMonitoring()
        service.stopMonitoring()
    }

    @Test
    fun cleanup_stopsMonitoring() {
        service.startMonitoring()
        service.cleanup()
    }

    @Test
    fun forceSync_callsSyncScheduler() {
        service.forceSync()
        verify(exactly = 1) { syncScheduler.scheduleSync() }
    }
}