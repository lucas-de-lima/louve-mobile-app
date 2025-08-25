package com.lucasdelima.louveapp.ui.common.auth

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.lucasdelima.louveapp.R
import com.lucasdelima.louveapp.ui.screens.settings.AuthError

/**
 * Helper reutilizável para fluxo de login com Google usando GoogleSignInClient estável.
 * Retorna uma lambda que, quando chamada, inicia o fluxo de login.
 * Inclui tratamento de erro robusto com mapeamento de códigos de erro do Google.
 */
@Composable
fun rememberGoogleSignInLauncher(
    onIdTokenReceived: (String) -> Unit,
    onError: (AuthError) -> Unit
): () -> Unit {
    val context = LocalContext.current

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val idToken = account.idToken
                
                if (idToken != null) {
                    onIdTokenReceived(idToken)
                } else {
                    onError(AuthError.InvalidCredentials)
                }
            } catch (e: ApiException) {
                val authError = when (e.statusCode) {
                    GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> AuthError.UserCancelled
                    GoogleSignInStatusCodes.NETWORK_ERROR -> AuthError.NetworkError
                    GoogleSignInStatusCodes.SIGN_IN_REQUIRED -> AuthError.InvalidCredentials
                    GoogleSignInStatusCodes.SIGN_IN_FAILED -> AuthError.InvalidCredentials
                    GoogleSignInStatusCodes.INVALID_ACCOUNT -> AuthError.InvalidCredentials
                    GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS -> AuthError.UnknownError("Login já está em andamento")
                    else -> AuthError.UnknownError("Falha no login com Google: ${e.statusCode}")
                }
                onError(authError)
            }
        }
    )

    return {
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
    }
}


