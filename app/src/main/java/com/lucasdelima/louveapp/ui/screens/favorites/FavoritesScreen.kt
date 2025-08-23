package com.lucasdelima.louveapp.ui.screens.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lucasdelima.louveapp.ui.components.FavoritesTopAppBar
import com.lucasdelima.louveapp.ui.components.LouveBottomNavBar
import com.lucasdelima.louveapp.ui.screens.home.HymnUi
import com.lucasdelima.louveapp.ui.screens.home.components.HymnCardItem
import com.lucasdelima.louveapp.ui.screens.home.toHymnUi
import com.lucasdelima.louveapp.ui.theme.LouveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    bottomNavController: NavHostController,
    onHymnClick: (Int) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Cada tela agora tem seu próprio Scaffold
    Scaffold(
        topBar = {
            FavoritesTopAppBar()
        },
        bottomBar = {
            LouveBottomNavBar(navController = bottomNavController)
        },
        containerColor = Color.Transparent // Mantém o fundo personalizado
    ) { innerPadding ->
        // DESENHAMOS O FUNDO DO TEMA AQUI, DENTRO DA ÁREA DE CONTEÚDO
        Box(modifier = Modifier.fillMaxSize()) {
            // O fundo do tema é desenhado aqui, ocupando a tela inteira
            LouveTheme.backgrounds.screenBackground()
            
            // O conteúdo da tela vai aqui, usando o innerPadding do Scaffold
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.error != null) {
                    Text(
                        text = "Erro: ${uiState.error}",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else if (uiState.favoriteHymns.isEmpty()) {
                    EmptyFavoritesState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.favoriteHymns, key = { it.id }) { hymn: HymnUi ->
                            HymnCardItem(hymn = hymn, onClick = { onHymnClick(hymn.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFavoritesState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sua lista está vazia",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Toque no ícone de coração nos hinos que você mais ama para guardá-los aqui.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
