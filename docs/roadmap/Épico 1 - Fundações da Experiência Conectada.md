Documento de Design: Épico 1 - Fundações da Experiência Conectada
Público-Alvo: Arquitetos, Engenheiros, Designers e Gestores de Produto.

Propósito: Detalhar a visão, os requisitos técnicos e a filosofia por trás da reestruturação da navegação e da introdução das funcionalidades de perfil, suporte e analytics. Este é o guia mestre para a execução do Épico 1.

1. Visão Geral e Filosofia
Este épico marca a transição do Louve App de uma ferramenta de leitura para uma plataforma de adoração pessoal e conectada. O objetivo não é apenas adicionar telas, mas estabelecer as fundações arquiteturais e de experiência do usuário que sustentarão o crescimento futuro do app.

A filosofia é a de clareza e propósito: cada nova seção deve ter um objetivo claro, e a experiência do usuário deve ser intuitiva, guiando-o naturalmente através das novas funcionalidades. Estamos a construir o "sistema nervoso central" do app.

1.1. Reestruturação da Navegação Principal
A navegação inferior será expandida para se tornar o principal meio de locomoção do usuário, preparando o terreno para o futuro Hub.

Especificações Técnicas e de UI
Barra de Navegação Inferior (BottomNavBar):

A barra será composta por 4 abas iniciais:

Harpa (Ícone: Icons.Filled.Book ou similar) - Rota: hymns_screen

Favoritos (Ícone: Icons.Filled.Favorite) - Rota: favorites_screen

Descubra (Ícone: Icons.Filled.Explore ou Bússola) - Rota: discover_screen

Mais (Ícone: Icons.Filled.Menu) - Rota: more_screen

A tela principal (startDestination do NavHost aninhado) será a rota da Harpa, mantendo o acesso rápido à funcionalidade principal do app por enquanto.

NavGraph e Preservação de Estado:

O NavGraph aninhado, controlado pelo MainScreen, será ajustado para incluir as novas rotas.

É crítico que o estado de cada aba seja preservado. Ao navegar da "Harpa" para "Mais" e voltar, o usuário deve encontrar a lista de hinos na mesma posição de scroll e com o mesmo termo de busca aplicado. O rememberNavController() no MainScreen já auxilia nisso, mas a gestão de estado nos ViewModels de cada tela deve ser robusta.

1.2. A Tela "Mais": O Centro de Controle
Esta tela age como um índice organizado, oferecendo acesso a todas as funcionalidades que não são de conteúdo direto.

Layout e Componentes
A tela será uma lista vertical de itens clicáveis, com seções visualmente distintas.

Seção de Perfil (Topo):

Um card proeminente que funciona como atalho para a ProfileScreen.

Estado Deslogado: Exibe um ícone genérico de perfil, o texto "Visitante" e um subtítulo convidativo: "Entre para salvar seus hinos e configurações". Clicar neste card inicia o fluxo de login.

Estado Logado: Exibe a foto do perfil do Google, o nome do usuário e o e-mail.

Seção de Ações do App:

Configurações: (Ícone: Engrenagem) Leva para a SettingsScreen existente. O atalho no topo da HymnListScreen será removido.

Compartilhar o App: (Ícone: Compartilhar) Aciona o Share Sheet nativo do Android com um link para a Play Store e um texto pré-definido.

Seção de Informações e Suporte:

Sobre: (Ícone: Informação) Leva para a AboutScreen.

Ajuda e Suporte: (Ícone: Ajuda/Balão de diálogo) Leva para a SupportScreen.

1.3. Tela de Perfil do Usuário
Esta tela solidifica a identidade do usuário dentro do app.

Features e Fluxos
UI Padrão da Indústria:

Cabeçalho com foto de perfil em destaque, nome e e-mail.

Seção "Estatísticas" (preparação para o futuro): Exibirá o loginStreak do Épico 2.

Seção "Gerenciamento da Conta":

Botão "Sair" (Logout): Invoca a função signOut() no AuthViewModel. A UI deve reagir imediatamente, atualizando a tela "Mais" para o estado deslogado.

