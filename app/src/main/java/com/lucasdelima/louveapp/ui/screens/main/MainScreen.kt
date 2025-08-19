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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lucasdelima.louveapp.ui.components.LouveBottomNavBar
import com.lucasdelima.louveapp.ui.navigation.BottomNavItem
import com.lucasdelima.louveapp.ui.screens.favorites.FavoritesScreen
import com.lucasdelima.louveapp.ui.screens.home.HomeScreen
import com.lucasdelima.louveapp.ui.screens.discover.DiscoverScreen
import com.lucasdelima.louveapp.ui.screens.more.MoreScreen
import com.lucasdelima.louveapp.ui.theme.LouveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(rootNavController: NavHostController) {
    val bottomNavController = rememberNavController()
    
    // Estado para controlar qual TopAppBar deve ser renderizada
    var topBarState by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }
    
    // Estado para controlar qual fundo deve ser exibido
    var currentBackground by remember {
        mutableStateOf<@Composable () -> Unit>({ LouveTheme.backgrounds.screenBackground() })
    }
    
    // Estado para controlar a visibilidade da BottomBar
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val isBottomBarVisible = when (currentRoute) {
        // Lista de rotas onde a barra deve ser escondida
        "hymnDetail/{hymnId}" -> false
        else -> true
    }

    // Atualiza o fundo baseado na rota atual
    LaunchedEffect(currentRoute) {
        currentBackground = when {
            currentRoute?.startsWith("hymnDetail/") == true -> {
                // Se a rota for de detalhes, use o fundo de detalhe
                { LouveTheme.backgrounds.detailScreenBackground() }
            }
            else -> {
                // Para todas as outras rotas, use o fundo padrão
                { LouveTheme.backgrounds.screenBackground() }
            }
        }
    }

    // Scaffold central que gerencia toda a estrutura da UI
    Scaffold(
        // TopAppBar dinâmica controlada pelas telas filhas
        topBar = { topBarState?.invoke() },
        
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
        containerColor = androidx.compose.ui.graphics.Color.Transparent
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
                    HomeScreen(
                        onHymnSelected = { hymnId ->
                            bottomNavController.navigate("hymnDetail/$hymnId")
                        },
                        onSettingsClick = {
                            rootNavController.navigate("settings")
                        },
                        onComposingTopBar = { topBar -> topBarState = topBar }
                    )
                }
                
                // Tela de favoritos
                composable(BottomNavItem.Favorites.route) {
                    FavoritesScreen(
                        onHymnClick = { hymnId ->
                            bottomNavController.navigate("hymnDetail/$hymnId")
                        },
                        onComposingTopBar = { topBar -> topBarState = topBar }
                    )
                }
                
                // Tela de descoberta
                composable(BottomNavItem.Discover.route) {
                    DiscoverScreen(
                        onComposingTopBar = { topBar -> topBarState = topBar }
                    )
                }
                
                // Tela mais (centro de controle)
                composable(BottomNavItem.More.route) {
                    MoreScreen(
                        onNavigateToProfile = { rootNavController.navigate("profile") },
                        onNavigateToSettings = { rootNavController.navigate("settings") },
                        onNavigateToAbout = { rootNavController.navigate("about") },
                        onNavigateToSupport = { rootNavController.navigate("support") },
                        onComposingTopBar = { topBar -> topBarState = topBar }
                    )
                }
                
                // Tela de detalhes do hino (esconde a bottom bar)
                composable(
                    route = "hymnDetail/{hymnId}",
                    arguments = listOf(
                        androidx.navigation.navArgument("hymnId") { 
                            type = androidx.navigation.NavType.IntType 
                        }
                    )
                ) { backStackEntry ->
                    val hymnId = backStackEntry.arguments?.getInt("hymnId") ?: 0
                    com.lucasdelima.louveapp.ui.screens.hymn.HymnDetailScreen(
                        onBack = { bottomNavController.popBackStack() }
                    )
                }
            }
        }
    }
}
