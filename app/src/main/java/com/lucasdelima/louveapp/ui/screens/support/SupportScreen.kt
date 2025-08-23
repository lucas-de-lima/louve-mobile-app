package com.lucasdelima.louveapp.ui.screens.support

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.ui.screens.settings.AuthViewModel
import com.lucasdelima.louveapp.ui.theme.LouveTheme
import com.lucasdelima.louveapp.domain.repository.AnalyticsService
import javax.inject.Inject
import com.lucasdelima.louveapp.ui.screens.support.SupportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    onBack: () -> Unit,
    screenshotUri: String? = null,
    authViewModel: AuthViewModel = hiltViewModel(),
    viewModel: SupportViewModel = hiltViewModel()
) {
    val userProfile by authViewModel.userProfile.collectAsState()
    val context = LocalContext.current
    
    var name by remember { mutableStateOf(userProfile?.name ?: "") }
    var email by remember { mutableStateOf(userProfile?.email ?: "") }
    var description by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LouveTheme.backgrounds.screenBackground()
        
        // Track screen view
        LaunchedEffect(Unit) {
            viewModel.trackScreenView()
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Ajuda e Suporte") },
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
                // Ícone principal
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Suporte",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Título
                Text(
                    text = "Como podemos ajudar?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Descrição
                Text(
                    text = "Descreva o problema que você está enfrentando e nossa equipe entrará em contato para ajudar.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Formulário de suporte
                SupportForm(
                    name = name,
                    onNameChange = { name = it },
                    email = email,
                    onEmailChange = { email = it },
                    description = description,
                    onDescriptionChange = { description = it },
                    screenshotUri = screenshotUri,
                    onSubmit = {
                        isSubmitting = true
                        // TODO: Implementar envio do ticket
                        viewModel.trackSupportTicketSent()
                        isSubmitting = false
                        showSuccessDialog = true
                    },
                    isSubmitting = isSubmitting
                )
            }
        }
    }
    
    // Diálogo de sucesso
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                onBack()
            },
            title = { Text("Ticket Enviado!", color = MaterialTheme.colorScheme.onSurface) },
            text = { 
                Text("Obrigado pelo seu feedback! Nossa equipe analisará o problema e entrará em contato em breve.", color = MaterialTheme.colorScheme.onSurface)
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showSuccessDialog = false
                        onBack()
                    }
                ) {
                    Text("OK", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }
}

@Composable
private fun SupportForm(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    screenshotUri: String?,
    onSubmit: () -> Unit,
    isSubmitting: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Campo Nome
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Nome", color = MaterialTheme.colorScheme.onSurface) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Nome"
                    )
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("E-mail", color = MaterialTheme.colorScheme.onSurface) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "E-mail"
                    )
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo Descrição
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Descrição do problema", color = MaterialTheme.colorScheme.onSurface) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 6,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Descrição"
                    )
                }
            )
            
            // Screenshot (se disponível)
            if (screenshotUri != null) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Screenshot",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = "Screenshot anexado",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Uma captura da tela foi incluída para ajudar na análise",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botão de envio
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && email.isNotBlank() && description.isNotBlank() && !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enviando...", color = MaterialTheme.colorScheme.onSurface)
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enviar Ticket", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}
