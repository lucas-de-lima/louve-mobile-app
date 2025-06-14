# Documentação: Sistema de Temas Dinâmicos - Louve App

## 1. Visão Geral

Este documento descreve a arquitetura e o fluxo de dados do sistema de temas dinâmicos do Louve App. O objetivo deste sistema é permitir a criação de múltiplos temas visuais (cores, fundos, tipografia) de forma desacoplada, permitindo que o utilizador escolha a sua preferência e que esta seja guardada no dispositivo.

A arquitetura foi construída a pensar na escalabilidade e manutenibilidade, seguindo os padrões de Clean Architecture e as melhores práticas do Jetpack Compose.

## 2. Ferramentas e Componentes Principais

O sistema é composto por um conjunto de ferramentas e classes que trabalham em harmonia.

### 2.1. `LouveThemeData.kt` - O ADN do Tema

Esta é a estrutura de dados central que define tudo o que um tema pode personalizar. A sua adição principal é o objeto `LouveBackgrounds`, que permite fundos contextuais.

```kotlin
// Define os diferentes tipos de fundo que um tema pode ter.
data class LouveBackgrounds(
    // Fundo para telas de navegação e listas (Home, Configurações).
    val screenBackground: @Composable () -> Unit,
    // Fundo "especial" para a tela de detalhes de um hino.
    val detailScreenBackground: @Composable () -> Unit
)

// O "contrato" completo de um tema.
data class LouveThemeData(
    val name: String,
    val colors: ColorScheme,
    val typography: Typography,
    val backgrounds: LouveBackgrounds // Contém os diferentes fundos
)
```

- **`name`**: Um nome único para identificar o tema na UI (ex: "Padrão Claro").
- **`colors`**: Um objeto `ColorScheme` do Material 3, que define toda a paleta de cores.
- **`typography`**: Um objeto `Typography` do Material 3, com os estilos de texto.
- **`backgrounds`**: O novo objeto que contém os Composables para os diferentes fundos da aplicação, permitindo a criação de "salinhas especiais".

### 2.2. `AppThemes.kt` - O Catálogo de Temas

Este é o nosso "catálogo", um ficheiro central onde todos os objetos `LouveThemeData` concretos são criados e listados. É aqui que a "arte" do design acontece, definindo as paletas e os fundos para cada tema.

```kotlin
// Exemplo da definição de um tema completo
val SweetCandyTheme = LouveThemeData(
    name = "Sweet Candy",
    colors = SweetCandyColors,
    typography = Typography,
    backgrounds = LouveBackgrounds(
        screenBackground = { /* Composable que desenha o gradiente da Home */ },
        detailScreenBackground = { /* Composable que desenha o fundo especial da "salinha" */ }
    )
)

// Lista que expõe todos os temas para o app
val AllThemes = listOf(DefaultTheme, DarkTheme, SweetCandyTheme)
```

### 2.3. `Theme.kt` - O Motor do Tema

Este ficheiro contém o `Composable` principal `LouveAppTheme`. A sua responsabilidade foi simplificada: agora, ele apenas "injeta" os dados do tema na hierarquia da UI e controla as barras de sistema.

- **`LouveAppTheme(themeData: LouveThemeData, ...)`**: Recebe um `LouveThemeData` e o disponibiliza para toda a aplicação.
- **`CompositionLocalProvider`**: A ferramenta do Compose que torna o tema ativo acessível em qualquer lugar.
- **`LouveTheme` (objeto):** O nosso ponto de acesso seguro e limpo para usar as propriedades do tema, agora incluindo `LouveTheme.backgrounds`.
- **`SideEffect`**: Usado para controlar a aparência (clara/escura) dos ícones das barras de sistema do Android.

### 2.4. `SettingsRepository` & Jetpack DataStore - A Memória do App

Esta camada, responsável por guardar e ler as preferências do utilizador, permanece inalterada.

- **Jetpack DataStore**: Guarda o nome do tema escolhido no disco.
- **`SettingsRepository`**: A interface que abstrai a fonte de dados, permitindo futuras integrações com contas na nuvem sem alterar a UI ou os ViewModels.

### 2.5. ViewModels & Hilt - A Orquestração

A orquestração também se mantém, mas agora suporta a nova arquitetura.

- **Hilt**: Gere as nossas dependências de forma limpa.
- **`MainViewModel`**: Observa a preferência de tema guardada e informa a `MainActivity` qual `LouveThemeData` deve ser aplicado.
- **`SettingsViewModel`**: Gere a lógica da `SettingsScreen`, fornecendo a lista de temas e guardando a escolha do utilizador.

## 3. Fluxo de Dados e Renderização de Fundo

O fluxo de dados para ler e escrever a preferência de tema continua o mesmo. O que muda é **como o fundo é desenhado**.

1.  A `MainActivity` aplica o `LouveAppTheme` correto e desenha o fundo principal (`screenBackground`) para toda a aplicação.
2.  Cada tela individual (`HomeScreen`, `SettingsScreen`) agora tem um `Scaffold` transparente, permitindo que o fundo principal desenhado na `MainActivity` apareça.
3.  A tela de detalhes (`HymnDetailScreen`), a nossa "salinha especial", desenha o seu próprio fundo (`detailScreenBackground`) por cima do fundo principal, criando um efeito de camadas.

## 4. Como Usar e Estender o Sistema

### 4.1. Como Criar Novas Telas

Qualquer nova tela criada deve seguir o padrão de ter um `Scaffold` transparente para respeitar o fundo global.

```kotlin
@Composable
fun MinhaNovaTela() {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = { /* ... */ }
    ) { innerPadding ->
        // Conteúdo da sua tela aqui...
    }
}
```

### 4.2. Como Adicionar um Novo Tema

O processo continua simples:

1.  **Abra `ui/theme/AppThemes.kt`**.
2.  Crie uma nova paleta de cores (se necessário).
3.  Crie uma nova definição de `LouveThemeData`, fornecendo um `name`, `colors`, `typography`, e o mais importante, um `LouveBackgrounds` com os dois tipos de fundo.
4.  Adicione a sua nova variável de tema à lista `AllThemes`.

O resto da aplicação irá reconhecer e adaptar-se ao novo tema automaticamente.
