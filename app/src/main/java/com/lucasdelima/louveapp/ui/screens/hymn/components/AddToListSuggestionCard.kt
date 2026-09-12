package com.lucasdelima.louveapp.ui.screens.hymn.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lucasdelima.louveapp.domain.model.HymnList
import com.lucasdelima.louveapp.ui.screens.favorites.HymnListsUiState
import com.lucasdelima.louveapp.ui.screens.hymn.SuggestionInteraction
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
private const val PHASE1_MS = 1000

private const val PHASE2_MS = 1500

@Composable
fun AddToListSuggestionCard(
    visible: Boolean,
    interaction: SuggestionInteraction?,
    hymnListsUiState: HymnListsUiState,
    onDismiss: () -> Unit,
    onChooseList: () -> Unit,
    onCreateList: () -> Unit,
    onConfirmAddToList: (String) -> Unit,
    onConfirmCreateList: (String) -> Unit,
    onBackFromInteraction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var userInteracted by remember { mutableStateOf(false) }
    var dismissed by remember { mutableStateOf(false) }
    val fadeJob = remember { mutableStateOf<Job?>(null) }
    val alpha = remember { Animatable(1f) }

    fun cancelFade() {
        userInteracted = true
        fadeJob.value?.cancel()
        fadeJob.value = null
        scope.launch { alpha.snapTo(1f) }
    }

    LaunchedEffect(visible) {
        if (visible) {
            userInteracted = false
            dismissed = false
            alpha.snapTo(1f)

            fadeJob.value = launch {
                delay(PHASE1_MS.toLong())
                alpha.animateTo(0.85f, tween(PHASE1_MS, easing = LinearEasing))

                if (userInteracted) return@launch

                alpha.animateTo(0f, tween(PHASE2_MS, easing = LinearEasing))
                if (userInteracted) return@launch

                dismissed = true
                onDismiss()
            }
        }
    }

    AnimatedVisibility(
        visible = visible && !dismissed,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .alpha(alpha.value)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                .clickable(enabled = false) { }
        ) {
            when (interaction) {
                is SuggestionInteraction.ChooseList -> {
                    cancelFade()
                    ListSelectionContent(
                        lists = hymnListsUiState.lists,
                        onSelectList = onConfirmAddToList,
                        onBack = onBackFromInteraction
                    )
                }
                is SuggestionInteraction.CreateNewList -> {
                    cancelFade()
                    CreateListContent(
                        onConfirm = onConfirmCreateList,
                        onBack = onBackFromInteraction
                    )
                }
                null -> InitialSuggestionContent(
                    onChooseList = {
                        cancelFade()
                        onChooseList()
                    },
                    onCreateList = {
                        cancelFade()
                        onCreateList()
                    }
                )
            }
        }
    }
}

@Composable
private fun InitialSuggestionContent(
    onChooseList: () -> Unit,
    onCreateList: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Salvo nos favoritos!",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Deseja salvar numa lista separada?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onChooseList,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.LibraryMusic, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                Text("Adicionar à lista", style = MaterialTheme.typography.labelMedium)
            }
            Button(
                onClick = onCreateList,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                Text("Criar nova lista", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun ListSelectionContent(
    lists: List<HymnList>,
    onSelectList: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.padding(end = 4.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
            }
            Text(
                text = "Escolher lista",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (lists.isEmpty()) {
            Text(
                text = "Nenhuma lista criada ainda.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                items(lists) { list ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectList(list.id) }
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = list.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${list.hymnIds.size} hinos",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Adicionar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun CreateListContent(
    onConfirm: (String) -> Unit,
    onBack: () -> Unit
) {
    var listName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.padding(end = 4.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
            }
            Text(
                text = "Nova lista",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = listName,
            onValueChange = { listName = it },
            label = { Text("Nome da lista") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { if (listName.isNotBlank()) onConfirm(listName.trim()) },
            enabled = listName.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Criar")
        }
    }
}