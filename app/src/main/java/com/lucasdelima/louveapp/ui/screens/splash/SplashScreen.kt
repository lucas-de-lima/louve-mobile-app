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
import androidx.compose.ui.graphics.Color
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

    // Silhuetas - agora aparecem de baixo para cima
    val silhouettesAlpha = remember { Animatable(0f) }
    val silhouettesTranslateY = remember { Animatable(screenHeight * 0.3f) } // Começam de baixo
    val manTargetX = screenWidth / 4
    val womanTargetX = -screenWidth / 4
    val manOffsetX = remember { Animatable(manTargetX) } // Já posicionados
    val womanOffsetX = remember { Animatable(womanTargetX) } // Já posicionados

    // Logo animado
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.8f) }
    val logoTranslateY = remember { Animatable(0f) }

    // Fundo - transição da splash padrão para personalizada
    val backgroundAlpha = remember { Animatable(1f) } // Fundo estático primeiro
    val gradientAlpha = remember { Animatable(0f) } // Gradiente depois

    // --- ROTEIRO DA ANIMAÇÃO INTEGRADA (SPLASH PADRÃO + PERSONALIZADA) ---
    LaunchedEffect(key1 = true) {
        // ✅ CORREÇÃO: Evita múltiplas execuções da animação
        if (animationStarted.value) return@LaunchedEffect
        animationStarted.value = true
        
        try {
            // FASE 1: CLONAGEM DA SPLASH PADRÃO (0-800ms)
            // Simula exatamente a splash padrão do Android
            launch {
                logoAlpha.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
            }
            launch {
                logoScale.animateTo(1.0f, animationSpec = tween(300, easing = FastOutSlowInEasing))
            }
            
            // Logo estabilizado (300-800ms)
            delay(500)

            // FASE 2: TRANSIÇÃO PARA SPLASH PERSONALIZADA (800-1500ms)
            // Fundo muda de estático para gradiente
            launch {
                backgroundAlpha.animateTo(0f, animationSpec = tween(700, easing = FastOutSlowInEasing))
                gradientAlpha.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
            }
            
            // Logo levita suavemente
            launch {
                logoScale.animateTo(1.1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
                logoTranslateY.animateTo(-20f, animationSpec = tween(700, easing = FastOutSlowInEasing))
            }

            // FASE 3: SILHUETAS APARECEM DE BAIXO (1500-2500ms)
            // Silhuetas sobem de baixo para cima
            launch {
                silhouettesAlpha.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
                silhouettesTranslateY.animateTo(0f, animationSpec = tween(500, easing = FastOutSlowInEasing))
            }

            // FASE 4: FINALIZAÇÃO (2500-3000ms)
            // Logo se estabiliza no ar
            launch {
                logoScale.animateTo(1.05f, animationSpec = tween(500, easing = FastOutSlowInEasing))
            }

            // Transição para o app
            delay(500)
            
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
            // Cor de fundo da splash padrão (#5D1F28)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Fundo sólido da splash padrão
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Aqui seria a cor de fundo estática
                    // Por enquanto, usamos uma cor sólida
                }
            }
        }

        // FONDO GRADIENTE (SPLASH PERSONALIZADA) - aparece depois
        Image(
            painter = painterResource(id = R.drawable.splash_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(gradientAlpha.value),
            contentScale = ContentScale.Crop
        )

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

        // SILHUETAS - aparecem de baixo para cima
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