Botão "Excluir Conta":

Fluxo de UX: Ao clicar, exibe um diálogo de confirmação crítico, explicando que a ação é irreversível e todos os dados (favoritos, configurações) serão permanentemente apagados. Requer uma segunda confirmação (ex: digitar "excluir").

Backend: A confirmação deve acionar uma Cloud Function (onCall) que executa a exclusão segura dos dados do usuário no Firestore e, em seguida, remove a conta do Firebase Authentication. O app recebe a confirmação e desloga o usuário.

1.4. Sistema de Ajuda e Report de Bug (Shake-to-Report)
Esta é uma feature de qualidade premium que demonstra nosso compromisso com a estabilidade e com o feedback do usuário.

Arquitetura Detalhada
1. Detecção do Gesto (Frontend):

Um SensorManager será usado para monitorar o acelerômetro.

A lógica de detecção de "agitar" será encapsulada e gerenciada a partir da MainActivity para estar disponível em todo o app.

Condição: O listener de agitação só será ativado se o usuário estiver logado, para evitar spam e garantir que temos o contexto do usuário (uid) para o ticket.

2. Captura e Otimização da Imagem (Frontend):

Ao detectar o gesto, o app programaticamente captura a View raiz atual como um Bitmap.

Este Bitmap passa por um pipeline de otimização no cliente:

Redimensionamento: Para uma largura máxima (ex: 1080px).

Compressão: Convertido para o formato WEBP com 75% de qualidade.

O Bitmap otimizado é salvo temporariamente no cache do app e seu URI é passado para a SupportScreen.

3. Tela de Suporte (SupportScreen):

A tela exibe o formulário com nome/e-mail pré-preenchidos a partir do UserProfile.

Mostra uma miniatura do screenshot capturado.

Contém um campo de texto para o usuário descrever o problema.

4. Upload e Envio (Backend):

Ao clicar em "Enviar":

O app faz o upload da imagem otimizada para o Firebase Storage num caminho seguro e privado: /support_tickets/{userId}/{ticketId}.webp.

Após o upload bem-sucedido, o app obtém a URL de download da imagem.

O app invoca uma Cloud Function (https://us-central1-your-project.cloudfunctions.net/submitSupportTicket) passando um objeto JSON: { userEmail: "...", userName: "...", description: "...", screenshotUrl: "..." }.

5. Processamento e Notificação (Cloud Function):

A função submitSupportTicket recebe os dados.

Ela se autentica com um serviço de e-mail transacional (ex: SendGrid, ou a extensão Trigger Email do Firebase).

Formata e envia um e-mail para dev.lucasdelima@gmail.com com todos os detalhes, incluindo o link para a imagem.

Retorna uma resposta de sucesso para o app, que então exibe uma mensagem de "Obrigado!" ao usuário.

1.5. Fundação Técnica: A Camada de Analytics
Esta é uma tarefa de infraestrutura não-negociável para permitir o desenvolvimento orientado a dados.

Design e Responsabilidades
AnalyticsService (Interface):

Define os métodos para todos os eventos de tracking. Ex: trackHymnViewed(hymnId: String, duration: Int).

FirebaseAnalyticsService (Implementação):

Implementa a interface, formatando os dados e chamando o SDK do Firebase.

Injetado via Hilt como um singleton.

Eventos Iniciais a serem Implementados:

trackUserLogin(), trackUserLogout()

trackScreenView(screenName: String)

trackShareApp()

trackSupportTicketSent()

1.6. Telas de Conteúdo Estático
AboutScreen:

Conteúdo:

Título: "Sobre o Louve App"

Texto: Um parágrafo adaptado da "Visão e Filosofia do Produto".

Versão do App: Lida dinamicamente a partir do BuildConfig.

Links clicáveis para "Política de Privacidade" e "Licenças de Código Aberto".

DiscoverScreen (Mockup Inspirador):

A estrutura completa (Rota, Screen, ViewModel) será criada.

A UI não será um Text("Em breve"), mas um design visualmente agradável com um texto que gera antecipação, alinhado com a visão do produto para a descoberta de conteúdo.