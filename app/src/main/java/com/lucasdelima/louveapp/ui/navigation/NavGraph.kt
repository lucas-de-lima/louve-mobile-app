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
import com.lucasdelima.louveapp.ui.screens.profile.ProfileScreen
import com.lucasdelima.louveapp.ui.screens.about.AboutScreen
import com.lucasdelima.louveapp.ui.screens.support.SupportScreen

object Routes {
    const val SPLASH = "splash"
    const val MAIN = "main"
    const val HYMN_DETAIL = "hymnDetail/{id}"
    const val SETTINGS = "settings"
    const val PROFILE = "profile"
    const val ABOUT = "about"
    const val SUPPORT = "support"
}

@Composable
fun NavGraph(navController: NavHostController) {
    // O fundo do tema já está sendo desenhado na MainActivity
    // Aqui apenas renderizamos o conteúdo de navegação
    // O enableEdgeToEdge() permite que o fundo se estenda até as bordas
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
    ) {
        // Tela de splash que tem seu próprio fundo
        composable(Routes.SPLASH) {
            SplashScreen(
                onAnimationFinished = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Tela principal com navegação inferior
        // Esta tela usa o fundo do tema desenhado na MainActivity
        // O fundo cobre toda a tela, incluindo as áreas das barras de sistema
        composable(Routes.MAIN) {
            MainScreen(rootNavController = navController)
        }

        // Tela de detalhes do hino que tem seu próprio fundo especial
        // Este fundo é desenhado por cima do fundo principal
        composable(
            route = Routes.HYMN_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            HymnDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Tela de configurações que usa o fundo do tema
        // O fundo cobre toda a tela, incluindo as áreas das barras de sistema
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Tela de perfil do usuário
        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Tela sobre o app
        composable(Routes.ABOUT) {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Tela de suporte e ajuda
        composable(Routes.SUPPORT) {
            SupportScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
