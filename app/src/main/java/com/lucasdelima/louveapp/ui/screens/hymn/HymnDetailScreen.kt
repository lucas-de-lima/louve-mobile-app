package com.lucasdelima.louveapp.ui.screens.hymn

import android.content.Intent
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lucasdelima.louveapp.domain.model.Hymn
import com.lucasdelima.louveapp.ui.theme.LouveTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HymnDetailScreen(
    onBack: () -> Unit,
    viewModel: HymnDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // --- LÓGICA PARA AS NOVAS FUNCIONALIDADES ---
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showShareSheet by remember { mutableStateOf(false) } // Estado para controlar a folha de compartilhamento

    // Exibe a folha de compartilhamento customizada se showShareSheet for true
    if (showShareSheet) {
        uiState.hymn?.let { hymnToShare ->
            ShareBottomSheet(
                hymn = hymnToShare,
                onDismiss = { showShareSheet = false }
            )
        }
    }

    // ------------------------------------------

    Box(modifier = Modifier.fillMaxSize()) {
        LouveTheme.backgrounds.detailScreenBackground()

        Scaffold(
            // Adiciona o host do Snackbar para exibir nossas mensagens
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = Color.Transparent,
                    actions = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // --- BOTÃO DE FAVORITOS (Placeholder) ---
                            IconButton(onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Função de Favoritos em breve! ❤️", // Mensagem gentil
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }) {
                                Icon(Icons.Default.FavoriteBorder, "Curtir")
                            }

                            // --- BOTÃO DE COMPARTILHAR (Abre a nossa folha customizada) ---
                            IconButton(onClick = {
                                showShareSheet = true // Apenas mostra a folha
                            }) {
                                Icon(Icons.Default.Share, "Compartilhar")
                            }
                        }
                    }
                )
            },
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

/**
 * Uma folha de compartilhamento ("bottom sheet") que mostra uma pré-visualização
 * bonita do conteúdo antes de compartilhar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareBottomSheet(
    hymn: Hymn,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Compartilhar Hino", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            // Card de pré-visualização
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${hymn.number} - ${hymn.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = hymn.verses.firstOrNull() ?: "Confira este hino no Louve App!",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão final para compartilhar
            Button(
                onClick = {
                    // --- O TEXTO DE COMPARTILHAMENTO APRIMORADO ---
                    val firstVerse = hymn.verses.firstOrNull()?.replace("\n", " ") ?: ""
                    val shareText = """
                    📖 *${hymn.title} (Hino ${hymn.number})*
                    
                    _"${firstVerse}"_
                    
                    Enviado pelo Louve App! 🎵
                    (Link para a loja em breve)
                    """.trimIndent()
                    // ---------------------------------------------

                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Compartilhar Hino")

                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            context.startActivity(shareIntent)
                            onDismiss()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("COMPARTILHAR AGORA")
            }
            Spacer(modifier = Modifier.height(16.dp))
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