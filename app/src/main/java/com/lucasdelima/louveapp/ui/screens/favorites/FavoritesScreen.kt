package com.lucasdelima.louveapp.ui.screens.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    onListClick: (String) -> Unit = {},
    viewModel: FavoritesViewModel = hiltViewModel(),
    hymnListsViewModel: HymnListsViewModel = hiltViewModel()
) {
    val favoritesState by viewModel.uiState.collectAsState()
    val listsState by hymnListsViewModel.uiState.collectAsState()
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabTitles = listOf("Favoritos", "Listas")

    Scaffold(
        topBar = {
            FavoritesTopAppBar()
        },
        bottomBar = {
            LouveBottomNavBar(navController = bottomNavController)
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> FavoritesTabContent(
                    isLoading = favoritesState.isLoading,
                    error = favoritesState.error,
                    hymns = favoritesState.favoriteHymns,
                    onHymnClick = onHymnClick
                )
                1 -> HymnListsTabContent(
                    listsState = listsState,
                    onListClick = onListClick
                )
            }
        }
    }
}

@Composable
private fun FavoritesTabContent(
    isLoading: Boolean,
    error: String?,
    hymns: List<HymnUi>,
    onHymnClick: (Int) -> Unit
) {
    when {
        isLoading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }

        error != null -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Erro: $error",
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        hymns.isEmpty() -> EmptyFavoritesState()

        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(hymns, key = { it.id }) { hymn ->
                HymnCardItem(hymn = hymn, onClick = { onHymnClick(hymn.id) })
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
