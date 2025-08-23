package com.lucasdelima.louveapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// --- Paleta Padrão Claro ---
val LightThemeColors = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

// --- Paleta Escuro ---
val DarkThemeColors = darkColorScheme(
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

val SweetCandyColors = lightColorScheme(
    primary = CandyPink, onPrimary = DarkText,
    secondary = CandyBlue, onSecondary = DarkText,
    tertiary = LemonadeYellow, onTertiary = DarkText,
    background = OffWhite, onBackground = DarkText,
    surface = Color.White, onSurface = DarkText,
    surfaceVariant = Color(0xFFF1EBF2), onSurfaceVariant = DarkText,
    outline = Color(0xFFC8BFC9)
)

// --- NOVOS TEMAS ALINHADOS COM A FILOSOFIA DO APP ---

// --- Paleta "Aurora Matinal" - Inspiração e Novo Dia ---
private val AuroraGold = Color(0xFFFFD54F)
private val AuroraOrange = Color(0xFFFF8A65)
private val AuroraCream = Color(0xFFFFF8E1)
private val AuroraBrown = Color(0xFF5D4037)
private val AuroraWarm = Color(0xFFFFCC02)

val AuroraMorningColors = lightColorScheme(
    primary = AuroraGold, onPrimary = AuroraBrown,
    secondary = AuroraOrange, onSecondary = AuroraBrown,
    tertiary = AuroraWarm, onTertiary = AuroraBrown,
    background = AuroraCream, onBackground = AuroraBrown,
    surface = Color.White, onSurface = AuroraBrown,
    surfaceVariant = Color(0xFFFFF3E0), onSurfaceVariant = AuroraBrown,
    outline = AuroraGold,
    error = Color(0xFFD32F2F), onError = Color.White,
    errorContainer = Color(0xFFFFEBEE), onErrorContainer = Color(0xFFC62828)
)

// --- Paleta "Serenidade Noturna" - Reflexão e Paz ---
private val SerenityNavy = Color(0xFF1A237E)
private val SerenityBlue = Color(0xFF3949AB)
private val SerenityLight = Color(0xFFE8EAF6)
private val SerenityDark = Color(0xFF0D47A1)
private val SerenitySilver = Color(0xFF90A4AE)

val SerenityNightColors = lightColorScheme(
    primary = SerenityNavy, onPrimary = Color.White,
    secondary = SerenityBlue, onSecondary = Color.White,
    tertiary = SerenitySilver, onTertiary = SerenityNavy,
    background = SerenityLight, onBackground = SerenityNavy,
    surface = Color.White, onSurface = SerenityNavy,
    surfaceVariant = Color(0xFFC5CAE9), onSurfaceVariant = SerenityNavy,
    outline = SerenityBlue,
    error = Color(0xFFD32F2F), onError = Color.White,
    errorContainer = Color(0xFFFFEBEE), onErrorContainer = Color(0xFFC62828)
)

// --- Paleta "Vida Verde" - Crescimento e Esperança ---
private val LifeGreen = Color(0xFF4CAF50)
private val LifeEmerald = Color(0xFF66BB6A)
private val LifeMint = Color(0xFFE8F5E8)
private val LifeForest = Color(0xFF2E7D32)
private val LifeSage = Color(0xFF81C784)

val LifeGreenColors = lightColorScheme(
    primary = LifeGreen, onPrimary = Color.White,
    secondary = LifeEmerald, onSecondary = Color.White,
    tertiary = LifeSage, onTertiary = LifeForest,
    background = LifeMint, onBackground = LifeForest,
    surface = Color.White, onSurface = LifeForest,
    surfaceVariant = Color(0xFFC8E6C9), onSurfaceVariant = LifeForest,
    outline = LifeGreen,
    error = Color(0xFFD32F2F), onError = Color.White,
    errorContainer = Color(0xFFFFEBEE), onErrorContainer = Color(0xFFC62828)
)

// --- Paleta "Chama Sagrada" - Paixão e Adoração ---
private val SacredRed = Color(0xFFD32F2F)
private val SacredOrange = Color(0xFFFF5722)
private val SacredAmber = Color(0xFFFF9800)
private val SacredCream = Color(0xFFFFF8E1)
private val SacredBrown = Color(0xFF5D4037)

val SacredFlameColors = lightColorScheme(
    primary = SacredRed, onPrimary = Color.White,
    secondary = SacredOrange, onSecondary = Color.White,
    tertiary = SacredAmber, onTertiary = SacredBrown,
    background = SacredCream, onBackground = SacredBrown,
    surface = Color.White, onSurface = SacredBrown,
    surfaceVariant = Color(0xFFFFE0B2), onSurfaceVariant = SacredBrown,
    outline = SacredRed,
    error = Color(0xFFD32F2F), onError = Color.White,
    errorContainer = Color(0xFFFFEBEE), onErrorContainer = Color(0xFFC62828)
)

// --- Paleta "Céu Celestial" - Elevação e Transcendência ---
private val CelestialBlue = Color(0xFF1976D2)
private val CelestialLight = Color(0xFF42A5F5)
private val CelestialSky = Color(0xFFE3F2FD)
private val CelestialDark = Color(0xFF0D47A1)
private val CelestialSilver = Color(0xFF90CAF9)

val CelestialSkyColors = lightColorScheme(
    primary = CelestialBlue, onPrimary = Color.White,
    secondary = CelestialLight, onSecondary = Color.White,
    tertiary = CelestialSilver, onTertiary = CelestialDark,
    background = CelestialSky, onBackground = CelestialDark,
    surface = Color.White, onSurface = CelestialDark,
    surfaceVariant = Color(0xFFBBDEFB), onSurfaceVariant = CelestialDark,
    outline = CelestialBlue,
    error = Color(0xFFD32F2F), onError = Color.White,
    errorContainer = Color(0xFFFFEBEE), onErrorContainer = Color(0xFFC62828)
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
    id = "default_light",
    name = "Padrão Claro",
    category = ThemeCategory.Light,
    colors = LightThemeColors,
    typography = Typography,
    backgrounds = LouveBackgrounds(
        screenBackground = { SolidColorBackground(LightThemeColors.background) },
        detailScreenBackground = { SolidColorBackground(LightThemeColors.surface) }
    ),
    isDefault = true
)

val DarkTheme = LouveThemeData(
    id = "dark",
    name = "Escuro",
    category = ThemeCategory.Dark,
    colors = DarkThemeColors,
    typography = Typography,
    backgrounds = LouveBackgrounds(
        screenBackground = { SolidColorBackground(DarkThemeColors.background) },
        detailScreenBackground = { SolidColorBackground(DarkThemeColors.surface) }
    )
)

val SweetCandyTheme = LouveThemeData(
    id = "sweet_candy",
    name = "Sweet Candy",
    category = ThemeCategory.Custom,
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
                                CandyPink.copy(alpha = 0.5f),
                                CandyBlue.copy(alpha = 0.6f)
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

// --- NOVOS TEMAS FILOSÓFICOS ---

val AuroraMorningTheme = LouveThemeData(
    id = "aurora_morning",
    name = "Aurora Matinal",
    category = ThemeCategory.Custom,
    colors = AuroraMorningColors,
    typography = Typography,
    backgrounds = LouveBackgrounds(
        screenBackground = {
            // Fundo que simula o nascer do sol - inspiração e novo dia
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                AuroraGold.copy(alpha = 0.3f),      // Dourado no topo (nascer do sol)
                                AuroraOrange.copy(alpha = 0.4f),    // Laranja no meio (aurora)
                                AuroraCream.copy(alpha = 0.8f)      // Creme na base (manhã clara)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
        },
        detailScreenBackground = {
            // Fundo especial para a "salinha" - luz dourada suave
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                AuroraGold.copy(alpha = 0.2f),
                                AuroraCream.copy(alpha = 0.6f)
                            ),
                            radius = 1000f,
                            center = androidx.compose.ui.geometry.Offset(0f, 0f)
                        )
                    )
            )
        }
    )
)

