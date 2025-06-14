package com.lucasdelima.louveapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.lucasdelima.louveapp.ui.navigation.NavGraph
import com.lucasdelima.louveapp.ui.theme.AllThemes
import com.lucasdelima.louveapp.ui.theme.DefaultTheme
import com.lucasdelima.louveapp.ui.theme.LouveAppTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels() // Pega a instância do ViewModel

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // "Escuta" o nome do tema salvo
            val themeName by viewModel.themeName.collectAsState()

            // Encontra o objeto LouveThemeData correspondente ao nome salvo
            val selectedTheme = AllThemes.find { it.name == themeName } ?: DefaultTheme

            LouveAppTheme(themeData = selectedTheme) { // Garantindo que estamos testando o tema certo
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    // ADICIONE ESTA LINHA PARA DEIXAR O FUNDO DO SCAFFOLD TRANSPARENTE
                    containerColor = Color.Transparent
                ) { innerPadding ->
                    // A TopAppBar, BottomAppBar e os Cards dentro do NavGraph
                    // manterão suas próprias cores, criando um efeito de flutuação sobre o gradiente.
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        innerPadding = innerPadding
                    ) // Passando o padding
                }
            }
        }
    }
}