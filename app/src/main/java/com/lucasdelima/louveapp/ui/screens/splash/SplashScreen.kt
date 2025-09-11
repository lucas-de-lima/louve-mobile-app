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
import androidx.compose.runtime.mutableStateOf
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
    // ✅ CORREÇÃO: Flag para controlar se a animação já foi executada
    val animationStarted = remember { mutableStateOf(false) }
    
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
    val manTargetX = screenWidth / 4
    val womanTargetX = -screenWidth / 4
    val manOffsetX = remember { Animatable(-screenWidth / 2) }
    val womanOffsetX = remember { Animatable(screenWidth / 2) }

    // Notas Musicais
    val notesAlpha = remember { Animatable(0f) }
    val notesScale = remember { Animatable(0.2f) }
    val notesTranslateY = remember { Animatable(0f) }

    // Efeito de Luz
    val lightEffectAlpha = remember { Animatable(0f) }
    val lightEffectScale = remember { Animatable(0.5f) }

    // --- ROTEIRO DA ANIMAÇÃO (VERSÃO FINAL POLIDA) ---
    LaunchedEffect(key1 = true) {
        // ✅ CORREÇÃO: Evita múltiplas execuções da animação
        if (animationStarted.value) return@LaunchedEffect
        animationStarted.value = true
        
        try {
            // ATO I: A CHEGADA (720ms - reduzido 40%)
            // O movimento de chegada é mais rápido e dinâmico.
            launch {
                silhouettesAlpha.animateTo(1f, animationSpec = tween(720, easing = FastOutSlowInEasing))
            }
            launch {
                manOffsetX.animateTo(targetValue = manTargetX, animationSpec = tween(720, easing = FastOutSlowInEasing))
            }
            womanOffsetX.animateTo(targetValue = womanTargetX, animationSpec = tween(720, easing = FastOutSlowInEasing))

            // Pausa curta após a chegada.
            delay(120) // Reduzido de 200ms para 120ms

            // ATO II: O LOUVOR E A LUZ (Duração total ~900ms - reduzido 40%)
            // A ascensão do louvor e a explosão de luz são mais rápidas e dinâmicas.
            launch {
                notesAlpha.animateTo(1f, animationSpec = tween(180)) // Reduzido de 300ms
                // Desaparecem mais rápido.
                notesAlpha.animateTo(0f, animationSpec = tween(durationMillis = 600, delayMillis = 300)) // Reduzido de 1000ms/500ms
            }
            launch {
                notesScale.animateTo(1.0f, animationSpec = tween(900, easing = FastOutSlowInEasing)) // Reduzido de 1500ms
                notesTranslateY.animateTo(-screenHeight * 0.5f, animationSpec = tween(900, easing = LinearEasing)) // Reduzido de 1500ms
            }
            launch {
                delay(300) // Reduzido de 500ms para 300ms
                lightEffectAlpha.animateTo(0.8f, animationSpec = tween(180)) // Reduzido de 300ms
                lightEffectScale.animateTo(2.0f, animationSpec = tween(480, easing = FastOutSlowInEasing)) // Reduzido de 800ms
                lightEffectAlpha.animateTo(0f, animationSpec = tween(240)) // Reduzido de 400ms
            }

            // ATO III: A TRANSIÇÃO SUAVE
            // Duração total da cena visível é de aprox. 0.72s + 0.12s + 0.9s = 1.74s (reduzido 40%)
            // Damos um pequeno tempo para o olho descansar antes de mudar de tela.
            delay(1740) // Reduzido de 2900ms para 1740ms
            
            // ✅ CORREÇÃO: Chama o callback apenas se a animação foi executada com sucesso
            onAnimationFinished()
        } catch (e: Exception) {
            // ✅ CORREÇÃO: Em caso de erro, chama o callback para não travar
            onAnimationFinished()
        }
    }

    // --- MONTAGEM DAS CAMADAS VISUAIS ---
    // As silhuetas agora permanecem na tela, pois não há mais animação de alpha para 0.
    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = R.drawable.splash_background), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)

        Image(painter = painterResource(id = R.drawable.splash_light_effect), contentDescription = null, modifier = Modifier.align(Alignment.Center).offset(y = (-150).dp).scale(lightEffectScale.value).alpha(lightEffectAlpha.value))

        Image(painter = painterResource(id = R.drawable.splash_woman_silhouette), contentDescription = "Silhueta de mulher em louvor", modifier = Modifier.align(Alignment.BottomCenter).graphicsLayer { translationX = womanOffsetX.value; alpha = silhouettesAlpha.value })
        Image(painter = painterResource(id = R.drawable.splash_man_silhouette), contentDescription = "Silhueta de homem em louvor", modifier = Modifier.align(Alignment.BottomCenter).graphicsLayer { translationX = manOffsetX.value; alpha = silhouettesAlpha.value })

        Image(painter = painterResource(id = R.drawable.splash_music_notes), contentDescription = null, modifier = Modifier.align(Alignment.BottomCenter).offset(y = (-500).dp).graphicsLayer { alpha = notesAlpha.value; scaleX = notesScale.value; scaleY = notesScale.value; translationY = notesTranslateY.value; transformOrigin = TransformOrigin(0.5f, 1.0f) })
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    LouveAppTheme(themeData = SweetCandyTheme) {
        SplashScreen(onAnimationFinished = {})
    }
}
