package com.lucasdelima.louveapp.ui.screens.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.lucasdelima.louveapp.R
import com.lucasdelima.louveapp.ui.theme.LouveAppTheme
import com.lucasdelima.louveapp.ui.theme.SweetCandyTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onAnimationFinished: () -> Unit // Função a ser chamada quando a animação terminar
) {
    // 1. Timer para a navegação
    // LaunchedEffect(true) executa o bloco de código apenas uma vez, quando o Composable é exibido.
    LaunchedEffect(key1 = true) {
        delay(4000L) // Espera por 4 segundos
        onAnimationFinished() // Chama a função para navegar para a Home
    }

    // 2. Configuração da animação infinita para o brilho
    val infiniteTransition = rememberInfiniteTransition(label = "Splash Animation")

    // Animação para o brilho se mover na diagonal, por trás das silhuetas
    val lightPosition by infiniteTransition.animateFloat(
        initialValue = -0.2f, // Começa fora da tela (canto superior esquerdo)
        targetValue = 1.2f,   // Termina fora da tela (canto inferior direito)
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Light Position"
    )

    // Animação para o brilho piscar suavemente (fade in/out)
    val lightAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Light Alpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Camada 1: Fundo
        Image(
            painter = painterResource(id = R.drawable.splash_background),
            contentDescription = "Fundo da tela de abertura",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Camada 2: Efeito de Luz (que será animado)
        // Posicionado atrás das silhuetas
        Image(
            painter = painterResource(id = R.drawable.splash_light_effect),
            contentDescription = null, // Decorativo, não precisa de descrição
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    // Aplica as animações de posição e transparência
                    translationX = size.width * (lightPosition - 0.5f)
                    translationY = size.height * (lightPosition - 0.5f)
                }
                .alpha(lightAlpha)
        )

        // Camada 3: Silhuetas (na frente do brilho)
        Image(
            painter = painterResource(id = R.drawable.splash_man_silhouette),
            contentDescription = "Silhueta de homem em louvor",
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        Image(
            painter = painterResource(id = R.drawable.splash_woman_silhouette),
            contentDescription = "Silhueta de mulher em louvor",
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Camada 4: Notas Musicais
        Image(
            painter = painterResource(id = R.drawable.splash_music_notes),
            contentDescription = "Notas musicais flutuando",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    LouveAppTheme(themeData = SweetCandyTheme) {
        SplashScreen(onAnimationFinished = {})
    }
}