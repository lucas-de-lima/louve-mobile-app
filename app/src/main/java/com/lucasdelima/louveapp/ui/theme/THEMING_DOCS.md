# Documentação: Sistema de Temas Dinâmicos - Louve App

## 1. Visão Geral

Este documento descreve a arquitetura e o fluxo de dados do sistema de temas dinâmicos do Louve App. O objetivo deste sistema é permitir a criação de múltiplos temas visuais (cores, fundos, tipografia) de forma desacoplada, permitindo que o utilizador escolha a sua preferência e que esta seja guardada no dispositivo.

A arquitetura foi construída a pensar na escalabilidade e manutenibilidade, seguindo os padrões de Clean Architecture e as melhores práticas do Jetpack Compose.

## 2. Filosofia dos Temas

### 2.1. Alinhamento com a Visão do App

Os temas do Louve App não são apenas escolhas estéticas, mas **experiências espirituais** que se alinham com a filosofia de "jornada de adoração, descoberta e entendimento". Cada tema foi criado para representar diferentes momentos da vida espiritual e estados de alma, permitindo que o usuário escolha o ambiente visual que melhor se conecta com seu momento atual.

### 2.2. Os 8 Temas Filosóficos

#### **🌅 Aurora Matinal** - Inspiração e Novo Dia
- **Filosofia**: Representa o nascer do sol, novos começos e a inspiração que vem com a manhã
- **Momentos ideais**: Devoção matinal, louvor ao acordar, momentos de renovação
- **Paleta**: Dourado, laranja e creme - cores quentes que energizam e inspiram
- **Fundo**: Gradiente vertical que simula o nascer do sol, criando uma atmosfera de esperança

#### **🌙 Serenidade Noturna** - Reflexão e Paz
- **Filosofia**: Representa o céu noturno sereno, momentos de contemplação e paz interior
- **Momentos ideais**: Oração noturna, reflexão sobre o dia, momentos de quietude
- **Paleta**: Azul marinho, azul médio e azul claro - cores frias que acalmam e centram
- **Fundo**: Gradiente vertical que simula o céu noturno, criando uma atmosfera contemplativa

#### **🌱 Vida Verde** - Crescimento e Esperança
- **Filosofia**: Representa a vida que floresce, o crescimento espiritual e a esperança renovada
- **Momentos ideais**: Estudo bíblico, momentos de crescimento, celebração da vida
- **Paleta**: Verde, esmeralda e menta - cores naturais que revitalizam e renovam
- **Fundo**: Gradiente vertical que simula um jardim vivo, criando uma atmosfera de vitalidade

#### **🔥 Chama Sagrada** - Paixão e Adoração
- **Filosofia**: Representa o fogo sagrado, a paixão pela adoração e o fervor espiritual
- **Momentos ideais**: Louvor congregacional, momentos de adoração intensa, celebração
- **Paleta**: Vermelho, laranja e âmbar - cores quentes que aquecem e energizam
- **Fundo**: Gradiente vertical que simula o fogo sagrado, criando uma atmosfera devocional

#### **☁️ Céu Celestial** - Elevação e Transcendência
- **Filosofia**: Representa o céu infinito, a elevação espiritual e a transcendência divina
- **Momentos ideais**: Meditação profunda, momentos de elevação, conexão com o divino
- **Paleta**: Azul celestial, azul claro e azul celeste - cores que elevam e inspiram
- **Fundo**: Gradiente vertical que simula o céu infinito, criando uma atmosfera transcendente

#### **🍬 Sweet Candy** - Alegria e Celebração
- **Filosofia**: Representa a alegria pura, a celebração da vida e momentos de felicidade
- **Momentos ideais**: Celebrações, momentos de gratidão, louvor alegre
- **Paleta**: Rosa, azul e amarelo limão - cores vibrantes que energizam e alegram
- **Fundo**: Gradiente vertical vibrante, criando uma atmosfera festiva e alegre

