package com.lucasdelima.louveapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Arquivo de preview para testar todos os temas filosóficos do Louve App.
 * 
 * Este arquivo permite aos desenvolvedores visualizar como cada tema se comporta
 * antes de testar no dispositivo, garantindo que as cores, tipografia e fundos
 * estejam funcionando corretamente.
 */

@Preview(showBackground = true, name = "Aurora Matinal")
@Composable
fun AuroraMorningThemePreview() {
    LouveAppTheme(themeData = AuroraMorningTheme) {
        ThemePreviewContent(
            themeName = "Aurora Matinal",
            themeDescription = "Inspiração e Novo Dia",
            themeColors = AuroraMorningColors
        )
    }
}

@Preview(showBackground = true, name = "Serenidade Noturna")
@Composable
fun SerenityNightThemePreview() {
    LouveAppTheme(themeData = SerenityNightTheme) {
        ThemePreviewContent(
            themeName = "Serenidade Noturna",
            themeDescription = "Reflexão e Paz",
            themeColors = SerenityNightColors
        )
    }
}

@Preview(showBackground = true, name = "Vida Verde")
@Composable
fun LifeGreenThemePreview() {
    LouveAppTheme(themeData = LifeGreenTheme) {
        ThemePreviewContent(
            themeName = "Vida Verde",
            themeDescription = "Crescimento e Esperança",
            themeColors = LifeGreenColors
        )
    }
}

@Preview(showBackground = true, name = "Chama Sagrada")
@Composable
fun SacredFlameThemePreview() {
    LouveAppTheme(themeData = SacredFlameTheme) {
        ThemePreviewContent(
            themeName = "Chama Sagrada",
            themeDescription = "Paixão e Adoração",
            themeColors = SacredFlameColors
        )
    }
}

@Preview(showBackground = true, name = "Céu Celestial")
@Composable
fun CelestialSkyThemePreview() {
    LouveAppTheme(themeData = CelestialSkyTheme) {
        ThemePreviewContent(
            themeName = "Céu Celestial",
            themeDescription = "Elevação e Transcendência",
            themeColors = CelestialSkyColors
        )
    }
}

@Preview(showBackground = true, name = "Sweet Candy")
@Composable
fun SweetCandyThemePreview() {
    LouveAppTheme(themeData = SweetCandyTheme) {
        ThemePreviewContent(
            themeName = "Sweet Candy",
            themeDescription = "Alegria e Celebração",
            themeColors = SweetCandyColors
        )
    }
}

@Preview(showBackground = true, name = "Escuro")
@Composable
fun DarkThemePreview() {
    LouveAppTheme(themeData = DarkTheme) {
        ThemePreviewContent(
            themeName = "Escuro",
            themeDescription = "Sobreidade e Reverência",
            themeColors = DarkThemeColors
        )
    }
}

@Preview(showBackground = true, name = "Padrão Claro")
@Composable
fun DefaultThemePreview() {
    LouveAppTheme(themeData = DefaultTheme) {
        ThemePreviewContent(
            themeName = "Padrão Claro",
            themeDescription = "Clareza e Simplicidade",
            themeColors = LightThemeColors
        )
    }
}

/**
 * Conteúdo de preview que demonstra os elementos principais de cada tema.
 */
@Composable
private fun ThemePreviewContent(
    themeName: String,
    themeDescription: String,
    themeColors: ColorScheme
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cabeçalho do tema
        Text(
            text = themeName,
            style = MaterialTheme.typography.headlineLarge,
            color = themeColors.onBackground,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = themeDescription,
            style = MaterialTheme.typography.bodyLarge,
            color = themeColors.onSurfaceVariant
        )
        
        // Demonstração de cores principais
        ColorPaletteSection(themeColors)
        
        // Demonstração de componentes
        ComponentsSection(themeColors)
        
        // Demonstração de tipografia
        TypographySection(themeColors)
        
        // Demonstração de fundos
        BackgroundsSection()
    }
}

@Composable
private fun ColorPaletteSection(colors: ColorScheme) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Paleta de Cores",
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorSwatch("Primary", colors.primary, colors.onPrimary)
                ColorSwatch("Secondary", colors.secondary, colors.onSecondary)
                ColorSwatch("Tertiary", colors.tertiary, colors.onTertiary)
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ColorSwatch("Surface", colors.surface, colors.onSurface)
                ColorSwatch("Background", colors.background, colors.onBackground)
                ColorSwatch("Error", colors.error, colors.onError)
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    label: String,
    color: Color,
    textColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color, shape = MaterialTheme.shapes.small)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Composable
private fun ComponentsSection(colors: ColorScheme) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Componentes",
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
            ) {
                Text("Botão Primário", color = colors.onPrimary)
            }
            
            OutlinedButton(
                onClick = { },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colors.primary
                )
            ) {
                Text("Botão Secundário")
            }
            
            TextField(
                value = "Campo de texto",
                onValueChange = { },
                label = { Text("Label") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface,
                    focusedLabelColor = colors.primary,
                    unfocusedLabelColor = colors.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun TypographySection(colors: ColorScheme) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tipografia",
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = "Headline Large",
                style = MaterialTheme.typography.headlineLarge,
                color = colors.onBackground
            )
            
            Text(
                text = "Title Medium",
                style = MaterialTheme.typography.titleMedium,
                color = colors.onBackground
            )
            
            Text(
                text = "Body Large",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onBackground
            )
            
            Text(
                text = "Label Small",
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BackgroundsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Fundos Contextuais",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = "Este tema inclui fundos contextuais especiais:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "• Fundo principal para navegação",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "• Fundo especial para detalhes dos hinos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Preview de todos os temas lado a lado para comparação.
 */
@Preview(showBackground = true, name = "Comparação de Temas")
@Composable
fun AllThemesComparisonPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Comparação de Todos os Temas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        // Lista de todos os temas disponíveis
        AllThemes.forEach { theme ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = theme.colors.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = theme.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = theme.colors.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "Categoria: ${theme.category.javaClass.simpleName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = theme.colors.onSurfaceVariant
                    )
                    
                    Text(
                        text = "ID: ${theme.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = theme.colors.onSurfaceVariant
                    )
                }
            }
        }
    }
}
