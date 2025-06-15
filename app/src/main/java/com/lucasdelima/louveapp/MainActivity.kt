package com.lucasdelima.louveapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.lucasdelima.louveapp.ui.navigation.NavGraph
import com.lucasdelima.louveapp.ui.theme.AllThemes
import com.lucasdelima.louveapp.ui.theme.DefaultTheme
import com.lucasdelima.louveapp.ui.theme.LouveAppTheme
import com.lucasdelima.louveapp.ui.theme.LouveTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeName by viewModel.themeName.collectAsState()
            val selectedTheme = AllThemes.find { it.name == themeName } ?: DefaultTheme

            LouveAppTheme(themeData = selectedTheme) {
                // O Box externo desenha o fundo principal uma única vez.
                Box(modifier = Modifier.fillMaxSize()) {
                    LouveTheme.backgrounds.screenBackground()

                    // O NavGraph é desenhado por cima e não precisa mais do padding.
                    val navController = rememberNavController()
                    NavGraph(navController = navController) // <-- CHAMADA CORRIGIDA
                }
            }
        }
    }
}
