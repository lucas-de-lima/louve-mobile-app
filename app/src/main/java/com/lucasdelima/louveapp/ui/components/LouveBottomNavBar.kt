package com.lucasdelima.louveapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.twotone.Menu
import androidx.compose.material.icons.twotone.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lucasdelima.louveapp.ui.navigation.BottomNavItem

@Composable
fun LouveBottomNavBar(navController: NavController) {
    // ✅ OTIMIZAÇÃO: Lista de itens em remember para evitar recriação
    val items = remember {
        listOf(
            BottomNavItem.Harpa,
            BottomNavItem.Favorites,
            BottomNavItem.Discover,
            BottomNavItem.More
        )
    }

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    // 1. Definimos a altura base que queremos para o nosso conteúdo (ícones e texto)
    val contentHeight = 68.dp

    // 2. Pegamos os espaçamentos (insets) da barra de navegação do sistema
    val navigationBarInsets = WindowInsets.navigationBars.asPaddingValues()

    // 3. Calculamos a altura total: nossa altura base + a altura da barra do sistema
    val totalHeight = contentHeight + navigationBarInsets.calculateBottomPadding()

    // Nova implementação usando BottomAppBar com altura dinâmica
    BottomAppBar(
        modifier = Modifier.height(totalHeight), // Altura calculada dinamicamente
        containerColor = Color.Transparent, // Transparência mantida
        contentPadding = PaddingValues(0.dp) // Zeramos o padding padrão para controle total
    ) {
        // Solução com Box para centralização vertical perfeita
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Ajuste visual fino: empurra conteúdo para cima em dispositivos com barra
                .padding(bottom = navigationBarInsets.calculateBottomPadding() / 2),
            contentAlignment = Alignment.Center // Centraliza o Row verticalmente
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(contentHeight), // Altura definida para o conteúdo
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically // Centraliza itens dentro do Row
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route

                    BottomNavItem(
                        label = item.title,
                        icon = when (item.route) {
                            BottomNavItem.Harpa.route -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
                            BottomNavItem.Favorites.route -> if (isSelected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
                            BottomNavItem.Discover.route -> if (isSelected) Icons.TwoTone.Search else Icons.Outlined.Search
                            BottomNavItem.More.route -> if (isSelected) Icons.TwoTone.Menu else Icons.Outlined.Menu
                            else -> Icons.Outlined.Home
                        },
                        isSelected = isSelected,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

// Componente customizado para itens de navegação
@Composable
fun RowScope.BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f) // Garante que todos os itens tenham a mesma largura
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}