package com.lucasdelima.louveapp.ui.common.auth

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.lucasdelima.louveapp.R

/**
 * Helper reutilizável para fluxo de login com Google usando GoogleSignInClient estável.
 * Retorna uma lambda que, quando chamada, inicia o fluxo de login.
 */
@Composable
fun rememberGoogleSignInLauncher(
    onIdTokenReceived: (String) -> Unit
): () -> Unit {
    val context = LocalContext.current

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val idToken = account.idToken!!
                onIdTokenReceived(idToken)
            } catch (e: ApiException) {
                Toast.makeText(context, "Falha no login com o Google.", Toast.LENGTH_SHORT).show()
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


