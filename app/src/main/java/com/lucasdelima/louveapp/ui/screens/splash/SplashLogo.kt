package com.lucasdelima.louveapp.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lucasdelima.louveapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashLogo(
    modifier: Modifier = Modifier,
    onAnimationFinished: () -> Unit = {}
) {
    // Estados de animação do logo
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.8f) }
    val logoTranslateY = remember { Animatable(0f) }
    val logoRotation = remember { Animatable(0f) }

    // Animação do logo
    LaunchedEffect(Unit) {
        // Fase 1: Logo aparece (primeiros 300ms - cópia da splash padrão)
        logoAlpha.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
        
        // Fase 2: Logo estabiliza (300-800ms)
        delay(500)
        
        // Fase 3: Logo levita suavemente (800-1500ms)
        logoScale.animateTo(1.1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
        logoTranslateY.animateTo(-20f, animationSpec = tween(700, easing = FastOutSlowInEasing))
        logoRotation.animateTo(2f, animationSpec = tween(700, easing = FastOutSlowInEasing))
        
        // Fase 4: Logo se estabiliza no ar (1500-2000ms)
        delay(500)
        
        onAnimationFinished()
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Logo animado
        Image(
            painter = painterResource(id = R.drawable.ic_splash_logo),
            contentDescription = "Logo do Louve App",
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer {
                    alpha = logoAlpha.value
                    scaleX = logoScale.value
                    scaleY = logoScale.value
                    translationY = logoTranslateY.value
                    rotationZ = logoRotation.value
                }
        )
    }
}

@Composable
fun SplashBackground(
    modifier: Modifier = Modifier,
    onTransitionFinished: () -> Unit = {}
) {
    // Estados de animação do fundo
    val backgroundAlpha = remember { Animatable(1f) }
    val gradientAlpha = remember { Animatable(0f) }

    // Animação do fundo
    LaunchedEffect(Unit) {
        // Fase 1: Fundo estático (primeiros 800ms - cópia da splash padrão)
        delay(800)
        
        // Fase 2: Transição para gradiente (800-1500ms)
        backgroundAlpha.animateTo(0f, animationSpec = tween(700, easing = FastOutSlowInEasing))
        gradientAlpha.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
        
        onTransitionFinished()
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Fundo estático (cópia da splash padrão)
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
                // Simula o fundo da splash padrão
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Aqui seria a cor de fundo estática
                    // Por enquanto, usamos uma cor sólida
                }
            }
        }

        // Fundo gradiente (splash personalizada)
        Image(
            painter = painterResource(id = R.drawable.splash_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(gradientAlpha.value)
        )
    }
}
