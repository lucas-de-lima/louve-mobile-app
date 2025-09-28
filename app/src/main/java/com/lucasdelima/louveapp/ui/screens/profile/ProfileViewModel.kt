package com.lucasdelima.louveapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.lucasdelima.louveapp.domain.repository.AnalyticsService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val analyticsService: AnalyticsService
) : ViewModel() {

    fun trackUserLogout() {
        analyticsService.trackUserLogout()
    }

    fun trackScreenView() {
        analyticsService.trackScreenView("ProfileScreen")
    }
    
    fun trackLoginAttempt() {
        analyticsService.trackUserLogin()
    }
}
