package com.lucasdelima.louveapp.ui.navigation


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lucasdelima.louveapp.ui.screens.hymn.HymnDetailScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.lucasdelima.louveapp.ui.screens.home.HomeScreen

object Routes {
    const val HOME = "home"
    const val HYMN_DETAIL = "hymnDetail/{id}"
}

@Composable
fun NavGraph(navController: NavHostController, innerPadding: PaddingValues) {
    NavHost(navController = navController, startDestination = Routes.HOME, modifier = Modifier.padding(innerPadding)) {
        composable(Routes.HOME) {
            HomeScreen(
                onHymnSelected = { id ->
                    navController.navigate("hymnDetail/$id")
                }
            )
        }

        // SUBSTITUA O SEU BLOCO composable() PARA A ROTA DE DETALHES POR ESTE:
        composable(
            route = Routes.HYMN_DETAIL, // Usando a constante: "hymnDetail/{id}"
            // Esta linha é a correção crucial.
            // Ela diz ao NavHost: "a parte {id} da rota é um argumento e o tipo dele é Inteiro".
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            // Agora o ViewModel receberá o ID corretamente.
            HymnDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}