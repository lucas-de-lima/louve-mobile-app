package com.lucasdelima.louveapp.ui.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lucasdelima.louveapp.ui.screens.home.HomeScreen
import com.lucasdelima.louveapp.ui.screens.hymn.HymnDetailScreen

object Routes {
    const val HOME = "home"
    const val HYMN_DETAIL = "hymnDetail/{id}"
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onHymnSelected = { id ->
                    navController.navigate("hymnDetail/$id")
                }
            )
        }
        composable("hymnDetail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: return@composable
            HymnDetailScreen(hymnId = id, onBack = { navController.popBackStack() })
        }
    }
}
