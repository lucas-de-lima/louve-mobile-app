package com.lucasdelima.louveapp.ui.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lucasdelima.louveapp.ui.screens.hymn.HymnDetailScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

object Routes {
    const val HOME = "home"
    const val HYMN_DETAIL = "hymnDetail/{id}"
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(
            route = "hymnDetail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType }) // Boa prática: define o tipo do argumento
        ) {
            // A tela não precisa mais do ID diretamente, então não o passamos.
            // O ViewModel que adicionaremos depois irá acessá-lo.
            HymnDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}