package com.lucasdelima.louveapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.lucasdelima.louveapp.ui.screens.hymn.HymnDetailUiState

/**
 * Componente especializado para a TopAppBar da tela principal (Harpa)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(onSettingsClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("Louve App") },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Configurações"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
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
        title = { Text("Favoritos") },
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
        title = { Text("Descubra") },
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
        title = { Text("Mais") },
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
                text = uiState.hymn?.number?.toString()?.padStart(3, '0') ?: "..."
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
