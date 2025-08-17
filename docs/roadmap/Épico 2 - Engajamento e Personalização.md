Documento de Design: Épico 2 - Engajamento e Personalização
Público-Alvo: Arquitetos, Engenheiros, Designers e Gestores de Produto.

Propósito: Detalhar a arquitetura e a implementação das features de engajamento, incluindo a gamificação pessoal (streaks, marcos) e a primeira infraestrutura de backend ativo para agregação de dados. Este documento é o guia para tornar o Louve App uma experiência mais inteligente, pessoal e recompensadora.

1. Visão Geral e Filosofia
Este épico move o Louve App para além da funcionalidade reativa, introduzindo sistemas proativos que enriquecem a jornada do usuário. A filosofia central é a de "celebração sobre competição". Todas as mecânicas de gamificação serão projetadas para incentivar e celebrar o progresso pessoal do usuário na sua jornada de adoração, evitando rankings públicos que poderiam transformar a devoção em uma competição.

Tecnicamente, este é o nosso primeiro passo para um backend com lógica customizada, utilizando o poder do ecossistema serverless do Firebase para entregar insights e features que seriam impossíveis de se realizar apenas no cliente.

2. Gamificação Pessoal: Streaks e Marcos
O objetivo é criar um ciclo de engajamento positivo, motivando o uso diário e recompensando a exploração do conteúdo.

2.1. Sequência de Acesso Diário (Streaks)
Esta feature visa incentivar o hábito diário da devoção através de uma mecânica simples e eficaz.

Experiência do Usuário (UX):

A contagem de dias seguidos de acesso será exibida de forma proeminente na Tela de Perfil, abaixo do nome do usuário (ex: "🔥 7 dias de adoração contínua").

No futuro, este dado será um componente chave do Hub de Boas-Vindas.

Arquitetura de Dados (Firestore):

No documento do usuário (/users/{userId}), serão adicionados dois novos campos:

lastAccessDate: Timestamp - Armazena a data do último acesso registrado.

currentStreak: Number - A contagem atual de dias consecutivos.

Lógica de Negócio (Client-side):

Esta lógica será encapsulada em um UpdateStreakUseCase para ser reutilizável e testável.

Fluxo:

Na inicialização do app, após o login do usuário, o UseCase é invocado.

Ele lê o documento do usuário do Firestore.

Compara a lastAccessDate com a data atual (ignorando as horas).

Se lastAccessDate for de ontem: Incrementa currentStreak em 1.

Se lastAccessDate for de antes de ontem (ou nula): Reseta currentStreak para 1 (iniciando uma nova sequência).

Se lastAccessDate for de hoje: Não faz nada, para evitar múltiplos incrementos no mesmo dia.

Atualiza a lastAccessDate para o Timestamp atual.

Escreve os novos valores (currentStreak e lastAccessDate) de volta no Firestore.

2.2. Sistema de Marcos e Distintivos (Visão de Futuro)
Enquanto a implementação completa pertence a um épico futuro, a arquitetura de dados e a lógica de desbloqueio devem ser planejadas agora.

Filosofia: Marcos são conquistas pessoais que celebram a profundidade da interação do usuário com o conteúdo.

Arquitetura de Dados (Firestore):

Será criada uma nova sub-coleção: /users/{userId}/achievements.

Cada documento nesta coleção representará um marco desbloqueado. O ID do documento pode ser o nome do marco (ex: pilgrim_of_grace).

Estrutura do Documento: { name: "Peregrino da Graça", unlockedAt: Timestamp, details: "Leu todos os hinos sobre a Graça." }

Lógica de Desbloqueio:

A lógica será acionada pelo AnalyticsService. Certos eventos-chave (ex: hymn_favorited, hymn_viewed) podem disparar uma Cloud Function (via functions.analytics.event('eventName').onLog(...)) que verifica se as condições para um novo marco foram atingidas.

Exemplos de Marcos:

"Colecionador Iniciante": Favoritou 10 hinos.

"Madrugador": Abriu o app antes das 6h da manhã por 7 dias seguidos.

"Explorador Temático": Completou uma "Jornada de Louvor" (feature futura).

Recompensas:

O desbloqueio de marcos poderá liberar recompensas tangíveis no app, como temas exclusivos ou a habilidade de escolher um ícone de app alternativo, criando um valor real para o engajamento.

3. Backend Ativo v1: Agregação de Dados
Esta é a implementação da nossa primeira feature orientada a dados, que calculará os "hinos mais cantados" da semana.

3.1. Visão Geral e Arquitetura Serverless
A agregação de dados de todos os usuários é computacionalmente intensiva. A execução no cliente é inviável. Portanto, adotaremos uma pipeline serverless, assíncrona e altamente eficiente:

App Client -> Firebase Analytics -> BigQuery Export -> Scheduled Cloud Function -> Firestore

3.2. Passo a Passo da Implementação
Passo 1: Coleta (App Client)

O AnalyticsService será usado para registrar um evento crítico sempre que um usuário permanecer em uma tela de hino por um tempo mínimo (ex: > 20 segundos) para garantir a intenção de leitura.

Evento: trackHymnViewed(hymnId: String, durationInSeconds: Int)

Passo 2: Armazenamento e Exportação (Configuração Única)

No Console do Firebase, na seção de "Integrações", habilitar a exportação de dados do Analytics para o BigQuery.

Isso criará automaticamente um dataset no BigQuery que receberá os dados brutos de eventos do app diariamente.

Passo 3: Processamento (Cloud Function Agendada)

Será criada uma nova Cloud Function em TypeScript/JavaScript no nosso projeto Firebase.

Trigger: A função será acionada por um agendamento do Cloud Scheduler para rodar uma vez por semana (ex: todo domingo às 00:05 UTC).

Lógica:

A função se autentica com a API do BigQuery.

Executa uma query SQL no dataset de analytics para contar e agrupar os eventos hymn_viewed dos últimos 7 dias.

Query Exemplo:

SELECT
  (SELECT value.string_value FROM UNNEST(event_params) WHERE key = 'hymn_id') AS hymn_id,
  COUNT(*) AS view_count
FROM
  `your-project-id.analytics_XXXX.events_*`
WHERE
  event_name = 'hymn_viewed' AND
  _TABLE_SUFFIX BETWEEN FORMAT_DATE('%Y%m%d', DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY)) AND FORMAT_DATE('%Y%m%d', CURRENT_DATE())
GROUP BY
  hymn_id
ORDER BY
  view_count DESC
LIMIT 5;

A função processa o resultado da query.

Passo 4: Publicação (Firestore)

Após obter a lista dos "Top 5" hinos, a Cloud Function escreve esses dados em um documento global e de fácil acesso no Firestore.

Caminho: /app_metadata/weekly_top_hymns

Estrutura do Documento: { updated_at: Timestamp, top_hymns: [ { id: "15", rank: 1 }, { id: "42", rank: 2 }, ... ] }

3.3. Exibição na UI (App Client)
Um novo método no HymnRepository (ou um novo AppMetadataRepository) será responsável por ler o documento /app_metadata/weekly_top_hymns.

Como este dado muda apenas uma vez por semana, ele pode ser agressivamente cacheado no cliente para evitar leituras desnecessárias.

A UI (futuro Hub) consumirá este Flow de dados e renderizará a lista dos hinos mais populares, buscando os detalhes completos de cada hino (título, número) a partir dos IDs fornecidos.