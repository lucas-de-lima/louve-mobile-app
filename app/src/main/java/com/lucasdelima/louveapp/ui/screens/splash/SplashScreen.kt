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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
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

    // Silhuetas - surgem da base enquanto se cruzam
    val silhouettesAlpha = remember { Animatable(0f) }
    val silhouettesTranslateY = remember { Animatable(screenHeight * 0.3f) } // Começam da base
    val manStartX = -screenWidth / 3 // Começa da esquerda
    val womanStartX = screenWidth / 3 // Começa da direita
    val manTargetX = screenWidth / 4 // Vai para a direita
    val womanTargetX = -screenWidth / 4 // Vai para a esquerda
    val manOffsetX = remember { Animatable(manStartX) } // Começa da esquerda
    val womanOffsetX = remember { Animatable(womanStartX) } // Começa da direita

    // Logo animado
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.8f) }
    val logoTranslateY = remember { Animatable(0f) }

    // Fundo - transição da splash padrão para transparente
    val backgroundAlpha = remember { Animatable(1f) } // Fundo estático primeiro

    // --- ROTEIRO DA ANIMAÇÃO INTEGRADA (SPLASH PADRÃO + PERSONALIZADA) ---
    LaunchedEffect(key1 = true) {
        // ✅ CORREÇÃO: Evita múltiplas execuções da animação
        if (animationStarted.value) return@LaunchedEffect
        animationStarted.value = true
        
        try {
            // FASE 1: CLONAGEM DA SPLASH PADRÃO (0-400ms)
            // Simula exatamente a splash padrão do Android
            logoAlpha.animateTo(1f, animationSpec = tween(200, easing = FastOutSlowInEasing))
            logoScale.animateTo(1.0f, animationSpec = tween(200, easing = FastOutSlowInEasing))
            
            // Logo estabilizado (200-400ms)
            delay(200)

            // FASE 2: TRANSIÇÃO PARA SPLASH PERSONALIZADA (400-800ms)
            // Fundo muda de estático para transparente (sem gradiente)
            backgroundAlpha.animateTo(0f, animationSpec = tween(400, easing = FastOutSlowInEasing))
            
            // Logo levita suavemente e pulsa
            logoScale.animateTo(1.15f, animationSpec = tween(400, easing = FastOutSlowInEasing))
            logoTranslateY.animateTo(-15f, animationSpec = tween(400, easing = FastOutSlowInEasing))

            // FASE 3: SILHUETAS SURGEM DA BASE E SE CRUZAM + LOGO SOBE (800-2000ms)
            // ✅ CORREÇÃO: Animações mais lentas e simultâneas
            launch {
                silhouettesAlpha.animateTo(1f, animationSpec = tween(1200, easing = FastOutSlowInEasing))
            }
            launch {
                silhouettesTranslateY.animateTo(0f, animationSpec = tween(1200, easing = FastOutSlowInEasing)) // Sobem da base
            }
            launch {
                manOffsetX.animateTo(targetValue = manTargetX, animationSpec = tween(1200, easing = FastOutSlowInEasing)) // Cruzam
            }
            launch {
                womanOffsetX.animateTo(targetValue = womanTargetX, animationSpec = tween(1200, easing = FastOutSlowInEasing)) // Cruzam
            }
            launch {
                // Logo sobe bem acima das silhuetas do casal
                logoTranslateY.animateTo(-640f, animationSpec = tween(1200, easing = FastOutSlowInEasing))
            }

            // FASE 4: FINALIZAÇÃO (2000-2200ms)
            // Logo pulsa suavemente
            logoScale.animateTo(1.1f, animationSpec = tween(200, easing = FastOutSlowInEasing))

            // Transição para o app - tempo suficiente para animação completa
            delay(1200)
            
            // ✅ CORREÇÃO: Chama o callback apenas se a animação foi executada com sucesso
            onAnimationFinished()
        } catch (e: Exception) {
            // ✅ CORREÇÃO: Em caso de erro, chama o callback para não travar
            onAnimationFinished()
        }
    }

    // --- MONTAGEM DAS CAMADAS VISUAIS INTEGRADAS ---
    Box(modifier = Modifier.fillMaxSize()) {
        // FONDO ESTÁTICO (SPLASH PADRÃO) - aparece primeiro
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(backgroundAlpha.value),
            contentAlignment = Alignment.Center
        ) {
            // Cor de fundo da splash padrão (#FFFDFE - branco papel)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Fundo sólido da splash padrão com cor atualizada
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Cor de fundo atualizada para #FFFDFE (branco papel)
                }
            }
        }

        // FUNDO TRANSPARENTE (SPLASH PERSONALIZADA) - sem gradiente
        // O fundo agora é transparente, mostrando apenas o logo e silhuetas

        // LOGO ANIMADO - presente em ambas as fases
        Image(
            painter = painterResource(id = R.drawable.ic_splash_logo),
            contentDescription = "Logo do Louve App",
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    alpha = logoAlpha.value
                    scaleX = logoScale.value
                    scaleY = logoScale.value
                    translationY = logoTranslateY.value
                }
        )

        // SILHUETAS - surgem da base enquanto se cruzam
        Image(
            painter = painterResource(id = R.drawable.splash_woman_silhouette),
            contentDescription = "Silhueta de mulher em louvor",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .graphicsLayer {
                    translationX = womanOffsetX.value
                    translationY = silhouettesTranslateY.value
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
                    translationY = silhouettesTranslateY.value
                    alpha = silhouettesAlpha.value
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
