package com.lucasdelima.louveapp.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush

/**
 * Define todas as propriedades customizáveis de um tema no Louve App.
 *
 * @param colors O esquema de cores padrão do Material 3.
 * @param typography A definição da tipografia do app.
 * @param screenBackground Um Composable opcional para ser usado como plano de fundo global das telas.
 * Permite o uso de cores sólidas, gradientes ou imagens.
 */
data class LouveThemeData(
    val name: String,
    val colors: ColorScheme,
    val typography: Typography,
    val screenBackground: @Composable (() -> Unit)? = null
)