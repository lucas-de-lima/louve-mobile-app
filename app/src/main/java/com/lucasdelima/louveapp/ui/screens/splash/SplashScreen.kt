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
    // --- ESTADOS DE ANIMAÇÃO ---
    val density = LocalDensity.current
    val screenWidth = with(density) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }
    val screenHeight = with(density) {
        LocalConfiguration.current.screenHeightDp.dp.toPx()
    }

    // Silhuetas
    val silhouettesAlpha = remember { Animatable(0f) }
    val manOffsetX = remember { Animatable(-screenWidth / 2) } // Começa fora, à esquerda
    val womanOffsetX = remember { Animatable(screenWidth / 2) }  // Começa fora, à direita

    // Notas Musicais (um único louvor)
    val notesAlpha = remember { Animatable(0f) }
    val notesScale = remember { Animatable(0.2f) }
    val notesTranslateY = remember { Animatable(0f) }

    // Efeito de Luz
    val lightEffectAlpha = remember { Animatable(0f) }
    val lightEffectScale = remember { Animatable(0.5f) }

    // --- ROTEIRO DA ANIMAÇÃO (COREOGRAFIA FINAL) ---
    LaunchedEffect(key1 = true) {
        // ATO I: A CHEGADA DOS ADORADORES (1500ms)
        // Eles deslizam suavemente das laterais para suas posições, enquanto aparecem.
        launch {
            silhouettesAlpha.animateTo(1f, animationSpec = tween(1500, easing = FastOutSlowInEasing))
        }
        launch {
            manOffsetX.animateTo(0f, animationSpec = tween(1500, easing = FastOutSlowInEasing))
        }
        womanOffsetX.animateTo(0f, animationSpec = tween(1500, easing = FastOutSlowInEasing))

        delay(1300) // Pausa dramática para estabelecer a presença deles.

        // ATO II: O LOUVOR ASCENDE (2500ms)
        // O louvor começa a emanar, crescendo e subindo.
        launch {
            notesAlpha.animateTo(1f, animationSpec = tween(500))
            // As notas começam a desaparecer quando atingem 60% da sua trajetória para um efeito suave.
            notesAlpha.animateTo(0f, animationSpec = tween(durationMillis = 1500, delayMillis = 1000))
        }
        launch {
            notesScale.animateTo(1.0f, animationSpec = tween(2500, easing = FastOutSlowInEasing))
            notesTranslateY.animateTo(-screenHeight * 0.5f, animationSpec = tween(2500, easing = LinearEasing))
        }

        // ATO III: O CLÍMAX DA LUZ (sincronizado com o louvor)
        launch {
            delay(1000) // A luz "explode" no meio da ascensão do louvor.
            lightEffectAlpha.animateTo(0.8f, animationSpec = tween(400))
            lightEffectScale.animateTo(2.0f, animationSpec = tween(1200, easing = FastOutSlowInEasing))
            lightEffectAlpha.animateTo(0f, animationSpec = tween(800)) // A luz desaparece suavemente.
        }

        // ATO IV: TRANSIÇÃO
        delay(2000) // Segura a cena final por um instante antes de prosseguir.
        onAnimationFinished()
    }

    // --- MONTAGEM DAS CAMADAS VISUAIS ---
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.splash_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painterResource(id = R.drawable.splash_light_effect),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-150).dp)
                .scale(lightEffectScale.value)
                .alpha(lightEffectAlpha.value)
        )

        Image(
            painter = painterResource(id = R.drawable.splash_woman_silhouette),
            contentDescription = "Silhueta de mulher em louvor",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 10.dp)
                .graphicsLayer {
                    translationX = womanOffsetX.value
                    alpha = silhouettesAlpha.value
                }
        )
        Image(
            painter = painterResource(id = R.drawable.splash_man_silhouette),
            contentDescription = "Silhueta de homem em louvor",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-10).dp)
                .graphicsLayer {
                    translationX = manOffsetX.value
                    alpha = silhouettesAlpha.value
                }
        )

        // A camada de notas agora é uma só, representando um louvor unificado.
        Image(
            painter = painterResource(id = R.drawable.splash_music_notes),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-350).dp) // Posição ajustada para a altura do peito/boca.
                .graphicsLayer {
                    alpha = notesAlpha.value
                    scaleX = notesScale.value
                    scaleY = notesScale.value
                    translationY = notesTranslateY.value
                    transformOrigin = TransformOrigin(0.5f, 1.0f) // A escala acontece a partir da base
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
