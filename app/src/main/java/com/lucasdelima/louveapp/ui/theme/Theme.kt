package com.lucasdelima.louveapp.ui.theme

import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * CompositionLocal para prover os dados do tema customizado (`LouveThemeData`).
 */
private val LocalLouveTheme = staticCompositionLocalOf<LouveThemeData> {
    error("No LouveThemeData provided")
}

private val ColorScheme.isLight get() = this.background.luminance() > 0.5

@Composable
fun LouveAppTheme(
    themeData: LouveThemeData,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val isLight = themeData.colors.isLight

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            // Configura a aparência das barras de sistema baseada no tema
            // Isso garante que os ícones sejam visíveis sobre o fundo do tema
            insetsController.isAppearanceLightStatusBars = isLight
            insetsController.isAppearanceLightNavigationBars = isLight
        }
    }

    // O LouveAppTheme fornece os dados do tema e o MaterialTheme
    // O fundo do tema é desenhado na MainActivity para cobrir toda a tela
    // O enableEdgeToEdge() permite que o fundo se estenda até as bordas
    CompositionLocalProvider(LocalLouveTheme provides themeData) {
        MaterialTheme(
            colorScheme = themeData.colors,
            typography = themeData.typography,
            content = content
        )
    }
}

/**
 * Objeto para acessar as propriedades do tema ativo de forma fácil e segura.
 * 
 * Este objeto fornece acesso às cores, tipografia e fundos do tema ativo.
 * Os fundos são renderizados na MainActivity para cobrir toda a tela,
 * incluindo as áreas das barras de sistema graças ao enableEdgeToEdge().
 */
object LouveTheme {
    val colors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalLouveTheme.current.colors

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalLouveTheme.current.typography

    val backgrounds: LouveBackgrounds
        @Composable
        @ReadOnlyComposable
        get() = LocalLouveTheme.current.backgrounds
}
