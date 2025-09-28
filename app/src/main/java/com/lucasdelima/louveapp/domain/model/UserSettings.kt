package com.lucasdelima.louveapp.domain.model

/**
 *
 * Representa as configurações sincronizáveis de um usuário.
 * O @DocumentId é uma anotação do Firestore que pode ser útil, mas como este é o domain,
 * a manteremos como um modelo de dados puro.
 */
data class UserSettings(
    // O ID do tema, ex: "default_light", "dark", "sweet_candy", etc.
    // Usamos o ID correto do DefaultTheme para garantir a criação segura do objeto.
    val themeId: String = "default_light"
)
