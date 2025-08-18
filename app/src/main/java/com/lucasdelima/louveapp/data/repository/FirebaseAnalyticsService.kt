package com.lucasdelima.louveapp.data.repository

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.lucasdelima.louveapp.domain.repository.AnalyticsService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do AnalyticsService usando Firebase Analytics.
 * Esta classe é responsável por formatar os dados e chamar o SDK do Firebase.
 */
@Singleton
class FirebaseAnalyticsService @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsService {

    override fun trackUserLogin() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, null)
    }

    override fun trackUserLogout() {
        firebaseAnalytics.logEvent("user_logout", null)
    }

    override fun trackScreenView(screenName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Screen")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    override fun trackHymnViewed(hymnId: String, duration: Int) {
        val bundle = Bundle().apply {
            putString("hymn_id", hymnId)
            putInt("duration_seconds", duration)
        }
        firebaseAnalytics.logEvent("hymn_viewed", bundle)
    }

    override fun trackShareApp() {
        firebaseAnalytics.logEvent("share_app", null)
    }

    override fun trackSupportTicketSent() {
        firebaseAnalytics.logEvent("support_ticket_sent", null)
    }

    override fun trackHymnFavorited(hymnId: String) {
        val bundle = Bundle().apply {
            putString("hymn_id", hymnId)
        }
        firebaseAnalytics.logEvent("hymn_favorited", bundle)
    }

    override fun trackHymnUnfavorited(hymnId: String) {
        val bundle = Bundle().apply {
            putString("hymn_id", hymnId)
        }
        firebaseAnalytics.logEvent("hymn_unfavorited", bundle)
    }

    override fun trackSearchPerformed(query: String, resultCount: Int) {
        val bundle = Bundle().apply {
            putString("search_query", query)
            putInt("result_count", resultCount)
        }
        firebaseAnalytics.logEvent("search_performed", bundle)
    }
}
