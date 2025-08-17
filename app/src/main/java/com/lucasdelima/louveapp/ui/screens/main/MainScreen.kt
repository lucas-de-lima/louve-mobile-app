package com.lucasdelima.louveapp.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lucasdelima.louveapp.ui.components.LouveBottomNavBar
import com.lucasdelima.louveapp.ui.navigation.BottomNavItem
import com.lucasdelima.louveapp.ui.screens.favorites.FavoritesScreen
import com.lucasdelima.louveapp.ui.screens.home.HomeScreen

@Composable
fun MainScreen(rootNavController: NavHostController) {
    val bottomNavController = rememberNavController()

    // O fundo do tema já está sendo desenhado na MainActivity
    // Aqui apenas renderizamos o conteúdo com a barra de navegação
    // O Scaffold é transparente para permitir que o fundo do tema seja visível
    // O enableEdgeToEdge() permite que o fundo se estenda até as bordas
    Scaffold(
        bottomBar = { LouveBottomNavBar(navController = bottomNavController) },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { innerPadding ->
        // O fundo do tema já está sendo desenhado na MainActivity
        // Aqui apenas aplicamos o padding necessário para o conteúdo
        // O fundo cobre toda a tela, incluindo as áreas das barras de sistema
        Box(modifier = Modifier.padding(innerPadding)) {
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
                // Adicionar outras rotas conforme necessário
                // composable(BottomNavItem.Discover.route) { DiscoverScreen() }
                // composable(BottomNavItem.More.route) { MoreScreen() }
            }
        }
    }
}
