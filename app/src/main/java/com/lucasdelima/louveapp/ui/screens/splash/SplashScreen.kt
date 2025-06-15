package com.lucasdelima.louveapp.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucasdelima.louveapp.R
import com.lucasdelima.louveapp.ui.theme.LouveAppTheme
import com.lucasdelima.louveapp.ui.theme.SweetCandyTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onAnimationFinished: () -> Unit
) {
    // --- PREPARAÇÃO DO PALCO E DOS ATORES ---
    // Pegamos as dimensões da tela para coreografar os movimentos com precisão.
    val density = LocalDensity.current
    val screenWidth = with(density) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }
    val screenHeight = with(density) {
        LocalConfiguration.current.screenHeightDp.dp.toPx()
    }

    // Cada elemento da nossa cena é um "ator" com propriedades animáveis.
    // Silhuetas: controlamos sua transparência e posição inicial fora da tela.
    val silhouettesAlpha = remember { Animatable(0f) }
    val manTargetX = screenWidth / 4
    val womanTargetX = -screenWidth / 4
    val manOffsetX = remember { Animatable(-screenWidth / 2) }
    val womanOffsetX = remember { Animatable(screenWidth / 2) }

    // Notas Musicais: controlamos sua aparição, escala e movimento de subida.
    val notesAlpha = remember { Animatable(0f) }
    val notesScale = remember { Animatable(0.2f) }
    val notesTranslateY = remember { Animatable(0f) }

    // Efeito de Luz: controlamos sua aparição e "explosão".
    val lightEffectAlpha = remember { Animatable(0f) }
    val lightEffectScale = remember { Animatable(0.5f) }

    // --- O DIRETOR GRITA "AÇÃO!" ---
    // LaunchedEffect é o nosso diretor. Ele orquestra toda a sequência de animações
    // do início ao fim, garantindo que cada "Ato" aconteça no tempo certo.
    LaunchedEffect(key1 = true) {
        // --- ATO I: A CHEGADA DOS ADORADORES (Duração: 1500ms) ---
        // A cena começa. As silhuetas deslizam suavemente das laterais para suas
        // posições de adoração, enquanto se tornam visíveis.
        launch {
            silhouettesAlpha.animateTo(1f, animationSpec = tween(1500, easing = FastOutSlowInEasing))
        }
        launch {
            manOffsetX.animateTo(targetValue = manTargetX, animationSpec = tween(1500, easing = FastOutSlowInEasing))
        }
        launch {
            womanOffsetX.animateTo(targetValue = womanTargetX, animationSpec = tween(1500, easing = FastOutSlowInEasing))
        }

        // Uma pausa dramática para estabelecer a cena e a presença dos adoradores.
        delay(1300)

        // --- ATO II: O DESPERTAR DO LOUVOR (Duração: 2500ms) ---
        // O louvor, representado pelas notas, emana do espaço entre eles.
        // Ele aparece, sobe e cresce, como um sopro que se expande.
        launch {
            notesAlpha.animateTo(1f, animationSpec = tween(500))
            // Para um efeito suave, as notas começam a desaparecer antes de saírem da tela.
            notesAlpha.animateTo(0f, animationSpec = tween(durationMillis = 1500, delayMillis = 1000))
        }
        launch {
            notesScale.animateTo(1.0f, animationSpec = tween(2500, easing = FastOutSlowInEasing))
            notesTranslateY.animateTo(-screenHeight * 0.5f, animationSpec = tween(2500, easing = LinearEasing))
        }

        // --- ATO III: O CLÍMAX DA LUZ (sincronizado com o louvor) ---
        // No meio da ascensão do louvor, a luz explode, representando o clímax da adoração.
        launch {
            delay(1000)
            lightEffectAlpha.animateTo(0.8f, animationSpec = tween(400))
            lightEffectScale.animateTo(2.0f, animationSpec = tween(1200, easing = FastOutSlowInEasing))
            lightEffectAlpha.animateTo(0f, animationSpec = tween(800)) // A luz se dissipa.
        }

        // --- ATO IV: A TRANSIÇÃO ---
        // Seguramos a cena final por um instante para o espectador absorver, e então prosseguimos.
        delay(2000)
        onAnimationFinished()
    }

    // --- A MONTAGEM DO CENÁRIO (Renderização das Camadas) ---
    // No Box, empilhamos nossas camadas de arte, da mais funda para a mais próxima.
    Box(modifier = Modifier.fillMaxSize()) {
        // Camada 1: O Fundo
        Image(
            painter = painterResource(id = R.drawable.splash_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Camada 2: A Luz de Fundo (nosso efeito de Bokeh)
        Image(
            painter = painterResource(id = R.drawable.splash_light_effect),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-150).dp)
                .scale(lightEffectScale.value)
                .alpha(lightEffectAlpha.value)
        )

        // Camada 3: Os Adoradores
        Image(
            painter = painterResource(id = R.drawable.splash_woman_silhouette),
            contentDescription = "Silhueta de mulher em louvor",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .graphicsLayer {
                    translationX = womanOffsetX.value
                    alpha = silhouettesAlpha.value
                }
        )
        Image(
            painter = painterResource(id = R.drawable.splash_man_silhouette),
            contentDescription = "Silhueta de homem em louvor",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .graphicsLayer {
                    translationX = manOffsetX.value
                    alpha = silhouettesAlpha.value
                }
        )

        // Camada 4: As Notas Musicais (O Louvor)
        Image(
            painter = painterResource(id = R.drawable.splash_music_notes),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-500).dp) // Ponto de origem entre eles, na altura correta.
                .graphicsLayer {
                    alpha = notesAlpha.value
                    scaleX = notesScale.value
                    scaleY = notesScale.value
                    translationY = notesTranslateY.value
                    transformOrigin = TransformOrigin(0.5f, 1.0f) // A escala acontece a partir da base.
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    LouveAppTheme(themeData = SweetCandyTheme) {
        SplashScreen(onAnimationFinished = {})
    }
}
