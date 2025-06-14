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
            insetsController.isAppearanceLightStatusBars = isLight
            insetsController.isAppearanceLightNavigationBars = isLight
        }
    }

    // O LouveAppTheme agora só fornece os dados. Cada tela desenha seu próprio fundo.
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
