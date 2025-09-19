package com.lucasdelima.louveapp.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.twotone.Search
import androidx.compose.material.icons.twotone.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavDestination.Companion.hierarchy
import com.lucasdelima.louveapp.ui.navigation.BottomNavItem

@Composable
fun LouveBottomNavBar(navController: NavController) {
	// ✅ OTIMIZAÇÃO: Lista de itens em remember para evitar recriação
	val items = remember { listOf(
		BottomNavItem.Harpa,
		BottomNavItem.Favorites,
		BottomNavItem.Discover,
		BottomNavItem.More
	) }

	// A barra de navegação é transparente para permitir que o fundo do tema seja visível
	// Altura reduzida para uma aparência mais compacta e profissional
	NavigationBar(
		containerColor = Color.Transparent,
		modifier = Modifier, // ✅ Remove altura fixa
		windowInsets = WindowInsets.navigationBars // ✅ Consciente do sistema
	) {
		val navBackStackEntry = navController.currentBackStackEntryAsState()
		val currentRoute = navBackStackEntry.value?.destination?.route

		items.forEach { item ->
			val isSelected = currentRoute == item.route

			NavigationBarItem(
				selected = false, // Sempre false para permitir efeito ripple temporário
				onClick = {
					// ✅ Navegação otimizada para melhor performance
					if (currentRoute != item.route) {
						navController.navigate(item.route) {
							// Evita empilhar a mesma tela várias vezes
							popUpTo(navController.graph.startDestinationId) {
								saveState = true
							}
							launchSingleTop = true
							restoreState = true
						}
					}
				},
				icon = {
					// Usa ícones diferentes para indicar o estado ativo/inativo
					// Ícone preenchido para rota ativa, ícone vazio para rota inativa
					val icon = when (item.route) {
						BottomNavItem.Harpa.route -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
						BottomNavItem.Favorites.route -> if (isSelected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
						BottomNavItem.Discover.route -> if (isSelected) Icons.TwoTone.Search else Icons.Outlined.Search
						BottomNavItem.More.route -> if (isSelected) Icons.TwoTone.Menu else Icons.Outlined.Menu
						else -> Icons.Outlined.Home
					}

					Icon(
						imageVector = icon,
						contentDescription = item.title,
						modifier = Modifier.padding(vertical = 2.dp) // Padding reduzido para ícones
					)
				},
				label = {
					Text(
						text = item.title,
						modifier = Modifier.padding(top = 1.dp), // Padding reduzido para texto
						color = MaterialTheme.colorScheme.onSurface
					)
				},
				// Cores baseadas no tema ativo para feedback visual consistente
				colors = NavigationBarItemDefaults.colors(
					selectedIconColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
					unselectedIconColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
					selectedTextColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
					unselectedTextColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
					// Efeito ripple temporário natural com selected = false
				)
			)
		}
	}
}