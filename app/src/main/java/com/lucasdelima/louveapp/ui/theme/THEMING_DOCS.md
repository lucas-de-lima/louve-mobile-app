
---

# Documentação: Sistema de Temas Dinâmicos - Louve App

## 1. Visão Geral

Este documento descreve a arquitetura e o fluxo de dados do sistema de temas dinâmicos do Louve App. O objetivo deste sistema é permitir a criação de múltiplos temas visuais (cores, fundos, tipografia) de forma desacoplada, permitindo que o usuário escolha sua preferência e que ela seja salva no dispositivo.

A arquitetura foi construída pensando em escalabilidade e manutenibilidade, seguindo os padrões de Clean Architecture e as melhores práticas do Jetpack Compose.

## 2. Ferramentas e Componentes Principais

O sistema é composto por um conjunto de ferramentas e classes que trabalham em harmonia.

### 2.1. `LouveThemeData.kt` - O DNA do Tema

É uma `data class` que serve como um "contrato" ou "DNA" para qualquer tema no aplicativo. Todo tema precisa fornecer as propriedades definidas aqui.

```kotlin
data class LouveThemeData(
    val name: String,
    val colors: ColorScheme,
    val typography: Typography,
    val screenBackground: @Composable (() -> Unit)? = null
)
```
* **`name`**: Um nome único para identificar o tema na UI (ex: "Padrão Claro").
* **`colors`**: Um objeto `ColorScheme` do Material 3, que define toda a paleta de cores (primária, secundária, fundo, etc.).
* **`typography`**: Um objeto `Typography` do Material 3, com os estilos de texto.
* **`screenBackground`**: Um Composable opcional que pode desenhar um fundo customizado (cor, gradiente, imagem) para as telas.

### 2.2. `AppThemes.kt` - O Catálogo de Temas

Este é o nosso "catálogo", um arquivo central onde todos os objetos `LouveThemeData` concretos são criados e listados. É aqui que a "arte" do design acontece.

```kotlin
// Exemplo de um tema definido
val SweetCandyTheme = LouveThemeData(
    name = "Sweet Candy",
    colors = SweetCandyColors, // Um ColorScheme customizado
    typography = Typography,
    screenBackground = { /* Composable que desenha o fundo gradiente */ }
)

// Lista que expõe todos os temas para o app
val AllThemes = listOf(DefaultTheme, DarkTheme, SweetCandyTheme)
```

### 2.3. `Theme.kt` - O Motor do Tema

Este arquivo contém o `Composable` principal `LouveAppTheme`, que atua como o motor do sistema.

* **`LouveAppTheme(themeData: LouveThemeData, ...)`**: Ele não sabe qual tema é qual. Sua única responsabilidade é receber um `LouveThemeData` e aplicá-lo.
* **`CompositionLocalProvider`**: É a ferramenta do Compose que usamos para "injetar" os dados do tema ativo na árvore de componentes. Qualquer Composable filho pode, então, acessar as propriedades do tema atual.
* **`LouveTheme` (objeto):** Um "acesso" customizado e limpo que criamos para ler as propriedades do tema atual em qualquer lugar do app, de forma segura. Ex: `LouveTheme.colors.primary`.

### 2.4. `SettingsRepository` & Jetpack DataStore - A Memória do App

Esta é a camada responsável por salvar e ler as preferências do usuário.

* **Jetpack DataStore**: É a tecnologia que usamos para salvar a escolha do tema no disco do celular de forma segura e assíncrona.
* **`SettingsRepository`**: Uma interface que abstrai a fonte de dados. O resto do app (ViewModels) só conversa com essa interface, sem saber se os dados estão vindo do DataStore local ou (no futuro) de uma conta na nuvem.
    ```kotlin
    interface SettingsRepository {
        val theme: Flow<String> // Expõe o nome do tema salvo como um Flow reativo
        suspend fun saveTheme(themeName: String)
    }
    ```

### 2.5. ViewModels & Hilt - A Orquestração

* **Hilt**: Nossa ferramenta de injeção de dependência. É ela que "monta" as peças, por exemplo, fornecendo o `SettingsRepository` para os ViewModels automaticamente.
* **`MainViewModel`**: Usado pela `MainActivity`, sua única função é observar o tema salvo no `SettingsRepository` e garantir que o app inteiro seja atualizado quando a preferência mudar.
* **`SettingsViewModel`**: O cérebro da `SettingsScreen`. Ele obtém a lista de temas disponíveis (`AllThemes`) e o tema atualmente selecionado (do `SettingsRepository`), e fornece a função para a UI salvar uma nova escolha.

## 3. Fluxo de Dados

### 3.1. Fluxo de Leitura (Ao Iniciar o App)

1.  `MainActivity` é criada e pede seu `MainViewModel` (criado pelo Hilt).
2.  `MainViewModel` começa a observar o `Flow` `settingsRepository.theme`.
3.  `SettingsRepository` lê o nome do tema salvo no `DataStore` (ou retorna o padrão).
4.  O `Flow` emite o nome do tema (ex: "Sweet Candy").
5.  `MainViewModel` recebe o nome e o expõe como um `StateFlow`.
6.  `MainActivity` coleta o estado, encontra o objeto `LouveThemeData` correspondente em `AllThemes`, e o passa para o `LouveAppTheme`.
7.  A UI do app inteiro é desenhada com o tema correto.

### 3.2. Fluxo de Escrita (Ao Trocar de Tema na `SettingsScreen`)

1.  O usuário clica em um `RadioButton` na `SettingsScreen`.
2.  A UI chama a função `settingsViewModel.selectTheme("Dark")`.
3.  O `SettingsViewModel` chama a função `settingsRepository.saveTheme("Dark")`.
4.  O `SettingsRepository` usa o `DataStore` para salvar a string "Dark" no disco.
5.  **A Mágica Reativa:** O `Flow` do DataStore automaticamente emite o novo valor ("Dark").
6.  Tanto o `MainViewModel` quanto o `SettingsViewModel` (que estão observando o mesmo `Flow`) recebem a atualização instantaneamente.
7.  A `MainActivity` atualiza o tema global do app, e a `SettingsScreen` atualiza qual `RadioButton` está marcado. Tudo em tempo real.

## 4. Como Usar e Estender o Sistema

### 4.1. Como Usar o Tema em Novos Componentes

Em qualquer `@Composable`, para usar as cores ou fontes do tema ativo, basta usar o objeto `LouveTheme`:

```kotlin
@Composable
fun MyCustomButton() {
    Button(
        onClick = { /* ... */ },
        colors = ButtonDefaults.buttonColors(
            containerColor = LouveTheme.colors.primary,
            contentColor = LouveTheme.colors.onPrimary
        )
    ) {
        Text(
            text = "Clique Aqui",
            style = LouveTheme.typography.labelLarge
        )
    }
}
```

### 4.2. Como Adicionar um Novo Tema

O processo é extremamente simples, graças à nossa arquitetura:

1.  **Abra `ui/theme/AppThemes.kt`**.
2.  **Crie uma nova paleta de cores** (se necessário), como fizemos para o `SweetCandyTheme`.
3.  **Crie um novo `val`** que seja uma instância de `LouveThemeData`, dando a ele um nome único e configurando suas cores, tipografia e fundo.
4.  **Adicione sua nova variável de tema à lista `AllThemes`** no final do arquivo.

Pronto! O resto do aplicativo (a `SettingsScreen` e o `MainActivity`) já vai reconhecer e exibir o novo tema automaticamente, sem precisar de mais nenhuma alteração.