val SerenityNightTheme = LouveThemeData(
    id = "serenity_night",
    name = "Serenidade Noturna",
    category = ThemeCategory.Custom,
    colors = SerenityNightColors,
    typography = Typography,
    backgrounds = LouveBackgrounds(
        screenBackground = {
            // Fundo que simula o céu noturno sereno - reflexão e paz
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                SerenityNavy.copy(alpha = 0.2f),    // Azul escuro no topo (céu noturno)
                                SerenityBlue.copy(alpha = 0.3f),    // Azul médio no meio (serenidade)
                                SerenityLight.copy(alpha = 0.9f)     // Azul claro na base (paz)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
        },
        detailScreenBackground = {
            // Fundo especial para a "salinha" - luz azul suave e contemplativa
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                SerenityLight.copy(alpha = 0.8f),
                                SerenityBlue.copy(alpha = 0.2f)
                            ),
                            radius = 1200f,
                            center = androidx.compose.ui.geometry.Offset(0f, 0f)
                        )
                    )
            )
        }
    )
)

val LifeGreenTheme = LouveThemeData(
    id = "life_green",
    name = "Vida Verde",
    category = ThemeCategory.Custom,
    colors = LifeGreenColors,
    typography = Typography,
    backgrounds = LouveBackgrounds(
        screenBackground = {
            // Fundo que simula um jardim vivo - crescimento e esperança
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                LifeGreen.copy(alpha = 0.2f),       // Verde no topo (crescimento)
                                LifeEmerald.copy(alpha = 0.3f),     // Esmeralda no meio (vida)
                                LifeMint.copy(alpha = 0.9f)         // Menta na base (esperança)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
        },
        detailScreenBackground = {
            // Fundo especial para a "salinha" - luz verde suave e revitalizante
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                LifeMint.copy(alpha = 0.7f),
                                LifeGreen.copy(alpha = 0.2f)
                            ),
                            radius = 1000f,
                            center = androidx.compose.ui.geometry.Offset(0f, 0f)
                        )
                    )
            )
        }
    )
)