#### **⚫ Escuro** - Sobreidade e Reverência
- **Filosofia**: Representa a sobreidade, a reverência e momentos de profunda reflexão
- **Momentos ideais**: Oração solene, reflexão profunda, momentos de reverência
- **Paleta**: Tons escuros e neutros - cores que centram e focam a atenção
- **Fundo**: Cor sólida escura, criando uma atmosfera contemplativa e reverente

#### **⚪ Padrão Claro** - Clareza e Simplicidade
- **Filosofia**: Representa a clareza, a simplicidade e a funcionalidade pura
- **Momentos ideais**: Uso diário, leitura de hinos, momentos de estudo
- **Paleta**: Cores padrão do Material Design - funcionalidade e legibilidade
- **Fundo**: Cor sólida clara, criando uma atmosfera limpa e focada

## 3. Ferramentas e Componentes Principais

O sistema é composto por um conjunto de ferramentas e classes que trabalham em harmonia.

### 3.1. `LouveThemeData.kt` - O ADN do Tema

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
    val id: String,
    val name: String,
    val category: ThemeCategory, // Light, Dark, Custom
    val colors: ColorScheme,
    val typography: Typography,
    val backgrounds: LouveBackgrounds // Contém os diferentes fundos
)
```

- **`id`**: Identificador único para o tema (ex: "aurora_morning").
- **`name`**: Nome amigável para exibir na UI (ex: "Aurora Matinal").
- **`category`**: Categoria do tema (Light, Dark, Custom).
- **`colors`**: Um objeto `ColorScheme` do Material 3, que define toda a paleta de cores.
- **`typography`**: Um objeto `Typography` do Material 3, com os estilos de texto.
- **`backgrounds`**: O novo objeto que contém os Composables para os diferentes fundos da aplicação, permitindo a criação de "salinhas especiais".

### 3.2. `AppThemes.kt` - O Catálogo de Temas

Este é o nosso "catálogo", um ficheiro central onde todos os objetos `LouveThemeData` concretos são criados e listados. É aqui que a "arte" do design acontece, definindo as paletas e os fundos para cada tema.

```kotlin
// Exemplo da definição de um tema completo
val AuroraMorningTheme = LouveThemeData(
    id = "aurora_morning",
    name = "Aurora Matinal",
    category = ThemeCategory.Custom,
    colors = AuroraMorningColors,
    typography = Typography,
    backgrounds = LouveBackgrounds(
        screenBackground = { /* Composable que desenha o nascer do sol */ },
        detailScreenBackground = { /* Composable que desenha a luz dourada */ }
    )
)

