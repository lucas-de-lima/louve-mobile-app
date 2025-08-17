package com.lucasdelima.louveapp.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lucasdelima.louveapp.ui.navigation.BottomNavItem

@Composable
fun LouveBottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Harpa,
        BottomNavItem.Favorites,
        BottomNavItem.Discover,
        BottomNavItem.More
    )

    // A barra de navegação é transparente para permitir que o fundo do tema seja visível
    NavigationBar(
        containerColor = Color.Transparent
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Evita empilhar a mesma tela várias vezes
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) }
            )
        }
    }
}
