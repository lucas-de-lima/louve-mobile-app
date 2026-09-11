package com.lucasdelima.louveapp.ui.screens.hymn.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lucasdelima.louveapp.domain.model.HymnList
import com.lucasdelima.louveapp.ui.screens.favorites.HymnListsViewModel

@Composable
fun AddToListBottomSheet(
    hymnListViewModel: HymnListsViewModel,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onSelectList: (String) -> Unit,
    onCreateNewList: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val listsState by hymnListViewModel.uiState.collectAsState()
    var showCreateSheet by remember { mutableStateOf(false) }

    if (showCreateSheet) {
        CreateListBottomSheet(
            onDismiss = { showCreateSheet = false },
            onCreated = { listId ->
                showCreateSheet = false
                onDismiss()
            }
        )
        return
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Adicionar a...",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))

            FilledTonalButton(
                onClick = onToggleFavorite,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isFavorite) "Remover de Favoritos" else "Meus Favoritos")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (listsState.lists.isNotEmpty()) {
                Text(
                    text = "Listas de Culto",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                listsState.lists.forEach { list ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectList(list.id) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = list.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "${list.hymnIds.size} hinos",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    HorizontalDivider()
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            FilledTonalButton(
                onClick = onCreateNewList,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Criar nova lista de culto...")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}