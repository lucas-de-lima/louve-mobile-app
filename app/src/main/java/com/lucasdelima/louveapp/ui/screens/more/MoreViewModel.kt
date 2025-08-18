package com.lucasdelima.louveapp.ui.screens.more

import androidx.lifecycle.ViewModel
import com.lucasdelima.louveapp.domain.repository.AnalyticsService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val analyticsService: AnalyticsService
) : ViewModel() {

    fun trackShareApp() {
        analyticsService.trackShareApp()
    }

    fun trackScreenView() {
        analyticsService.trackScreenView("MoreScreen")
    }
}
