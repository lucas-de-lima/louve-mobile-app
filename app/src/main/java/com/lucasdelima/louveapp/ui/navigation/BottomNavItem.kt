package com.lucasdelima.louveapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Usamos uma sealed class para definir cada item da nossa barra de navegação.
 * Isso nos dá segurança de tipo e facilita a criação da barra.
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Harpa : BottomNavItem("home_screen", "Harpa", Icons.Outlined.Home)
    data object Favorites :
        BottomNavItem("favorites_screen", "Favoritos", Icons.Outlined.FavoriteBorder)

    data object Discover : BottomNavItem("discover_screen", "Descubra", Icons.Outlined.Search)
    data object More : BottomNavItem("more_screen", "Mais", Icons.Outlined.Menu)
}
