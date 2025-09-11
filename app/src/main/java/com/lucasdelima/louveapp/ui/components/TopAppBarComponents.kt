package com.lucasdelima.louveapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.ui.semantics.Role
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.ui.screens.hymn.HymnDetailUiState
import com.lucasdelima.louveapp.ui.theme.AllThemes
import com.lucasdelima.louveapp.ui.theme.LouveThemeData
import androidx.compose.material.icons.filled.Brush

/**
 * Botão de seleção de temas com dropdown
 */
@Composable
fun ThemeSelectorButton(
    currentTheme: String,
    onThemeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .clickable { expanded = true },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Brush,
                contentDescription = "Temas",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Temas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        AllThemes.forEach { theme ->
            DropdownMenuItem(
                text = {
                    ThemePreviewItem(
                        themeData = theme,
                        isSelected = theme.name == currentTheme,
                        onSelected = {
                            onThemeSelected(theme.name)
                            expanded = false
                        }
                    )
                },
                onClick = {
                    onThemeSelected(theme.name)
                    expanded = false
                }
            )
        }
    }
}

/**
 * Item de preview do tema para o dropdown
 */
@Composable
private fun ThemePreviewItem(
    themeData: LouveThemeData,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .selectable(
                selected = isSelected,
                onClick = onSelected,
                role = Role.RadioButton
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Preview do fundo do tema
            themeData.backgrounds.screenBackground()

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = themeData.colors.primary,
                        unselectedColor = themeData.colors.onSurfaceVariant,
                        disabledSelectedColor = themeData.colors.primary.copy(alpha = 0.38f),
                        disabledUnselectedColor = themeData.colors.onSurfaceVariant.copy(alpha = 0.38f)
                    )
                )

                Text(
                    text = themeData.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = themeData.colors.onSurface
                )

                Spacer(modifier = Modifier.weight(1f))

                // Indicador visual do tema selecionado
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Tema selecionado",
                        tint = themeData.colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Botão de perfil do usuário
 */
@Composable
fun ProfileButton(
    userProfile: UserProfile?,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { onProfileClick() },
        contentAlignment = Alignment.Center
    ) {
        if (userProfile != null) {
            // Usuário logado - mostrar inicial do nome ou foto se disponível
            Text(
                text = (userProfile.name ?: "U").take(1).uppercase(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            // Usuário deslogado - mostrar ícone de pessoa
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Perfil",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Componente especializado para a TopAppBar da tela principal (Harpa)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    currentTheme: String,
    onThemeSelected: (String) -> Unit,
    userProfile: UserProfile?,
    onProfileClick: () -> Unit
) {
    TopAppBar(
        title = { 
            Text(
                "Louve App", 
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp)
            )
        },
        actions = {
            // Botão de temas
            ThemeSelectorButton(
                currentTheme = currentTheme,
                onThemeSelected = onThemeSelected
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Botão de perfil
            ProfileButton(
                userProfile = userProfile,
                onProfileClick = onProfileClick
            )

            Spacer(modifier = Modifier.width(16.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

/**
 * Componente especializado para a TopAppBar da tela de favoritos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesTopAppBar() {
    CenterAlignedTopAppBar(
        title = { Text("Favoritos", color = MaterialTheme.colorScheme.onSurface) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

/**
 * Componente especializado para a TopAppBar da tela de descoberta
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverTopAppBar() {
    CenterAlignedTopAppBar(
        title = { Text("Descubra", color = MaterialTheme.colorScheme.onSurface) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

/**
 * Componente especializado para a TopAppBar da tela mais
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreTopAppBar() {
    CenterAlignedTopAppBar(
        title = { Text("Mais", color = MaterialTheme.colorScheme.onSurface) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

/**
 * Componente especializado para a TopAppBar da tela de detalhes do hino
 * Recebe o estado e as ações necessárias para funcionar corretamente
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HymnDetailTopAppBar(
    uiState: HymnDetailUiState,
    onBackClick: () -> Unit,
    onIncreaseFont: () -> Unit,
    onDecreaseFont: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = uiState.hymn?.number?.toString()?.padStart(3, '0') ?: "...",
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar"
                )
            }
        },
        actions = {
            IconButton(
                onClick = onDecreaseFont,
                enabled = uiState.fontScaleFactor > 0.5f
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Diminuir fonte"
                )
            }
            IconButton(
                onClick = onIncreaseFont,
                enabled = uiState.fontScaleFactor < 2.0f
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Aumentar fonte"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}
