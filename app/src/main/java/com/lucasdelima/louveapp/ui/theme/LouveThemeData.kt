package com.lucasdelima.louveapp.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

/**
 * Define os diferentes tipos de fundo que um tema pode ter.
 * 
 * - screenBackground: Fundo para telas de navegação e listas (Home, Configurações)
 *   Este fundo é desenhado na MainActivity e cobre toda a tela, incluindo as áreas das barras de sistema
 *   graças ao enableEdgeToEdge() que permite que o conteúdo se estenda até as bordas
 * 
 * - detailScreenBackground: Fundo "especial" e imersivo para a tela de detalhes de um hino
 *   Este fundo é desenhado por cima do fundo principal para criar um efeito de camadas
 */
data class LouveBackgrounds(
    /**
     * O fundo para telas de navegação e listas (Home, Configurações).
     * Este fundo é desenhado na MainActivity e cobre toda a tela,
     * incluindo as áreas das barras de sistema.
     */
    val screenBackground: @Composable () -> Unit,

    /**
     * O fundo "especial" e imersivo para a tela de detalhes de um hino.
     * Este fundo é desenhado por cima do fundo principal.
     */
    val detailScreenBackground: @Composable () -> Unit
)

/**
 * Representa o DNA completo de um tema no Louve App.
 * Cada tema define suas cores, tipografia e fundos personalizados.
 * Os fundos são renderizados de forma a cobrir toda a tela,
 * incluindo as áreas das barras de sistema.
 */
data class LouveThemeData(
    val name: String,
    val colors: ColorScheme,
    val typography: Typography,
    val backgrounds: LouveBackgrounds
)
