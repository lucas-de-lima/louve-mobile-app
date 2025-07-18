package com.lucasdelima.louveapp.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.lucasdelima.louveapp.R
import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.ui.theme.LouveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel() // Pega o ViewModel de autenticação
) {
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    val userProfile by authViewModel.userProfile.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        LouveTheme.backgrounds.screenBackground()

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Configurações") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Seção de Perfil do Usuário
                ProfileSection(
                    userProfile = userProfile,
                    onSignInClick = { /* A lógica do clique virá aqui no próximo passo */ },
                    onSignOutClick = { authViewModel.signOut() }
                )

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                // Seção de Aparência (troca de tema)
                Text(
                    text = "Aparência",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Column(Modifier.selectableGroup()) {
                    settingsUiState.availableThemes.forEach { themeData ->
                        ThemeSelectorRow(
                            themeName = themeData.name,
                            isSelected = themeData.name == settingsUiState.selectedThemeName,
                            onSelected = { settingsViewModel.selectTheme(themeData.name) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileSection(
    userProfile: UserProfile?,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Text(
        text = "Conta",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    if (userProfile == null) {
        // --- VISÃO "NÃO LOGADO" ---
        OutlinedButton(
            onClick = onSignInClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google_logo), // Ícone do Google
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.Unspecified // Importante para não aplicar o tint do botão
            )
            Text("Entrar com o Google", modifier = Modifier.padding(start = 8.dp))
        }
    } else {
        // --- VISÃO "LOGADO" ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = userProfile.photoUrl,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_splash_logo) // Um placeholder
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    userProfile.name ?: "Usuário",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(userProfile.email ?: "", style = MaterialTheme.typography.bodyMedium)
            }
            OutlinedButton(onClick = onSignOutClick) {
                Text("Sair")
            }
        }
    }
}

@Composable
private fun ThemeSelectorRow(
    themeName: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelected,
                role = Role.RadioButton
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null // O clique é gerenciado pelo Modifier.selectable na Row
        )
        Text(
            text = themeName,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
