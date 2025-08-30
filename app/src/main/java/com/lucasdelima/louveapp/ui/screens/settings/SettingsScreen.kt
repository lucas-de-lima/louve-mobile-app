package com.lucasdelima.louveapp.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.lucasdelima.louveapp.R
import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.domain.repository.AuthCredentials
import com.lucasdelima.louveapp.ui.common.auth.rememberGoogleSignInLauncher
import com.lucasdelima.louveapp.ui.theme.LouveThemeData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    val userProfile by authViewModel.userProfile.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current

    // Launcher reutilizável para Google Sign-In com tratamento de erro
    val startGoogleSignIn = rememberGoogleSignInLauncher(
        onIdTokenReceived = { idToken ->
            authViewModel.signIn(AuthCredentials.Google(idToken))
        },
        onError = { error ->
            // Mostrar erro específico baseado no tipo
            val message = when (error) {
                is AuthError.NetworkError -> "Erro de conexão. Verifique sua internet."
                is AuthError.UserCancelled -> "Login cancelado pelo usuário."
                is AuthError.InvalidCredentials -> "Credenciais inválidas. Tente novamente."
                is AuthError.FirebaseError -> error.message
                is AuthError.UnknownError -> "Erro inesperado. Tente novamente."
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    )

    // O fundo do tema já está sendo desenhado na MainActivity
    // Aqui apenas renderizamos o conteúdo da tela
    // O fundo cobre toda a tela, incluindo as áreas das barras de sistema
    Box(modifier = Modifier.fillMaxSize()) {
        // ✅ REMOVIDO: Fundo duplicado - agora é desenhado apenas na MainActivity

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Configurações") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
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
                ProfileSection(
                    userProfile = userProfile,
                    authState = authState,
                    onSignInClick = { startGoogleSignIn() },
                    onSignOutClick = { authViewModel.signOut() },
                    onRetryClick = {
                        // Extrair credenciais do estado atual para retry
                        (authState as? AuthUiState.Error)?.retry?.invoke()
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Text(
                    text = "Aparência",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Column(Modifier.selectableGroup()) {
                    settingsUiState.availableThemes.forEach { themeData ->
                        ThemePreviewCard(
                            themeData = themeData,
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
    authState: AuthUiState,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    Text(
        text = "Conta",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = 8.dp),
        color = MaterialTheme.colorScheme.onSurface
    )

    when {
        userProfile != null -> {
            // Usuário logado - mostrar perfil
            UserProfileCard(
                userProfile = userProfile,
                onSignOutClick = onSignOutClick
            )
        }

        authState is AuthUiState.Loading -> {
            // Estado de loading
            LoadingCard()
        }

        authState is AuthUiState.Error -> {
            // Estado de erro com retry
            ErrorCard(
                error = authState.error,
                onRetryClick = onRetryClick,
                onSignInClick = onSignInClick
            )
        }

        else -> {
            // Estado inicial - botão de login
            SignInButton(onClick = onSignInClick)
        }
    }
}

@Composable
private fun LoadingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Fazendo login...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ErrorCard(
    error: AuthError,
    onRetryClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "Erro",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Erro no login",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onError
                )
            }

            Text(
                text = when (error) {
                    is AuthError.NetworkError -> "Verifique sua conexão com a internet e tente novamente."
                    is AuthError.InvalidCredentials -> "Suas credenciais parecem estar inválidas. Tente fazer login novamente."
                    is AuthError.FirebaseError -> error.message
                    is AuthError.UnknownError -> "Ocorreu um erro inesperado. Tente novamente."
                    is AuthError.UserCancelled -> "Login cancelado. Tente novamente quando estiver pronto."
                    else -> "Falha na autenticação. Tente novamente."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onError
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onRetryClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tentar Novamente")
                }
                OutlinedButton(
                    onClick = onSignInClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Novo Login")
                }
            }
        }
    }
}

@Composable
private fun SignInButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google_logo),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color.Unspecified
        )
        Text("Entrar com o Google", modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun UserProfileCard(
    userProfile: UserProfile,
    onSignOutClick: () -> Unit
) {
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
            placeholder = painterResource(id = R.drawable.ic_splash_logo)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                userProfile.name ?: "Usuário",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                userProfile.email ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        OutlinedButton(onClick = onSignOutClick) {
            Text("Sair", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun ThemePreviewCard(
    themeData: LouveThemeData,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .selectable(
                selected = isSelected,
                onClick = onSelected,
                role = Role.RadioButton
            )
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // ✅ Preview do fundo do tema
            themeData.backgrounds.screenBackground()

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = themeData.colors.primary,
                        unselectedColor = themeData.colors.onSurfaceVariant,
                        disabledSelectedColor = themeData.colors.primary.copy(alpha = 0.38f),
                        disabledUnselectedColor = themeData.colors.onSurfaceVariant.copy(alpha = 0.38f)
                    )
                )

                Column {
                    Text(
                        text = themeData.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = themeData.colors.onSurface
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Indicador visual do tema selecionado
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Tema selecionado",
                        tint = themeData.colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
