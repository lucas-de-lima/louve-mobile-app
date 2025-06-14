package com.lucasdelima.louveapp.ui.screens.hymn

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucasdelima.louveapp.ui.theme.LouveAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HymnDetailScreen(
    onBack: () -> Unit,
    // viewModel: HymnDetailViewModel = viewModel() // Vamos adicionar o ViewModel depois
) {
    // Por enquanto, vamos usar dados de exemplo para focar no visual
    val hymnTitle = "Graça Infinita"
    val hymnNumber = "001"
    val hymnContent = """
        Amazing grace! How sweet the sound
        That saved a wretch like me!
        I once was lost, but now am found;
        Was blind, but now I see.

        'Twas grace that taught my heart to fear,
        And grace my fears relieved;
        How precious did that grace appear
        The hour I first believed.
    """.trimIndent()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hino $hymnNumber") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    // Ações de Fonte
                    IconButton(onClick = { /* Lógica virá depois */ }) {
                        Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Diminuir Fonte")
                    }
                    IconButton(onClick = { /* Lógica virá depois */ }) {
                        Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Aumentar Fonte")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Ações do rodapé
                        IconButton(onClick = { /* Lógica virá depois */ }) {
                            Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "Curtir")
                        }
                        IconButton(onClick = { /* Lógica virá depois */ }) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Compartilhar")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // Habilita a rolagem vertical
                .padding(horizontal = 24.dp, vertical = 16.dp) // Padding interno para o texto
        ) {
            // Título do Hino
            Text(
                text = hymnTitle,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Letra do Hino
            Text(
                text = hymnContent,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = MaterialTheme.typography.bodyLarge.fontSize * 1.5 // Aumenta o espaçamento entre linhas para melhor leitura
            )
        }
    }
}


// Um Preview para montarmos o visual da tela de forma isolada!
@Preview(showBackground = true)
@Composable
private fun HymnDetailScreenPreview() {
    LouveAppTheme {
        HymnDetailScreen(onBack = {})
    }
}