// Lista que expõe todos os temas para o app
val AllThemes = listOf(
    DefaultTheme, DarkTheme, SweetCandyTheme,
    AuroraMorningTheme, SerenityNightTheme, LifeGreenTheme,
    SacredFlameTheme, CelestialSkyTheme
)
```

### 3.3. `Theme.kt` - O Motor do Tema

Este ficheiro contém o `Composable` principal `LouveAppTheme`. A sua responsabilidade foi simplificada: agora, ele apenas "injeta" os dados do tema na hierarquia da UI e controla as barras de sistema.

- **`LouveAppTheme(themeData: LouveThemeData, ...)`**: Recebe um `LouveThemeData` e o disponibiliza para toda a aplicação.
- **`CompositionLocalProvider`**: A ferramenta do Compose que torna o tema ativo acessível em qualquer lugar.
- **`LouveTheme` (objeto):** O nosso ponto de acesso seguro e limpo para usar as propriedades do tema, agora incluindo `LouveTheme.backgrounds`.
- **`SideEffect`**: Usado para controlar a aparência (clara/escura) dos ícones das barras de sistema do Android.

### 3.4. `SettingsRepository` & Jetpack DataStore - A Memória do App

Esta camada, responsável por guardar e ler as preferências do utilizador, permanece inalterada.

- **Jetpack DataStore**: Guarda o nome do tema escolhido no disco.
- **`SettingsRepository`**: A interface que abstrai a fonte de dados, permitindo futuras integrações com contas na nuvem sem alterar a UI ou os ViewModels.

### 3.5. ViewModels & Hilt - A Orquestração

A orquestração também se mantém, mas agora suporta a nova arquitetura.

- **Hilt**: Gere as nossas dependências de forma limpa.
- **`MainViewModel`**: Observa a preferência de tema guardada e informa a `MainActivity` qual `LouveThemeData` deve ser aplicado.
- **`SettingsViewModel`**: Gere a lógica da `SettingsScreen`, fornecendo a lista de temas e guardando a escolha do utilizador.

## 4. Fluxo de Dados e Renderização de Fundo

O fluxo de dados para ler e escrever a preferência de tema continua o mesmo. O que muda é **como o fundo é desenhado**.

1.  A `MainActivity` aplica o `LouveAppTheme` correto e desenha o fundo principal (`screenBackground`) para toda a aplicação.
2.  Cada tela individual (`HomeScreen`, `SettingsScreen`) agora tem um `Scaffold` transparente, permitindo que o fundo principal desenhado na `MainActivity` apareça.
3.  A tela de detalhes (`HymnDetailScreen`), a nossa "salinha especial", desenha o seu próprio fundo (`detailScreenBackground`) por cima do fundo principal, criando um efeito de camadas.

## 5. Como Usar e Estender o Sistema

### 5.1. Como Criar Novas Telas

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

### 5.2. Como Adicionar um Novo Tema

O processo continua simples:

1.  **Abra `ui/theme/AppThemes.kt`**.
2.  Crie uma nova paleta de cores (se necessário).
3.  Crie uma nova definição de `LouveThemeData`, fornecendo um `id`, `name`, `category`, `colors`, `typography`, e o mais importante, um `LouveBackgrounds` com os dois tipos de fundo.
4.  Adicione a sua nova variável de tema à lista `AllThemes`.

O resto da aplicação irá reconhecer e adaptar-se ao novo tema automaticamente.

### 5.3. Diretrizes para Novos Temas

Ao criar novos temas, considere:

- **Filosofia espiritual**: Qual momento ou estado espiritual o tema representa?
- **Paleta de cores**: As cores devem ter contraste adequado e seguir guidelines de acessibilidade
- **Fundos contextuais**: Os fundos devem criar a atmosfera desejada sem comprometer a legibilidade
- **Consistência**: O tema deve funcionar bem em todas as telas e componentes
- **Performance**: Os fundos devem ser eficientes e não causar lag

## 6. Testando e Validando Temas

### 6.1. Checklist de Validação

- [ ] **Cores**: Paleta Material 3 válida com contraste adequado
- [ ] **Fundos**: Renderizam corretamente em todas as telas
- [ ] **Barras de sistema**: Ícones visíveis sobre o fundo
- [ ] **Performance**: Sem lag na mudança de tema
- [ ] **Acessibilidade**: Testado com ferramentas de contraste
- [ ] **Consistência**: Funciona em diferentes tamanhos de tela
- [ ] **Filosofia**: O tema representa claramente sua intenção espiritual

### 6.2. Preview no Android Studio

```kotlin
@Preview(showBackground = true)
@Composable
fun AuroraMorningThemePreview() {
    LouveAppTheme(themeData = AuroraMorningTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Text(
                text = "Aurora Matinal - Inspiração e Novo Dia",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
```

## 7. Conclusão

O sistema de temas do Louve App vai além da estética - é uma ferramenta espiritual que permite aos usuários criar o ambiente visual perfeito para sua jornada de adoração. Cada tema foi cuidadosamente projetado para representar diferentes momentos da vida espiritual, criando uma experiência verdadeiramente personalizada e significativa.

Ao seguir as diretrizes estabelecidas nesta documentação, qualquer desenvolvedor pode criar novos temas que se integrem perfeitamente ao sistema existente, mantendo a qualidade e a filosofia que fazem do Louve App uma ferramenta única de adoração digital.