val SacredFlameTheme = LouveThemeData(
    id = "sacred_flame",
    name = "Chama Sagrada",
    category = ThemeCategory.Custom,
    colors = SacredFlameColors,
    typography = Typography,
    backgrounds = LouveBackgrounds(
        screenBackground = {
            // Fundo que simula o fogo sagrado - paixão e adoração
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                SacredRed.copy(alpha = 0.2f),      // Vermelho no topo (chama)
                                SacredOrange.copy(alpha = 0.3f),    // Laranja no meio (fogo)
                                SacredCream.copy(alpha = 0.9f)      // Creme na base (luz)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
        },
        detailScreenBackground = {
            // Fundo especial para a "salinha" - luz dourada quente e devocional
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                SacredCream.copy(alpha = 0.8f),
                                SacredAmber.copy(alpha = 0.3f)
                            ),
                            radius = 1100f,
                            center = androidx.compose.ui.geometry.Offset(0f, 0f)
                        )
                    )
            )
        }
    )
)

val CelestialSkyTheme = LouveThemeData(
    id = "celestial_sky",
    name = "Céu Celestial",
    category = ThemeCategory.Custom,
    colors = CelestialSkyColors,
    typography = Typography,
    backgrounds = LouveBackgrounds(
        screenBackground = {
            // Fundo que simula o céu infinito - elevação e transcendência
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                CelestialBlue.copy(alpha = 0.2f),   // Azul no topo (céu profundo)
                                CelestialLight.copy(alpha = 0.3f),  // Azul claro no meio (elevação)
                                CelestialSky.copy(alpha = 0.9f)     // Azul celeste na base (transcendência)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
        },
        detailScreenBackground = {
            // Fundo especial para a "salinha" - luz azul celestial e inspiradora
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                CelestialSky.copy(alpha = 0.7f),
                                CelestialLight.copy(alpha = 0.3f)
                            ),
                            radius = 1300f,
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
val AllThemes = listOf(
    DefaultTheme, 
    DarkTheme, 
    SweetCandyTheme,
    AuroraMorningTheme,    // ✅ NOVO: Inspiração e novo dia
    SerenityNightTheme,    // ✅ NOVO: Reflexão e paz
    LifeGreenTheme,        // ✅ NOVO: Crescimento e esperança
    SacredFlameTheme,      // ✅ NOVO: Paixão e adoração
    CelestialSkyTheme      // ✅ NOVO: Elevação e transcendência
)
