package com.lucasdelima.louveapp.domain.model

/**
 *
 * Representa as configurações sincronizáveis de um usuário.
 * O @DocumentId é uma anotação do Firestore que pode ser útil, mas como este é o domain,
 * a manteremos como um modelo de dados puro.
 */
data class UserSettings(
    // O ID do tema, ex: "dark_theme", "sweet_candy_theme", etc.
    // Usamos um valor padrão para garantir a criação segura do objeto.
    val themeId: String = "default_light_theme"
)
