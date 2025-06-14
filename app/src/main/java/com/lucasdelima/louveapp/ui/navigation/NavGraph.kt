package com.lucasdelima.louveapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lucasdelima.louveapp.ui.screens.home.HomeScreen
import com.lucasdelima.louveapp.ui.screens.hymn.HymnDetailScreen
import com.lucasdelima.louveapp.ui.screens.settings.SettingsScreen

object Routes {
    const val HOME = "home"
    const val HYMN_DETAIL = "hymnDetail/{id}"
    const val SETTINGS = "settings"
}

@Composable
fun NavGraph(navController: NavHostController) { // <-- REMOVIDO o parâmetro 'innerPadding'
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        // O modifier que aplicava o padding foi removido daqui.
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onHymnSelected = { id ->
                    navController.navigate("hymnDetail/$id")
                },
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }

        composable(
            route = Routes.HYMN_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            HymnDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
