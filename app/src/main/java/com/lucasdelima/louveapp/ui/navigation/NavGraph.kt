package com.lucasdelima.louveapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lucasdelima.louveapp.ui.screens.hymn.HymnDetailScreen
import com.lucasdelima.louveapp.ui.screens.main.MainScreen
import com.lucasdelima.louveapp.ui.screens.settings.SettingsScreen
import com.lucasdelima.louveapp.ui.screens.splash.SplashScreen

object Routes {
    const val SPLASH = "splash"
    const val MAIN = "main"
    const val HYMN_DETAIL = "hymnDetail/{id}"
    const val SETTINGS = "settings"
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onAnimationFinished = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // A rota "main" agora carrega a MainScreen, que tem sua própria navegação interna.
        composable(Routes.MAIN) {
            MainScreen(rootNavController = navController)
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
