package com.lucasdelima.louveapp.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lucasdelima.louveapp.ui.screens.home.components.HymnCardItem
import com.lucasdelima.louveapp.ui.screens.home.components.SearchField
import com.lucasdelima.louveapp.ui.theme.LouveTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onHymnSelected: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    // 1. Crie e lembre o estado da lista e um escopo de corrotina
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // 2. Use um LaunchedEffect para "observar" a query de busca
    LaunchedEffect(uiState.searchQuery) {
        // Se a busca for limpa, role para o topo da lista
        if (uiState.searchQuery.isBlank()) {
            scope.launch {
                listState.animateScrollToItem(0)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LouveTheme.backgrounds.screenBackground() // Usa o fundo padrão de tela
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Louve App") }, // Pode ser o nome do seu app
                    // Deixa a TopAppBar transparente também para se mesclar ao gradiente
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    actions = {
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Configurações"
                            )
                        }
                    }
                )
            },
            containerColor = Color.Transparent
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
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        state = listState // 3. Passe o estado para o LazyColumn
                    ) {
                        items(uiState.hymns, key = { it.id }) { hymn ->
                            HymnCardItem(hymn = hymn) {
                                onHymnSelected(hymn.id)
                            }
                        }
                    }
                }
            }
        }
    }
}