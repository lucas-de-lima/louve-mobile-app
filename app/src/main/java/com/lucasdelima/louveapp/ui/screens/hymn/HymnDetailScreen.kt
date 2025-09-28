package com.lucasdelima.louveapp.ui.screens.hymn

import android.content.Intent
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.gestures.TransformableState
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.lucasdelima.louveapp.domain.model.Hymn
import com.lucasdelima.louveapp.ui.components.HymnDetailTopAppBar
import com.lucasdelima.louveapp.ui.components.HymnTextFormatter
import com.lucasdelima.louveapp.ui.components.HymnTitleFormatter
import kotlinx.coroutines.launch
import com.lucasdelima.louveapp.ui.theme.LouveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HymnDetailScreen(
    uiState: HymnDetailUiState,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit
) {
    LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    rememberCoroutineScope()
    var showShareSheet by remember { mutableStateOf(false) }

    if (showShareSheet) {
        uiState.hymn?.let { hymnToShare ->
            ShareBottomSheet(
                hymn = hymnToShare,
                onDismiss = { showShareSheet = false }
            )
        }
    }

    // Cada tela agora tem seu próprio Scaffold
    Scaffold(
        topBar = {
            HymnDetailTopAppBar(
                uiState = uiState,
                onBackClick = onBack,
                onIncreaseFont = onIncreaseFontSize,
                onDecreaseFont = onDecreaseFontSize
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Transparent,
                actions = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = onToggleFavorite) {
                            Icon(
                                imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (uiState.isFavorite) "Desfavoritar" else "Favoritar",
                                tint = if (uiState.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(onClick = {
                            showShareSheet = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Compartilhar",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        // DESENHAMOS O FUNDO ESPECIAL DO TEMA AQUI, DENTRO DA ÁREA DE CONTEÚDO
        Box(modifier = Modifier.fillMaxSize()) {
            // O fundo especial para tela de detalhes é desenhado aqui, ocupando a tela inteira
            LouveTheme.backgrounds.detailScreenBackground()
            
            // O conteúdo da tela vai aqui, usando o innerPadding do Scaffold
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
                        fontScaleFactor = uiState.fontScaleFactor,
                        onIncreaseFont = onIncreaseFontSize,
                        onDecreaseFont = onDecreaseFontSize
                    )
                }
            }
        }
    }
}

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
            Text(
                "Compartilhar Hino",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
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
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = hymn.verses.firstOrNull() ?: "Confira este hino no Louve App!",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão final para compartilhar
            Button(
                onClick = {
                    val firstVerse = hymn.verses.firstOrNull()?.replace("\n", " ") ?: ""
                    val shareText = """
                    📖 *${hymn.title} (Hino ${hymn.number})*
                    
                    _"${firstVerse}"_
                    
                    Enviado pelo Louve App! 🎵
                    (Link para a loja em breve)
                    """.trimIndent()

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
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("COMPARTILHAR AGORA")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HymnContent(
    hymn: Hymn,
    fontScaleFactor: Float,
    onIncreaseFont: () -> Unit,
    onDecreaseFont: () -> Unit,
    modifier: Modifier = Modifier
) {
    var accumulatedZoom by remember { mutableFloatStateOf(1f) }
    val zoomThreshold = 0.25f

    // ✅ MELHORIA 4: Memoizar o transformState para evitar recriação
    val transformState = remember {
        TransformableState { zoomChange, _, _ ->
            accumulatedZoom *= zoomChange
            if (accumulatedZoom >= 1f + zoomThreshold) {
                onIncreaseFont()
                accumulatedZoom = 1f
            } else if (accumulatedZoom <= 1f - zoomThreshold) {
                onDecreaseFont()
                accumulatedZoom = 1f
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .transformable(state = transformState)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // ✅ MELHORIA 5: Usar componente reutilizável para título
        HymnTitleFormatter(
            title = hymn.title,
            fontScaleFactor = fontScaleFactor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        // ✅ MELHORIA 5: Usar componente reutilizável para texto do hino
        HymnTextFormatter(
            hymn = hymn,
            fontScaleFactor = fontScaleFactor
        )
    }
}
