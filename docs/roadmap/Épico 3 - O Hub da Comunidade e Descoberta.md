Documento de Design: Épico 3 - O Hub da Comunidade e Descoberta
Público-Alvo: Arquitetos, Engenheiros, Designers e Gestores de Produto.

Propósito: Detalhar a arquitetura e a implementação da nova HomeScreen como um hub de boas-vindas dinâmico e da primeira versão funcional da tela "Descubra". Este documento descreve o passo final para transformar o Louve App de uma ferramenta utilitária para uma plataforma de descoberta e engajamento contínuo.

1. Visão Geral e Filosofia
Este épico é a concretização da nossa visão de produto: transformar a experiência com a Harpa Cristã numa jornada de adoração, descoberta e entendimento. O Hub de Boas-Vindas será a nova porta de entrada do app, um espaço acolhedor e inteligente que oferece valor imediato e convida à exploração. A tela "Descubra" deixará de ser uma promessa para se tornar o primeiro portal para o "Ecossistema do Hino".

A filosofia é a da antecipação e curadoria. O app deve antecipar as necessidades do usuário (como continuar de onde parou) e guiá-lo através de conteúdo curado (como o Hino do Dia e as Jornadas de Louvor), tornando cada sessão uma oportunidade de aprendizado e conexão.

Pré-requisito Crítico: A execução deste épico depende fundamentalmente da conclusão bem-sucedida dos Épicos 1 (fundações de navegação e perfil) e 2 (pipeline de analytics e agregação de dados).

2. Implementação do Hub de Boas-Vindas (HomeScreen)
A HomeScreen se tornará a tela principal (startDestination) do app. Ela será uma composição de componentes dinâmicos, orquestrados pelo HomeViewModel.

2.1. Arquitetura e Fluxo de Dados
HomeViewModel: Será o cérebro da tela, responsável por:

Observar o perfil do usuário para a saudação personalizada.

Obter o último hino visualizado (a ser persistido localmente via DataStore ou Room).

Obter o "Hino do Dia" (via Remote Config).

Obter os "Hinos Mais Cantados" do AppMetadataRepository (dados gerados pelo backend no Épico 2).

Expor o estado de cada componente para a UI através de StateFlow.

2.2. Componentes da UI (em ordem de aparição)
1. Saudação Personalizada:

UI: Um Text proeminente no topo da tela.

Lógica: Exibirá "Bom dia, [Nome]!", "Boa tarde, [Nome]!" ou "Boa noite, [Nome]!". Se o usuário estiver deslogado, mostrará uma saudação genérica como "Bem-vindo(a) ao Louve App".

2. Card de Ação Rápida: "Continue de Onde Parou"

UI: Um card de destaque, visualmente distinto.

Lógica: Exibirá o título e o número do último hino que o usuário visualizou por mais de 20 segundos. Clicar no card navegará diretamente para a HymnDetailScreen daquele hino. Se não houver histórico, o card não será exibido.

Persistência: O ID do último hino visualizado será salvo localmente (DataStore é suficiente para este dado simples).

3. Card "Hino do Dia":

UI: Um card visualmente atraente.

Lógica: O HomeViewModel obterá o ID do hino do dia a partir do Firebase Remote Config. Isso nos dá total controle para curar o hino remotamente sem precisar atualizar o app. O ViewModel então buscará os detalhes do hino no HymnRepository para exibição.

4. Seção "Mais Cantados da Semana":

UI: Uma lista horizontal rolável (LazyRow) de 3 a 5 cards compactos.

Lógica: Consumirá os dados agregados pela nossa Cloud Function (implementada no Épico 2) e disponibilizados pelo AppMetadataRepository. Cada card exibirá o número e o título do hino, e ao ser clicado, navegará para a sua respectiva tela de detalhes.

5. Cards de Ação Rápida (Rodapé do Hub):

UI: Uma grade (Grid) ou linha (Row) com 2 ou 3 botões com ícones e texto.

Funcionalidades:

"Surpreenda-me!": Um botão que seleciona um hino aleatório dos 640 e navega para ele.

Busca por Número: Um OutlinedTextField compacto onde o usuário pode digitar um número e um botão "Ir" para navegar diretamente ao hino, oferecendo um atalho para usuários experientes.

3. Implementação da Tela "Descubra" v1
Esta tela substitui o mockup por uma primeira funcionalidade de conteúdo curado, as "Jornadas de Louvor".

3.1. Conceito: Jornadas de Louvor
O que são? São playlists de hinos selecionados e organizados por um tema teológico ou devocional específico (ex: "Hinos sobre a Graça", "Canções de Consolo para Tempos Difíceis", "A História da Salvação em 5 Hinos").

Objetivo: Guiar o usuário através do hinário de uma forma intencional, transformando o app numa ferramenta de estudo e meditação.

3.2. Arquitetura de Dados (Firestore)
A curadoria das jornadas será feita diretamente no Firestore, permitindo a adição e edição de conteúdo sem a necessidade de atualizar o app.

Estrutura da Coleção:

/journeys/ (coleção)
    ├── grace_journey (documento)
    │   ├── title: "Jornada da Graça"
    │   ├── description: "Uma seleção de hinos que exploram a profundidade da graça de Deus."
    │   ├── coverImageUrl: "url_para_uma_imagem_no_storage"
    │   ├── order: 1
    │   └── hymns: [ (sub-coleção)
    │       ├── hymn_1
    │       │   ├── hymnId: "208"
    │       │   └── order: 1
    │       ├── hymn_2
    │       │   ├── hymnId: "15"
    │       │   └── order: 2
    │   ]

3.3. Implementação (Cliente)
DiscoverViewModel e JourneysRepository:

Um novo JourneysRepository será criado para encapsular a lógica de leitura da coleção /journeys no Firestore.

O DiscoverViewModel consumirá este repositório e preparará a lista de jornadas para a UI.

DiscoverScreen (UI):

A tela exibirá uma lista vertical de "cards de jornada". Cada card mostrará a imagem de capa, o título e a descrição da jornada.

Clicar em um card levará o usuário para uma nova tela, a JourneyDetailScreen.

JourneyDetailScreen (UI):

Esta tela exibirá os detalhes da jornada (título, descrição) e uma lista ordenada dos hinos que a compõem.

Cada item na lista será um HymnCardItem clicável que navega para a tela de detalhes do respectivo hino.

4. Critérios de Sucesso
O épico será considerado um sucesso quando:

A nova HomeScreen for a tela de início do app e exibir todos os componentes dinâmicos com dados reais (ou placeholders graciosos, caso os dados não estejam disponíveis).

A tela "Descubra" exibir pelo menos 2-3 "Jornadas de Louvor" lidas a partir do Firestore.

A navegação entre o Hub, as Jornadas e as telas de detalhes dos hinos for fluida e intuitiva.

O impacto na performance de inicialização do app for mínimo, apesar da maior complexidade da HomeScreen.