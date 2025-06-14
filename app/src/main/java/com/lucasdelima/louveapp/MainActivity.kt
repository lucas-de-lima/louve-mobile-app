package com.lucasdelima.louveapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.lucasdelima.louveapp.ui.navigation.NavGraph
import com.lucasdelima.louveapp.ui.theme.LouveAppTheme
import com.lucasdelima.louveapp.ui.theme.DefaultTheme
import com.lucasdelima.louveapp.ui.theme.DarkTheme
import com.lucasdelima.louveapp.ui.theme.SweetCandyTheme


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LouveAppTheme(themeData = SweetCandyTheme) { // Garantindo que estamos testando o tema certo
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    // ADICIONE ESTA LINHA PARA DEIXAR O FUNDO DO SCAFFOLD TRANSPARENTE
                    containerColor = Color.Transparent
                ) { innerPadding ->
                    // A TopAppBar, BottomAppBar e os Cards dentro do NavGraph
                    // manterão suas próprias cores, criando um efeito de flutuação sobre o gradiente.
                    val navController = rememberNavController()
                    NavGraph(navController = navController, innerPadding = innerPadding) // Passando o padding
                }
            }
        }
    }
}