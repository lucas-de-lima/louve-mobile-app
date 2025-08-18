package com.lucasdelima.louveapp.ui.screens.support

import androidx.lifecycle.ViewModel
import com.lucasdelima.louveapp.domain.repository.AnalyticsService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SupportViewModel @Inject constructor(
    private val analyticsService: AnalyticsService
) : ViewModel() {

    fun trackSupportTicketSent() {
        analyticsService.trackSupportTicketSent()
    }

    fun trackScreenView() {
        analyticsService.trackScreenView("SupportScreen")
    }
}
