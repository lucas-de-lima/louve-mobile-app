package com.lucasdelima.louveapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.lucasdelima.louveapp.ui.navigation.NavGraph
import com.lucasdelima.louveapp.ui.theme.AllThemes
import com.lucasdelima.louveapp.ui.theme.DefaultTheme
import com.lucasdelima.louveapp.ui.theme.LouveAppTheme
import com.lucasdelima.louveapp.ui.theme.LouveTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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
            LouveApp(viewModel)
        }
    }
    

}

@Composable
fun LouveApp(viewModel: MainViewModel) {
    // ✅ SOLUÇÃO SIMPLES: Observa diretamente o tema atual
    val currentTheme by viewModel.currentTheme.collectAsState()
    
    // ✅ OTIMIZAÇÃO: Cache do tema selecionado para evitar recálculo
    val selectedTheme = remember(currentTheme) {
        AllThemes.find { it.name == currentTheme } 
            ?: run {
                Log.w("MainActivity", "Tema '$currentTheme' não encontrado, usando padrão")
                AllThemes.find { it.isDefault } ?: DefaultTheme
            }
    }
    
    // ✅ Aplica o tema e renderiza o app
    LouveAppTheme(themeData = selectedTheme) {
        Box(modifier = Modifier.fillMaxSize()) {
            // ✅ Fundo principal desenhado UMA VEZ na MainActivity
            LouveTheme.backgrounds.screenBackground()
            
            // ✅ NavGraph gerencia toda a navegação, incluindo o splash
            val navController = rememberNavController()
            NavGraph(navController = navController)
        }
    }
}
