package com.lucasdelima.louveapp.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lucasdelima.louveapp.ui.screens.home.components.HymnCardItem // Mude para o novo Card
import com.lucasdelima.louveapp.ui.screens.home.components.SearchField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onHymnSelected: (Int) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hinos") } // Pode ser o nome do seu app
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Nosso novo campo de busca estilizado
            SearchField(
                query = uiState.searchQuery,
                onQueryChanged = viewModel::onSearchQueryChanged // Referência direta da função
            )

            // Lógica de exibição da lista ou loading/erro
            if (uiState.isLoading && uiState.hymns.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (!uiState.isLoading && uiState.hymns.isEmpty() && uiState.searchQuery.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhum hino encontrado para \"${uiState.searchQuery}\"")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(uiState.hymns, key = { it.id }) { hymn ->
                        // Usando nosso novo Card!
                        HymnCardItem(hymn = hymn) {
                            onHymnSelected(hymn.id)
                        }
                    }
                }
            }
        }
    }
}