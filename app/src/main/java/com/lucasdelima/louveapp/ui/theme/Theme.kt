package com.lucasdelima.louveapp.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

/**
 * CompositionLocal para prover os dados do tema customizado (`LouveThemeData`)
 * na hierarquia de Composables de forma implícita.
 */
private val LocalLouveTheme = staticCompositionLocalOf<LouveThemeData> {
    // Medida de segurança: lança um erro se o tema não for fornecido na raiz do app.
    error("No LouveThemeData provided")
}

/**
 * O Composable principal do tema do aplicativo. Ele aplica um [LouveThemeData] customizado
 * que define cores, tipografia e fundos.
 *
 * @param themeData O objeto de dados contendo todas as propriedades do tema a ser aplicado.
 * @param content O conteúdo da UI que receberá este tema.
 */
@Composable
fun LouveAppTheme(
    themeData: LouveThemeData,
    content: @Composable () -> Unit
) {
    // Fornece o themeData para toda a árvore de componentes aninhada.
    CompositionLocalProvider(LocalLouveTheme provides themeData) {
        // Aplica as cores e tipografia padrões do Material 3, lidas do nosso tema.
        MaterialTheme(
            colorScheme = themeData.colors,
            typography = themeData.typography
        ) {
            // Garante que sempre haverá um fundo de tela, usando a cor do tema.
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                // Aplica um fundo customizado (ex: gradiente) por cima do fundo base, se existir.
                themeData.screenBackground?.invoke()
                // Renderiza o conteúdo do app.
                content()
            }
        }
    }
}

/**
 * Objeto para acessar as propriedades do tema ativo de forma fácil e segura em qualquer Composable.
 * Exemplo de uso: `Text(color = LouveTheme.colors.primary)`
 */
object LouveTheme {
    val colors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalLouveTheme.current.colors

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalLouveTheme.current.typography
}