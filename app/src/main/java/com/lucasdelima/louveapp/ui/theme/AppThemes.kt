package com.lucasdelima.louveapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Manteremos nossas paletas de cores aqui
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

// Definição do nosso Tema Padrão (já deve existir)
val DefaultTheme = LouveThemeData(
    name = "Padrão Claro",
    colors = lightColorScheme( // Usando o builder aqui para clareza
        primary = Purple40,
        secondary = PurpleGrey40,
        tertiary = Pink40
    ),
    typography = Typography,
    screenBackground = null
)


// Definição do Tema Escuro - Paleta de cores para o Tema Escuro
private val DarkThemeColors = darkColorScheme(
    primary = Purple80,
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378A),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = PurpleGrey80,
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),
    tertiary = Pink80,
    onTertiary = Color(0xFF492532),
    tertiaryContainer = Color(0xFF633B48),
    onTertiaryContainer = Color(0xFFFFD8E4),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454E),
    onSurfaceVariant = Color(0xFFCAC4CF),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    outline = Color(0xFF938F99)
)

val DarkTheme = LouveThemeData(
    name = "Escuro",
    colors = DarkThemeColors,
    typography = Typography,
    screenBackground = null
)

// --- Paleta de Cores para o Tema Sweet Candy ---
val CandyPink = Color(0xFFF48FB1)      // Rosa doce
val CandyBlue = Color(0xFF81D4FA)       // Azul céu
val CandyPurple = Color(0xFFCE93D8)    // Roxo claro
val Lemonade = Color(0xFFFFF59D)       // Amarelo limonada
val OffWhite = Color(0xFFF8F8F8)       // Um branco suave para o fundo

private val SweetCandyColors = lightColorScheme(
    primary = CandyPink,
    secondary = CandyPurple,
    tertiary = Lemonade,
    background = OffWhite,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

// NOVO: Definição do Tema Sweet Candy
val SweetCandyTheme = LouveThemeData(
    name = "Sweet Candy",
    colors = SweetCandyColors,
    typography = Typography,
    screenBackground = { // Exemplo de um fundo customizado com gradiente sutil
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            CandyPink.copy(alpha = 0.3f),   // Um rosa mais suave e transparente no topo
                            CandyBlue.copy(alpha = 0.5f)    // Um azul um pouco mais forte e transparente na base
                        )
                    )
                )
        )
    }
)

// Lista com todos os temas disponíveis que usaremos na UI
val AllThemes = listOf(DefaultTheme, DarkTheme, SweetCandyTheme)