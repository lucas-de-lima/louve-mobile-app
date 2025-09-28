package com.lucasdelima.louveapp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import com.lucasdelima.louveapp.domain.model.Hymn

/**
 * Componente otimizado para formatação e renderização do texto dos hinos.
 * 
 * Este componente segue os princípios de Clean Architecture do projeto,
 * separando a lógica de formatação de texto da apresentação visual.
 * 
 * Melhorias de performance implementadas:
 * - Memoização do texto formatado baseado no conteúdo do hino
 * - Memoização dos estilos SpanStyle para evitar recriação
 * - Memoização dos cálculos de tipografia
 * 
 * @param hymn O hino a ser formatado
 * @param fontScaleFactor Fator de escala da fonte (0.5f a 2.0f)
 * @param modifier Modificador de layout
 */
@Composable
fun HymnTextFormatter(
    hymn: Hymn,
    fontScaleFactor: Float,
    modifier: Modifier = Modifier
) {
    // ✅ Memoizar estilos para evitar recriação
    val boldStyle = remember { SpanStyle(fontWeight = FontWeight.Bold) }
    val semiBoldStyle = remember { SpanStyle(fontWeight = FontWeight.SemiBold) }
    
    // ✅ Memoizar o texto formatado baseado no hino
    val formattedLyrics = remember(hymn.id, hymn.verses, hymn.chorus) {
        buildAnnotatedString {
            hymn.verses.forEachIndexed { index, verse ->
                if (index > 0) append("\n")
                withStyle(style = boldStyle) {
                    append("${index + 1}\n")
                }
                append("$verse\n")
            }
            hymn.chorus?.let {
                append("\n")
                withStyle(style = boldStyle) {
                    append("Coro\n")
                }
                withStyle(style = semiBoldStyle) {
                    append(it)
                }
            }
        }
    }
    
    // ✅ Memoizar cálculos de tipografia
    val typography = MaterialTheme.typography
    val bodyFontSize = remember(fontScaleFactor, typography) {
        typography.bodyLarge.fontSize * fontScaleFactor
    }
    
    val bodyLineHeight = remember(fontScaleFactor, typography) {
        typography.bodyLarge.fontSize * 1.5 * fontScaleFactor
    }
    
    androidx.compose.material3.Text(
        text = formattedLyrics,
        style = MaterialTheme.typography.bodyLarge,
        fontSize = bodyFontSize,
        lineHeight = bodyLineHeight,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

/**
 * Componente otimizado para renderização do título do hino.
 * 
 * @param title Título do hino
 * @param fontScaleFactor Fator de escala da fonte
 * @param modifier Modificador de layout
 */
@Composable
fun HymnTitleFormatter(
    title: String,
    fontScaleFactor: Float,
    modifier: Modifier = Modifier
) {
    // ✅ Memoizar cálculo de tipografia do título
    val typography = MaterialTheme.typography
    val titleFontSize = remember(fontScaleFactor, typography) {
        typography.headlineMedium.fontSize * fontScaleFactor
    }
    
    androidx.compose.material3.Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        fontSize = titleFontSize,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}
