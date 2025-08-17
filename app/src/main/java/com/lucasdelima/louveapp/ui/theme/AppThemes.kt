package com.lucasdelima.louveapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// (As definições de paletas de cores `LightThemeColors`, `DarkThemeColors`, `SweetCandyColors`, etc. continuam as mesmas de antes)

// --- Paleta Padrão Claro ---
private val LightThemeColors = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

// --- Paleta Escuro ---
private val DarkThemeColors = darkColorScheme(
    primary = Purple80, onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378A), onPrimaryContainer = Color(0xFFEADDFF),
    secondary = PurpleGrey80, onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458), onSecondaryContainer = Color(0xFFE8DEF8),
    tertiary = Pink80, onTertiary = Color(0xFF492532),
    tertiaryContainer = Color(0xFF633B48), onTertiaryContainer = Color(0xFFFFD8E4),
    background = Color(0xFF1C1B1F), onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F), onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454E), onSurfaceVariant = Color(0xFFCAC4CF),
    error = Color(0xFFF2B8B5), onError = Color(0xFF601410),
    outline = Color(0xFF938F99)
)

// --- Paleta Sweet Candy ---
private val CandyPink = Color(0xFFF48FB1)
private val CandyBlue = Color(0xFF81D4FA)
private val LemonadeYellow = Color(0xFFFFF176)
private val OffWhite = Color(0xFFFFFDFE)
private val DarkText = Color(0xFF211A1D)

private val SweetCandyColors = lightColorScheme(
    primary = CandyPink, onPrimary = DarkText,
    secondary = CandyBlue, onSecondary = DarkText,
    tertiary = LemonadeYellow, onTertiary = DarkText,
    background = OffWhite, onBackground = DarkText,
    surface = Color.White, onSurface = DarkText,
    surfaceVariant = Color(0xFFF1EBF2), onSurfaceVariant = DarkText,
    outline = Color(0xFFC8BFC9)
)


//------------------------------------------------------------------
// FUNDOS REUTILIZÁVEIS
//------------------------------------------------------------------
@Composable
private fun SolidColorBackground(color: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
    ) {}
}

//------------------------------------------------------------------
// DEFINIÇÕES COMPLETAS DOS TEMAS
//------------------------------------------------------------------

val DefaultTheme = LouveThemeData(
    name = "Padrão Claro",
    colors = LightThemeColors,
    typography = Typography,
    backgrounds = LouveBackgrounds(
        screenBackground = { SolidColorBackground(LightThemeColors.background) },
        detailScreenBackground = { SolidColorBackground(LightThemeColors.surface) }
    )
)

val DarkTheme = LouveThemeData(
    name = "Escuro",
    colors = DarkThemeColors,
    typography = Typography,
    backgrounds = LouveBackgrounds(
        screenBackground = { SolidColorBackground(DarkThemeColors.background) },
        detailScreenBackground = { SolidColorBackground(DarkThemeColors.surface) }
    )
)

val SweetCandyTheme = LouveThemeData(
    name = "Sweet Candy",
    colors = SweetCandyColors,
    typography = Typography,
    backgrounds = LouveBackgrounds(
        screenBackground = {
            // Este fundo é desenhado na MainActivity e cobre toda a tela
            // incluindo as áreas das barras de sistema
            // O enableEdgeToEdge() permite que o gradiente se estenda até as bordas
            // O fillMaxSize() garante que o fundo cubra toda a área disponível
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                CandyPink.copy(alpha = 0.2f),
                                CandyBlue.copy(alpha = 0.3f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
        },
        detailScreenBackground = {
            // Este fundo é desenhado por cima do fundo principal
            // para criar um efeito de camadas
            // O fillMaxSize() garante que o fundo cubra toda a área disponível
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                CandyPink.copy(alpha = 0.4f)
                            ), 
                            radius = 1200f,
                            center = androidx.compose.ui.geometry.Offset(0f, 0f)
                        )
                    )
            )
        }
    )
)


//------------------------------------------------------------------
// LISTA DE TODOS OS TEMAS DISPONÍVEIS
//------------------------------------------------------------------
val AllThemes = listOf(DefaultTheme, DarkTheme, SweetCandyTheme)
