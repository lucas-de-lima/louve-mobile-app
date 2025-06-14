package com.lucasdelima.louveapp.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val theme: Flow<String> // Um Flow para "escutar" mudanças no tema em tempo real
    suspend fun saveTheme(themeName: String)
}