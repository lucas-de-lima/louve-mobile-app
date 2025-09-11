package com.lucasdelima.louveapp.ui.screens.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lucasdelima.louveapp.ui.navigation.BottomNavItem
import com.lucasdelima.louveapp.ui.screens.discover.DiscoverScreen
import com.lucasdelima.louveapp.ui.screens.favorites.FavoritesScreen
import com.lucasdelima.louveapp.ui.screens.home.HomeScreen
import com.lucasdelima.louveapp.ui.screens.hymn.HymnDetailScreen
import com.lucasdelima.louveapp.ui.screens.hymn.HymnDetailViewModel
import com.lucasdelima.louveapp.ui.screens.more.MoreScreen
import com.lucasdelima.louveapp.ui.screens.settings.AuthViewModel
import com.lucasdelima.louveapp.ui.screens.settings.SettingsViewModel

@Composable
fun MainScreen(rootNavController: NavHostController) {
    val bottomNavController = rememberNavController()
    
    // ViewModels para gerenciar estado do usuário e temas
    val authViewModel: AuthViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    
    // Estados observados
    val userProfile by authViewModel.userProfile.collectAsState()
    val settingsUiState by settingsViewModel.uiState.collectAsState()

    // O NavHost agora é o componente principal. Sem Scaffold ao redor dele.
    // Usando as animações padrão nativas do Android - suaves e familiares
    NavHost(
        navController = bottomNavController,
        startDestination = BottomNavItem.Harpa.route,
        modifier = Modifier.fillMaxSize()
    ) {
        // Tela principal da harpa
        composable(route = BottomNavItem.Harpa.route) {
            HomeScreen(
                bottomNavController = bottomNavController,
                onHymnSelected = { hymnId ->
                    bottomNavController.navigate("hymnDetail/$hymnId")
                },
                onProfileClick = {
                    rootNavController.navigate("profile")
                },
                onThemeSelected = { themeName ->
                    settingsViewModel.selectTheme(themeName)
                },
                currentTheme = settingsUiState.selectedThemeName,
                userProfile = userProfile
            )
        }

        // Tela de favoritos
        composable(route = BottomNavItem.Favorites.route) {
            FavoritesScreen(
                bottomNavController = bottomNavController,
                onHymnClick = { hymnId ->
                    bottomNavController.navigate("hymnDetail/$hymnId")
                }
            )
        }

        // Tela de descoberta
        composable(route = BottomNavItem.Discover.route) {
            DiscoverScreen(
                bottomNavController = bottomNavController
            )
        }

        // Tela mais (centro de controle)
        composable(route = BottomNavItem.More.route) {
            MoreScreen(
                bottomNavController = bottomNavController,
                onNavigateToProfile = { rootNavController.navigate("profile") },
                onNavigateToSettings = { rootNavController.navigate("settings") },
                onNavigateToAbout = { rootNavController.navigate("about") },
                onNavigateToSupport = { rootNavController.navigate("support") }
            )
        }

        // Tela de detalhes do hino (sem bottom bar)
        composable(
            route = "hymnDetail/{hymnId}",
            arguments = listOf(
                navArgument("hymnId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val hymnId = backStackEntry.arguments?.getInt("hymnId") ?: 0
            
            // ViewModel instanciado no lugar certo
            val viewModel: HymnDetailViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            // Define o hymnId no ViewModel quando a tela é carregada
            LaunchedEffect(hymnId) {
                viewModel.setHymnId(hymnId)
            }

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
