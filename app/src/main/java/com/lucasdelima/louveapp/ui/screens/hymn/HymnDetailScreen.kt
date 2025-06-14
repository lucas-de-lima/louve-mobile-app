package com.lucasdelima.louveapp.ui.screens.hymn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lucasdelima.louveapp.domain.model.Hymn
import com.lucasdelima.louveapp.ui.theme.LouveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HymnDetailScreen(
    onBack: () -> Unit,
    viewModel: HymnDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    // O Box externo desenha o fundo especial desta tela por cima do fundo principal.
    Box(modifier = Modifier.fillMaxSize()) {
        LouveTheme.backgrounds.detailScreenBackground()
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(uiState.hymn?.number?.toString()?.padStart(3, '0') ?: "...") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                        }
                    },
                    actions = {
                        IconButton(onClick = viewModel::decreaseFontSize) {
                            Icon(Icons.Default.KeyboardArrowDown, "Diminuir Fonte")
                        }
                        IconButton(onClick = viewModel::increaseFontSize) {
                            Icon(Icons.Default.KeyboardArrowUp, "Aumentar Fonte")
                        }
                    },
                    // CORREÇÃO: Deixa a barra do topo transparente
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            bottomBar = {
                BottomAppBar(
                    // CORREÇÃO: Deixa a barra inferior transparente
                    containerColor = Color.Transparent,
                    actions = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            IconButton(onClick = { /* TODO */ }) {
                                Icon(Icons.Default.FavoriteBorder, "Curtir")
                            }
                            IconButton(onClick = { /* TODO */ }) {
                                Icon(Icons.Default.Share, "Compartilhar")
                            }
                        }
                    }
                )
            },
            // CORREÇÃO: Deixa o corpo do Scaffold transparente para revelar o fundo global
            containerColor = Color.Transparent
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading -> CircularProgressIndicator()
                    uiState.error != null -> Text(
                        "Erro: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error
                    )

                    uiState.hymn != null -> HymnContent(
                        hymn = uiState.hymn!!,
                        fontScaleFactor = uiState.fontScaleFactor
                    )
                }
            }
        }
    }
}

@Composable
private fun HymnContent(hymn: Hymn, fontScaleFactor: Float, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = hymn.title,
            style = MaterialTheme.typography.headlineMedium,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize * fontScaleFactor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        val formattedLyrics = buildAnnotatedString {
            hymn.verses.forEachIndexed { index, verse ->
                if (index > 0) append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("${index + 1}\n")
                }
                append("$verse\n")
            }
            hymn.chorus?.let {
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Coro\n")
                }
                append(it)
            }
        }

        Text(
            text = formattedLyrics,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize * fontScaleFactor,
            lineHeight = MaterialTheme.typography.bodyLarge.fontSize * 1.5 * fontScaleFactor
        )
    }
}