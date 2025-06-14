package com.lucasdelima.louveapp.ui.screens.hymn

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lucasdelima.louveapp.domain.model.Hymn
import com.lucasdelima.louveapp.ui.theme.LouveAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HymnDetailScreen(
    onBack: () -> Unit,
    viewModel: HymnDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
                }
            )
        },
        bottomBar = {
            BottomAppBar(
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
        }
    ) { innerPadding ->
        // O Box agora só é usado para posicionar o Loading ou o Erro no centro.
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        "Erro: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.hymn != null -> {
                    // Quando temos o hino, usamos o layout original, sem o Box centralizado.
                    HymnContent(
                        hymn = uiState.hymn!!,
                        fontScaleFactor = uiState.fontScaleFactor
                    )
                }
            }
        }
    }
}

// O conteúdo do hino, que usa o layout original que gostamos
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

        // Corrigindo a formatação da letra com AnnotatedString
        val formattedLyrics = buildAnnotatedString {
            hymn.verses.forEachIndexed { index, verse ->
                if (index > 0) append("\n") // Espaço entre os versos
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
