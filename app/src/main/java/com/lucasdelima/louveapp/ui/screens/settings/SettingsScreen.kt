package com.lucasdelima.louveapp.ui.screens.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.lucasdelima.louveapp.R
import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.domain.repository.AuthCredentials
import com.lucasdelima.louveapp.ui.theme.LouveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    val userProfile by authViewModel.userProfile.collectAsState()
    val context = LocalContext.current

    // --- LÓGICA DE LOGIN COM GoogleSignInClient (ESTÁVEL) ---
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Se o login for bem-sucedido, pegamos a conta e o idToken.
                val account = task.getResult(ApiException::class.java)!!
                val idToken = account.idToken!!
                // Enviamos o token para o nosso ViewModel para autenticar no Firebase.
                authViewModel.signIn(AuthCredentials.Google(idToken))
            } catch (e: ApiException) {
                // Se o usuário cancelar ou ocorrer um erro, mostramos uma mensagem.
                Toast.makeText(context, "Falha no login com o Google.", Toast.LENGTH_SHORT).show()
            }
        }
    )

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
                ProfileSection(
                    userProfile = userProfile,
                    onSignInClick = {
                        val webClientId = context.getString(R.string.web_client_id)
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(webClientId)
                            .requestEmail()
                            .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)

                        // Garante que a tela de seleção de contas sempre apareça.
                        googleSignInClient.signOut().addOnCompleteListener {
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        }
                    },
                    onSignOutClick = { authViewModel.signOut() }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

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
        OutlinedButton(
            onClick = onSignInClick,
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
    } else {
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
            onClick = null
        )
        Text(
            text = themeName,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
