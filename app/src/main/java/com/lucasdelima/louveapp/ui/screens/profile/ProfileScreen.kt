package com.lucasdelima.louveapp.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.ui.screens.settings.AuthViewModel
import com.lucasdelima.louveapp.ui.theme.LouveTheme
import androidx.compose.ui.graphics.vector.ImageVector
import com.lucasdelima.louveapp.ui.screens.profile.ProfileViewModel
import com.lucasdelima.louveapp.ui.common.auth.rememberGoogleSignInLauncher
import com.lucasdelima.louveapp.domain.repository.AuthCredentials
import com.lucasdelima.louveapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userProfile by authViewModel.userProfile.collectAsState()
    
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    // Launcher reutilizável para Google Sign-In
    val startGoogleSignIn = rememberGoogleSignInLauncher { idToken ->
        authViewModel.signIn(AuthCredentials.Google(idToken))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LouveTheme.backgrounds.screenBackground()
        
        // Track screen view
        LaunchedEffect(Unit) {
            viewModel.trackScreenView()
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Perfil") },
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
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (userProfile != null) {
                    // Usuário logado
                    UserProfileContent(
                        userProfile = userProfile!!,
                        onLogout = { showLogoutDialog = true },
                        onDeleteAccount = { showDeleteAccountDialog = true }
                    )
                } else {
                    // Usuário não logado
                    NotLoggedInContent(
                        onGoogleSignIn = {
                            viewModel.trackLoginAttempt()
                            startGoogleSignIn()
                        }
                    )
                }
            }
        }
    }
    
    // Diálogo de logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sair da Conta") },
            text = { Text("Tem certeza que deseja sair da sua conta?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.signOut()
                        viewModel.trackUserLogout()
                        showLogoutDialog = false
                        onBack()
                    }
                ) {
                    Text("Sair")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de exclusão de conta
    if (showDeleteAccountDialog) {
        DeleteAccountDialog(
            onDismiss = { showDeleteAccountDialog = false },
            onConfirm = {
                // TODO: Implementar exclusão de conta
                showDeleteAccountDialog = false
            }
        )
    }
}

@Composable
private fun UserProfileContent(
    userProfile: UserProfile,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    // Foto de perfil
    AsyncImage(
        model = userProfile.photoUrl,
        contentDescription = "Foto do perfil",
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
    )
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Nome do usuário
    Text(
        text = userProfile.name ?: "Usuário",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    // Email
    Text(
        text = userProfile.email ?: "",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
    
    Spacer(modifier = Modifier.height(32.dp))
    
    // Seção de Estatísticas (preparação para o futuro)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Estatísticas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.Favorite,
                    label = "Favoritos",
                    value = "0" // TODO: Implementar contagem real
                )
                
                StatItem(
                    icon = Icons.Default.Star,
                    label = "Streak",
                    value = "0" // TODO: Implementar contagem real
                )
            }
        }
    }
    
    Spacer(modifier = Modifier.height(32.dp))
    
    // Seção de Gerenciamento da Conta
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Gerenciamento da Conta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botão Sair
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Sair"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sair da Conta", color = MaterialTheme.colorScheme.onSurface)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Botão Excluir Conta
            OutlinedButton(
                onClick = onDeleteAccount,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Excluir"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Excluir Conta", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun NotLoggedInContent(
    onGoogleSignIn: () -> Unit
) {
    Icon(
        imageVector = Icons.Default.Person,
        contentDescription = "Usuário não logado",
        modifier = Modifier.size(120.dp),
        tint = MaterialTheme.colorScheme.primary
    )
    
    Spacer(modifier = Modifier.height(24.dp))
    
    Text(
        text = "Usuário não logado",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Text(
        text = "Faça login para acessar seu perfil e gerenciar suas configurações.",
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    
    Spacer(modifier = Modifier.height(32.dp))
    
    // Botão de Login Google
    OutlinedButton(
        onClick = onGoogleSignIn,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        // Ícone do Google
        Icon(
            painter = painterResource(id = R.drawable.ic_google_logo),
            contentDescription = "Google",
            modifier = Modifier.size(20.dp),
            tint = Color.Unspecified
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = "Entrar com Google",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Text(
        text = "Acesse seus favoritos, configurações e sincronize dados entre dispositivos",
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun StatItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var confirmationText by remember { mutableStateOf("") }
    val isConfirmEnabled = confirmationText == "excluir"
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Excluir Conta", color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column {
                Text(
                    text = "Esta ação é irreversível e todos os seus dados (favoritos, configurações) serão permanentemente apagados.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Para confirmar, digite 'excluir' no campo abaixo:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = confirmationText,
                    onValueChange = { confirmationText = it },
                    label = { Text("Digite 'excluir'", color = MaterialTheme.colorScheme.onSurface) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = isConfirmEnabled
            ) {
                Text("Excluir Conta", color = MaterialTheme.colorScheme.onSurface)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    )
}
