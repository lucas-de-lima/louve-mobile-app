package com.lucasdelima.louveapp.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lucasdelima.louveapp.ui.components.DiscoverTopAppBar
import com.lucasdelima.louveapp.ui.components.FavoritesTopAppBar
import com.lucasdelima.louveapp.ui.components.HomeTopAppBar
import com.lucasdelima.louveapp.ui.components.HymnDetailTopAppBar
import com.lucasdelima.louveapp.ui.components.MoreTopAppBar
import com.lucasdelima.louveapp.ui.components.LouveBottomNavBar
import com.lucasdelima.louveapp.ui.navigation.BottomNavItem
import com.lucasdelima.louveapp.ui.screens.discover.DiscoverScreen
import com.lucasdelima.louveapp.ui.screens.favorites.FavoritesScreen
import com.lucasdelima.louveapp.ui.screens.home.HomeScreen
import com.lucasdelima.louveapp.ui.screens.hymn.HymnDetailScreen
import com.lucasdelima.louveapp.ui.screens.hymn.HymnDetailViewModel
import com.lucasdelima.louveapp.ui.screens.more.MoreScreen
import com.lucasdelima.louveapp.ui.theme.LouveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(rootNavController: NavHostController) {
    val bottomNavController = rememberNavController()

    // Estado para controlar a visibilidade da BottomBar
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // PASSO 1: Estado para a TopAppBar (controlado pelo bloco composable)
    val topBarState = remember {
        mutableStateOf<@Composable () -> Unit>({})
    }
    val topBar = topBarState.value

    // Estado derivado para o fundo atual (mais declarativo)
    val currentBackground by remember(currentRoute) {
        mutableStateOf<@Composable () -> Unit>({
            when {
                currentRoute?.startsWith("hymnDetail/") == true -> {
                    LouveTheme.backgrounds.detailScreenBackground()
                }

                else -> {
                    LouveTheme.backgrounds.screenBackground()
                }
            }
        })
    }

    val isBottomBarVisible = when {
        // Esconde em qualquer rota de detalhes de hino
        currentRoute?.startsWith("hymnDetail/") == true -> false
        else -> true
    }

    // Scaffold central que gerencia toda a estrutura da UI
    Scaffold(
        // TopAppBar dinâmica controlada pelo estado
        topBar = topBar,
        // BottomBar com animação de visibilidade
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                LouveBottomNavBar(navController = bottomNavController)
            }
        },

        // Container transparente para permitir que o fundo personalizado seja desenhado
        containerColor = Color.Transparent
    ) { innerPadding ->
        // DESENHAMOS O FUNDO AQUI, DENTRO DA ÁREA DE CONTEÚDO
        // Ele vai preencher o espaço atrás do NavHost e respeitar o Scaffold
        Box(modifier = Modifier.fillMaxSize()) {
            // O fundo atual é desenhado aqui, ocupando a tela inteira
            currentBackground()

            // NavHost aninhado para o conteúdo principal
            // Este NavHost renderiza as telas de navegação inferior
            NavHost(
                navController = bottomNavController,
                startDestination = BottomNavItem.Harpa.route,
                modifier = Modifier.padding(innerPadding) // ✅ MÁGICA AQUI
            ) {
                // Tela principal da harpa
                composable(BottomNavItem.Harpa.route) {
                    // PASSO 2: Cada tela define seu próprio header usando componentes especializados
                    LaunchedEffect(Unit) {
                        topBarState.value = {
                            HomeTopAppBar(
                                onSettingsClick = { rootNavController.navigate("settings") }
                            )
                        }
                    }
                    
                    HomeScreen(
                        onHymnSelected = { hymnId ->
                            bottomNavController.navigate("hymnDetail/$hymnId")
                        },
                        onSettingsClick = {
                            rootNavController.navigate("settings")
                        }
                    )
                }

                // Tela de favoritos
                composable(BottomNavItem.Favorites.route) {
                    LaunchedEffect(Unit) {
                        topBarState.value = {
                            FavoritesTopAppBar()
                        }
                    }
                    
                    FavoritesScreen(
                        onHymnClick = { hymnId ->
                            bottomNavController.navigate("hymnDetail/$hymnId")
                        }
                    )
                }

                // Tela de descoberta
                composable(BottomNavItem.Discover.route) {
                    LaunchedEffect(Unit) {
                        topBarState.value = {
                            DiscoverTopAppBar()
                        }
                    }
                    
                    DiscoverScreen()
                }

                // Tela mais (centro de controle)
                composable(BottomNavItem.More.route) {
                    LaunchedEffect(Unit) {
                        topBarState.value = {
                            MoreTopAppBar()
                        }
                    }
                    
                    MoreScreen(
                        onNavigateToProfile = { rootNavController.navigate("profile") },
                        onNavigateToSettings = { rootNavController.navigate("settings") },
                        onNavigateToAbout = { rootNavController.navigate("about") },
                        onNavigateToSupport = { rootNavController.navigate("support") }
                    )
                }

                // Tela de detalhes do hino (esconde a bottom bar)
                // PASSO 3: O bloco composable orquestra tudo usando componentes especializados
                composable(
                    route = "hymnDetail/{hymnId}",
                    arguments = listOf(
                        navArgument("hymnId") {
                            type = NavType.IntType
                        }
                    )
                ) { backStackEntry ->
                    val hymnId = backStackEntry.arguments?.getInt("hymnId") ?: 0
                    
                    // 1. ViewModel instanciado no lugar certo
                    val viewModel: HymnDetailViewModel = hiltViewModel()
                    val uiState by viewModel.uiState.collectAsState()

                    // 2. Define o hymnId no ViewModel quando a tela é carregada
                    LaunchedEffect(hymnId) {
                        viewModel.setHymnId(hymnId)
                    }

                    // 3. TopAppBar construída usando componente especializado
                    LaunchedEffect(uiState) {
                        topBarState.value = {
                            HymnDetailTopAppBar(
                                uiState = uiState,
                                onBackClick = { bottomNavController.popBackStack() },
                                onIncreaseFont = viewModel::increaseFontSize,
                                onDecreaseFont = viewModel::decreaseFontSize
                            )
                        }
                    }

                    // 4. Tela simplificada (sem ViewModel próprio)
                    HymnDetailScreen(
                        uiState = uiState,
                        onBack = { bottomNavController.popBackStack() },
                        onToggleFavorite = viewModel::onToggleFavorite,
                        onIncreaseFontSize = viewModel::increaseFontSize,
                        onDecreaseFontSize = viewModel::decreaseFontSize
                    )
                }
            }
        }
    }
}
