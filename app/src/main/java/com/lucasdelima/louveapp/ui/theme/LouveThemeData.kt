package com.lucasdelima.louveapp.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

/**
 * Define os diferentes tipos de fundo que um tema pode ter.
 */
data class LouveBackgrounds(
    /**
     * O fundo para telas de navegação e listas (Home, Configurações).
     */
    val screenBackground: @Composable () -> Unit,

    /**
     * O fundo "especial" e imersivo para a tela de detalhes de um hino.
     */
    val detailScreenBackground: @Composable () -> Unit
)

/**
 * Representa o DNA completo de um tema no Louve App.
 */
data class LouveThemeData(
    val name: String,
    val colors: ColorScheme,
    val typography: Typography,
    val backgrounds: LouveBackgrounds
)
