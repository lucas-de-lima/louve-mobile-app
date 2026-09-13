package com.lucasdelima.louveapp.ui.screens.hymn

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lucasdelima.louveapp.domain.model.Hymn
import com.lucasdelima.louveapp.ui.components.HymnDetailTopAppBar
import com.lucasdelima.louveapp.ui.components.HymnTextFormatter
import com.lucasdelima.louveapp.ui.components.HymnTitleFormatter
import com.lucasdelima.louveapp.ui.screens.favorites.HymnListsViewModel
import com.lucasdelima.louveapp.ui.screens.hymn.components.AddToListSuggestionCard
import com.lucasdelima.louveapp.ui.theme.LouveTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HymnDetailScreen(
    uiState: HymnDetailUiState,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onAddHymnToList: (String) -> Unit = {},
    onCreateList: (String) -> Unit = {},
    onDismissSuggestion: () -> Unit = {},
    onSuggestionChooseList: () -> Unit = {},
    onSuggestionCreateList: () -> Unit = {},
    onSuggestionBack: () -> Unit = {},
    hymnListsViewModel: HymnListsViewModel = hiltViewModel(),
    viewModel: HymnDetailViewModel? = null
) {
    LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showShareSheet by remember { mutableStateOf(false) }
    val listsState by hymnListsViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel?.eventFlow?.collect { event ->
            when (event) {
                is HymnDetailViewModel.HymnDetailEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    if (showShareSheet) {
        uiState.hymn?.let { hymnToShare ->
            ShareBottomSheet(
                hymn = hymnToShare,
                onDismiss = { showShareSheet = false }
            )
        }
    }

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
                tonalElevation = 0.dp,
                actions = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onToggleFavorite,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.clip(RoundedCornerShape(16.dp))
                        ) {
                            Icon(
                                imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (uiState.isFavorite) "Desfavoritar" else "Favoritar",
                                tint = if (uiState.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        IconButton(
                            onClick = { showShareSheet = true },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.clip(RoundedCornerShape(16.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Compartilhar",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LouveTheme.backgrounds.detailScreenBackground()

            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                when {
                    uiState.isLoading -> CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                    uiState.error != null -> Text(
                        "Erro: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    uiState.hymn != null -> HymnContent(
                        hymn = uiState.hymn!!,
                        fontScaleFactor = uiState.fontScaleFactor,
                        onIncreaseFont = onIncreaseFontSize,
                        onDecreaseFont = onDecreaseFontSize
                    )
                }

                AddToListSuggestionCard(
                    visible = uiState.showAddToListSuggestion,
                    interaction = uiState.suggestionInteraction,
                    hymnListsUiState = listsState,
                    onDismiss = onDismissSuggestion,
                    onChooseList = onSuggestionChooseList,
                    onCreateList = onSuggestionCreateList,
                    onConfirmAddToList = onAddHymnToList,
                    onConfirmCreateList = onCreateList,
                    onBackFromInteraction = onSuggestionBack,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )

                uiState.successMessage?.let { msg ->
                    SuccessMessageOverlay(
                        message = msg,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 80.dp)
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
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
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

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = hymn.number.toString() + " - " + hymn.title,
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
                shape = RoundedCornerShape(16.dp),
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
        HymnTitleFormatter(
            title = hymn.title,
            fontScaleFactor = fontScaleFactor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        HymnTextFormatter(
            hymn = hymn,
            fontScaleFactor = fontScaleFactor
        )
    }
}

@Composable
private fun SuccessMessageOverlay(
    message: String,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(true) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(2000),
        label = "successAlpha"
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2500)
        visible = false
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(2000))
    ) {
        Card(
            modifier = modifier
                .padding(horizontal = 24.dp)
                .alpha(alpha),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}