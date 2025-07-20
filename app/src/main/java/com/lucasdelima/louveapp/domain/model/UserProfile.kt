package com.lucasdelima.louveapp.domain.model

/**
 * Representa os dados essenciais de um usuário logado.
 * Esta é uma classe de modelo de domínio, agnóstica de qualquer
 * plataforma ou serviço de autenticação.
 *
 * @param uid O identificador único do usuário (vindo do Firebase Auth).
 * @param name O nome de exibição do usuário (ex: "Lucas de Lima"). Pode ser nulo.
 * @param email O e-mail do usuário. Pode ser nulo.
 * @param photoUrl A URL para a foto de perfil do usuário. Pode ser nula.
 */
data class UserProfile(
    val uid: String,
    val name: String?,
    val email: String?,
    val photoUrl: String?
)
