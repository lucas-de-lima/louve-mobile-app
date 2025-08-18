package com.lucasdelima.louveapp.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lucasdelima.louveapp.ui.components.LouveBottomNavBar
import com.lucasdelima.louveapp.ui.navigation.BottomNavItem
import com.lucasdelima.louveapp.ui.screens.favorites.FavoritesScreen
import com.lucasdelima.louveapp.ui.screens.home.HomeScreen
import com.lucasdelima.louveapp.ui.screens.discover.DiscoverScreen
import com.lucasdelima.louveapp.ui.screens.more.MoreScreen

@Composable
fun MainScreen(rootNavController: NavHostController) {
    val bottomNavController = rememberNavController()

    // O fundo do tema já está sendo desenhado na MainActivity
    // Aqui apenas renderizamos o conteúdo com a barra de navegação
    // Removido o Scaffold para evitar camadas adicionais que suavizam as cores
    Box(modifier = Modifier.fillMaxSize()) {
        // NavHost aninhado para o conteúdo principal
        // Este NavHost renderiza as telas de navegação inferior
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Harpa.route
        ) {
            // Tela principal da harpa
            composable(BottomNavItem.Harpa.route) {
                HomeScreen(
                    onHymnSelected = { hymnId ->
                        rootNavController.navigate("hymnDetail/$hymnId")
                    },
                    onSettingsClick = {
                        rootNavController.navigate("settings")
                    }
                )
            }
            // Tela de favoritos
            composable(BottomNavItem.Favorites.route) {
                FavoritesScreen(
                    onHymnClick = { hymnId ->
                        rootNavController.navigate("hymnDetail/$hymnId")
                    }
                )
            }
            // Tela de descoberta
            composable(BottomNavItem.Discover.route) {
                DiscoverScreen()
            }
            
            // Tela mais (centro de controle)
            composable(BottomNavItem.More.route) {
                MoreScreen(
                    onNavigateToProfile = { rootNavController.navigate("profile") },
                    onNavigateToSettings = { rootNavController.navigate("settings") },
                    onNavigateToAbout = { rootNavController.navigate("about") },
                    onNavigateToSupport = { rootNavController.navigate("support") }
                )
            }
        }
        
        // Barra de navegação inferior posicionada na parte inferior
        // Posicionada de forma absoluta para não interferir com o layout
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp) // Ajustado para a nova altura da barra (64dp)
        ) {
            LouveBottomNavBar(navController = bottomNavController)
        }
    }
}
