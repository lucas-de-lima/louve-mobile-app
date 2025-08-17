package com.lucasdelima.louveapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.lucasdelima.louveapp.ui.navigation.NavGraph
import com.lucasdelima.louveapp.ui.theme.AllThemes
import com.lucasdelima.louveapp.ui.theme.DefaultTheme
import com.lucasdelima.louveapp.ui.theme.LouveAppTheme
import com.lucasdelima.louveapp.ui.theme.LouveTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // Habilita o modo edge-to-edge para que o conteúdo possa se estender
        // até as bordas da tela, incluindo as áreas das barras de sistema
        enableEdgeToEdge()
        
        setContent {
            val themeName by viewModel.themeName.collectAsState()
            val selectedTheme = AllThemes.find { it.name == themeName } ?: DefaultTheme

            LouveAppTheme(themeData = selectedTheme) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // O fundo do tema agora é renderizado diretamente em cada tela
                    // para evitar suavização causada por camadas intermediárias
                    // O enableEdgeToEdge() permite que o fundo se estenda até as bordas
                    
                    // O conteúdo de navegação é renderizado por cima do fundo
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}
