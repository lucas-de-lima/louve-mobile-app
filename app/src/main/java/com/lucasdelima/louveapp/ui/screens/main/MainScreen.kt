package com.lucasdelima.louveapp.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    Scaffold(
        bottomBar = { LouveBottomNavBar(navController = bottomNavController) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // NavHost aninhado para o conteúdo principal
            NavHost(
                navController = bottomNavController,
                startDestination = BottomNavItem.Harpa.route
            ) {
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
                composable(BottomNavItem.Favorites.route) {
                    FavoritesScreen(
                        onHymnClick = { hymnId ->
                            rootNavController.navigate("hymnDetail/$hymnId")
                        }
                    )
                }
            }
        }
    }
}
