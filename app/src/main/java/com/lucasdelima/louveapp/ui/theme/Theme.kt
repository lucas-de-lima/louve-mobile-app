package com.lucasdelima.louveapp.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.material3.Typography

// 1. Criamos um "CompositionLocal" para "transportar" nosso tema pela árvore de componentes.
//    Ele garante que toda parte do app tenha acesso ao tema ativo.
private val LocalLouveTheme = staticCompositionLocalOf<LouveThemeData> {
    error("No LouveThemeData provided") // Medida de segurança: crasha se o tema não for fornecido.
}

@Composable
fun LouveAppTheme(
    themeData: LouveThemeData, // 2. Agora o tema recebe nosso objeto de dados, não mais um booleano "darkTheme".
    content: @Composable () -> Unit
) {
    // 3. O CompositionLocalProvider "injeta" o themeData na hierarquia da UI.
    CompositionLocalProvider(LocalLouveTheme provides themeData) {
        // 4. O MaterialTheme agora é configurado com os dados do tema recebido.
        MaterialTheme(
            colorScheme = themeData.colors,
            typography = themeData.typography
        ) {
            // 5. Aplicamos nosso fundo de tela customizado, se ele existir.
            Box(modifier = Modifier.fillMaxSize()) {
                themeData.screenBackground?.invoke()
                content()
            }
        }
    }
}

// 6. Criamos um objeto "LouveTheme" para acessar nosso tema de forma fácil e limpa
//    em qualquer lugar do app, usando por exemplo: LouveTheme.colors.primary
object LouveTheme {
    val colors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalLouveTheme.current.colors

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalLouveTheme.current.typography

    // Poderíamos adicionar outros acessos aqui no futuro, como fontes ou fundos.
}