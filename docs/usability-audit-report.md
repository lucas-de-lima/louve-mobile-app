# Relatório de Auditoria de Usabilidade - Louve App

> Gerado em: 2026-09-21
> Escopo: Análise completa de fluxos do aplicativo Louve App
> Metodologia: Revisão de código-fonte (Kotlin/Jetpack Compose), análise de navegação, fluxos de dados e interações de UI

---

## Resumo Executivo

20 falhas de usabilidade identificadas. 3 relatos do usuário, 17 descobertas em auditoria. Gravidade distribui-se entre Crítica (3), Alta (5), Média (7), Baixa (5).

---

## Relatos do Usuário (Confirmados)

### U01 - Campo de pesquisa sem botão "X" para limpar termo [CRÍTICA]

**Arquivo:** `app/src/main/java/com/lucasdelima/louveapp/ui/screens/home/components/SearchField.kt`

**Problema:** `SearchField` usa `TextField` com `leadingIcon` (lupa) mas NÃO tem `trailingIcon`. Usuário digita termo e não consegue limpar com 1 toque. Forçado a apagar caractere por caractere.

**Impacto:** Fricção direta em fluxo primário. Usuário pesquisa hino, quer trocar termo, precisa deletar manualmente.

**Solução:** Adicionar `trailingIcon` condicional (exibe "X" apenas quando `query.isNotBlank()`), com `onClick` que chama `onQueryChanged("")`.

---

### U02 - Scroll da lista de hinos perdido ao voltar do detalhe [CRÍTICA]

**Arquivos:** `HomeScreen.kt` (linha 56-67), `MainScreen.kt` (linha 46-61)

**Problema:** `HomeScreen` cria `rememberLazyListState()` no escopo do Composable. Quando navega para `hymnDetail/{hymnId}` via `bottomNavController.navigate("hymnDetail/$hymnId")` e depois volta via `popBackStack()`, a `HomeScreen` é recomposta com um NOVO `listState` (no fundo o ViewModel retém dados mas o `LazyListState` recria na raiz se o backstack entry for recomposto). Além disso, `LaunchedEffect(uiState.searchQuery)` força `animateScrollToItem(0)` quando `searchQuery` fica vazia — mas isso não deveria disparar na volta.

**Impacto:** Usuário rola até o hino 27, clica, lê, volta. Está no topo da lista. Precisa rolar tudo de novo. Experiência frustrante.

**Solução (múltiplas abordagens):**
1. Elevar `LazyListState` para `HomeViewModel` ou salvar em `SaveableState`
2. OU usar `rememberLazyListState()` com chave estável
3. OU usar `LaunchedEffect` que só rola ao topo quando a query muda DE para valor não-vazio para vazio (comparar previous vs current), não na recomposição inicial

---

### U03 - Listas de hinos: sem editar nome e sem apagar lista e sem opção para criar nova lista [ALTA]

**Arquivos:** `HymnListsTabContent.kt`, `FavoritesScreen.kt`, `HymnListsViewModel.kt`

**Problema:** `HymnListsTabContent` renderiza cards de lista via `HymnListCard` com apenas `onClick` (navega para detalhe). Não há:
- Botão de editar nome (embora `HymnListRepository.renameList()` exista no repositório — não exposto na UI)
- Botão de apagar lista (embora `HymnListRepository.deleteList()` exista — não exposto na UI)
- Botão de adicionar/criar nova lista a partir da UI de lista (investigar se existe ´HymnListRepository.createList()` se existir usar, se não criar.)

**Impacto:** Usuário cria lista com nome errado → não consegue corrigir. Cria lista duplicada → não consegue remover. Quer criar lista nova? → Fica preso ao fluxo de de criar lista a partir de hino que fovoritou (fluxo unico)

**Solução:** Adicionar menu contextual (3 dots) ou swipe-to-delete + diálogo de rename em cada `HymnListCard`. Ambas ações devem chamar `syncRemotely()` (já implícito no repositório). E adicionar botão superior de "+" para criar nova lista

**Sincronização:** `DataStoreHymnListRepository.renameList()` (linha 82) e `deleteList()` (linha 94) já disparam `syncRemotely()`. UI só precisa expor os botões.

---

## Falhas Descobertas na Auditoria

### D01 - HymnListDetail: sem remover hino da lista [ALTA]

**Arquivo:** `HymnListDetailScreen.kt`

**Problema:** Tela de detalhe da lista exibe hinos via `HymnCardItem` com `onClick` que navega para detalhe do hino. Não há ação de remover hino da lista (long-press, swipe, ícone de lixeira). `HymnListRepository.removeHymnFromList()` existe mas não é chamado.

**Impacto:** Usuário adiciona hino errado à lista. Sem saída para corrigir. Lista fica poluída permanentemente a menos que recrie do zero.

**Solução:** Adicionar ação de remover (swipe-to-dismiss no `LazyColumn` ou ícone de remover no card). Ação deve disparar `hymnListRepository.removeHymnFromList()`.

---

### D02 - HymnListDetail: sem reordenar hinos na lista [MÉDIA]

**Arquivo:** `HymnListDetailScreen.kt`

**Problema:** Hinos na lista aparecem na ordem de inserção (primeiro adicionado = primeiro na lista). Não há mecanismo de reordenação (drag & drop, mover para cima/baixo).

**Impacto:** Usuários de igreja que organizam hinos para culto precisam de ordem específica. Sem reorder, precisam recriar a lista inteira se a ordem estiver errada.

**Solução:** Adicionar `LazyColumn` com `reorderable` (biblioteca externa como `sh.calvin.reorderable` ou implementação manual com botões de seta). `HymnList` model já tem `hymnIds: List<String>` — reorder persiste ao salvar lista.

---

### D03 - Exclusão de conta não implementada (TODO stub) [CRÍTICA]

**Arquivo:** `ProfileScreen.kt` (linha 197)

**Problema:** `DeleteAccountDialog` existe com confirmação por digitar "excluir", mas `onConfirm` é:

```kotlin
// TODO: Implementar exclusão de conta
showDeleteAccountDialog = false
```

Ação não faz nada. Usuário passa pelo fluxo de confirmação, digita "excluir", clica em "Excluir Conta", e nada acontece.

**Impacto:** Engana o usuário dando falsa sensação de controle sobre dados. Violação de confiança.

**Solução:** Implementar exclusão real: chamar `FirebaseAuth.getCurrentUser()?.delete()`, apagar documento `users/{uid}` no Firestore, limpar DataStore local.

**Refutação de arquiteto** Uma vez no banco, sempre no banco. Ao inves de deletar, a conta do usuario fica desativada com seus dados ali. Essa é uma estrategia de bigtechs. A menos que exista uma contrariação legal brasileira para essa ideia, vamos apenas desativar e não deletar.

**Revisão:** LGPD Art. 18, VI garante direito à **eliminação** de dados pessoais tratados com consentimento. Soft-delete só é suficiente se PII for removida (nome, email, foto, Auth). Dados anonimizados (Art. 16, IV) podem reter. Padrão recomendado: (1) deletar Firebase Auth, (2) anonimizar doc `users/{uid}` (null em name/email/photoUrl), (3) deletar subcoleções `favorites/` e `settings/`, (4) manter `hymnLists/` com `creatorId: null` apenas se houver planos de listas públicas. Retenção indefinida de contas desativadas com PII intacta viola Art. 15 (fim do propósito).

---

### D04 - Estatísticas do perfil exibem "0" placeholder [BAIXA]

**Arquivo:** `ProfileScreen.kt` (linhas 268-275)

**Problema:** Cards de estatísticas mostram `value = "0"` com comentário `// TODO: Implementar contagem real`. Usuário logado vê "0" favoritos mesmo tendo dezenas.

**Impacto:** Baixa credibilidade. Usuário pode pensar que favoritos não estão salvos.

**Solução:** Calcular contagem real a partir do `FavoritesRepository` / `HymnListRepository`.

---

### D05 - Aba "Descubra" é tela placeholder não funcional [MÉDIA]

**Arquivo:** `DiscoverScreen.kt`

**Problema:** Tab inteira com cards "Em breve" que descrevem features futuras (Jornadas, História, Recomendações IA). Nenhum card é clicável. Nada funcional.

**Impacto:** Ocupa espaço na bottom nav com conteúdo inútil. Usuário toca na aba, vê promessas vazias, sente o app inacabado.

**Solução:** Remover aba até que pelo menos 1 feature esteja implementada, ou substituir por conteúdo útil (ex: hinos mais vistos, lista de rodízio semanal).

**Refutação de arquiteto** Manter como está, o app ainda está em teste controlado, open beta, sequer existe na playstore ainda.

**Revisão:** Contexto de open beta atenua gravidade para MÉDIA, não elimina. Tab "Descubra" ocupa slot na bottom nav com 0 valor funcional. Sugestão: substituir bottom nav por 3 abas (Harpa, Favoritos, Mais) até que pelo menos 1 feature (Jornadas, História ou Recomendações IA) esteja implementada. Isso remove placeholder sem perder o trabalho de design já feito (código pode ficar, só não navegar para ele). Alternativa: mover conteúdo "Descubra" para dentro da tab "Mais" como seção de preview. (Pedido da revisão negado pelo arquiteto, ignorar até segunda ordem)

---

### D06 - Sem feedback visual de status offline/sincronização [ALTA]

**Arquivos:** `ConnectivityMonitorService.kt`, `SyncScheduler.kt`, `SyncWorker.kt`

**Problema:** App monitora conectividade e agenda sync via WorkManager, mas o usuário NUNCA vê:
- Indicador de "offline" quando sem internet
- Indicador de "sincronizando..."
- Confirmação de "dados sincronizados"

Erros de sync remoto são apenas `Log.e` — o usuário nunca sabe que favoritos não foram salvos na nuvem.

**Impacto:** Usuário favorita hinos offline, acha que estão salvos. Troca de dispositivo. Dados perdidos. Nenhum aviso.

**Solução:** Adicionar bandeira/barra de status de conectividade no topo da tela. Exibir Snackbar quando sync falhar. `DefaultFavoritesRepository` já engole erros silenciosamente (linhas do `addFavorite`/`removeFavorite`): propagar erro para UI quando relevante.

**Comentario de arquiteto** Exibir erros para o usuario que ele não pode lidar é igualmente frustrante. Se uma sincronização falhou para um item, que tal um sistema de retry? Similar a filas de mensageria kafka, onde uma mensagem com erro para para "_dql" para ser consumida novamente ou debugada. Podemos procurar como implementar uma ideia similar em Android para alem do disparo de sincronia normal, existir um sistema que consome os com erro e tenta novamente. Falha de fato de loop de tentativas não resultar em sucesso. Essa ideia precisa ser analisada.

**Revisão:** DQL em mobile não justifica overhead (Room + fila) para app com 3 tipos de operação (favorito, settings, lista). MVP recomendado (0 novas dependências): (1) decompor `syncRemoteToLocal()` em operações por item, (2) usar `inputData` no WorkManager (`retry_count`, `max_retries=5`) para rastrear tentativas, (3) WorkManager já tem `BackoffPolicy.EXPONENTIAL`, (4) ao atingir limite, escrever registro em DataStore Preferences + retornar `Result.success()` — não `Result.retry()`, (5) UI lê DataStore em tela de configurações para exibir badge "N itens com falha". DQL completo (Room + `sync_operations` table + `FAILED_PERMANENTLY` status) só compensa quando número de operações crescer ou houver necessidade de depuração remota.

---

### D07 - Erros de sincronização remota silenciados [ALTA]

**Arquivos:** `DefaultFavoritesRepository.kt`, `DataStoreHymnListRepository.kt`

**Problema:** Em `DefaultFavoritesRepository`, erros de escrita remota são tratados como:

```kotlin
if (result is Result.Error) {
    Log.w(TAG, "Falha ao sincronizar favorito: ${result.message}")
    // Não propaga erro para UI
}
```

E em `DataStoreHymnListRepository.syncRemotely()` (linha 231):
```kotlin
is Result.Error -> Log.e(TAG, "remote sync failed id=$listId message=${result.message}", result.cause)
```

Usuário nunca sabe que dados não foram para nuvem.

**Impacto:** Mesmo online, usuário acha que dados estão sincronizados quando não estão. Perda de dados silenciosa.

**Comentario de arquiteto** Mesma situação do anterior. Realmente é um erro, não discordo. Mas até onde mostrar um erro para o usuario onde ele não tem poder de resolver, contribui para a melhoria? E mesmo que existisse um sistema hipotetico, delegar uma função do sistema ao usuario, não seria frustrante e exaustivo para o usuario? (Sua lista falhou de sincronizar, clique aqui e tente de novo! Melhor procurar outro app pra usar?) Entende? é melhor reforçar nossos sistemas e arquitetura. Mas tudo bem aviso do tipo (Falha ao sincronizar, voce está off line).

**Revisão:** Arquitetos vencem em 3 de 4 categorias. Classificação correta: erros transientes (timeout, 503, rate-limit) → retry silencioso, sem UI. Erros permanentes não-acionáveis (schema mismatch) → `Log.e` apenas. Erros permanentes acionáveis (token expirado, permissão revogada) → Snackbar com ação "Fazer login". Ações do usuário que falham (adicionar à lista) → Snackbar sem ação (usuário já sabe que tentou). O `DefaultFavoritesRepository` já engole erros — correção só necessária para erros acionáveis, onde propagar via `HymnDetailViewModel.eventFlow` (ShowSnackbar) é o padrão correto.

---

### D08 - Link de compartilhamento de hino contém placeholder [BAIXA]

**Arquivo:** `HymnDetailScreen.kt` (linha 281)

**Problema:** Texto compartilhado do hino contém `(Link para a loja em breve)`. Funcionalidade incompleta em produção.

**Impacto:** Quem recebe o hino compartilhado vê texto de placeholder. Ação de compartilhar fica incompleta.

**Solução:** Incluir link real da Play Store (assim que publicado) ou remover a linha.

**Comentario de arquiteto** App não lançado no Play Store. Porem compartilhar poderia ser o link de download do apk do link de release do ultimo build do github

**Revisão:** Remover placeholder agora é melhor que link do GitHub. Problemas: (1) Android bloqueia instalação de fontes não-conhecidas, (2) `/releases/latest` redireciona para página HTML, não APK direto, (3) link versionado quebra a cada release. Ação imediata: remover linha `(Link para a loja em breve)` do texto de share. Na publicação da Play Store: adicionar `https://play.google.com/store/apps/details?id=com.lucasdelima.louveapp`.

---

### D09 - Hinos hardcoded em memória [BAIXA]

**Arquivo:** `app/src/main/java/com/lucasdelima/louveapp/data/datasource/HymnDataSource.kt`

**Problema:** 54 hinos estão hardcoded como `List<Hymn>` na classe `HymnDataSource`. Qualquer correção, adição ou remoção de hino requer atualização do app (nova versão na Play Store).

**Impacto:** App não escala. Correções de letra de hino demoram dias (aprovação da loja).

**Solução:** Migrar hinos para Firestore ou arquivo JSON baixável remotamente com fallback local.

**Refutação de arquiteto** Esse é um detalhe de arquitetura, uma solução, e não um erro. Foi propositalmente feito pra ser assim. Adicionar download de mais de 600 itens para o usuario faz o usuario ficar preso a internet, adiciona peso ao aparelho para download massivo, explosão de leitura Firestore de diferentes fontes quando o app tiver milhares de usuarios. Hinos como parte do código elimina overhead de leitura e conversão de json para elementos renderizavel de UI

**Revisão:** Correção: são 640 hinos (não 54) em ~7.420 linhas no `HymnDataSource.kt`. Arquitetura atual é defensável para app de hinário — conteúdo canônico raramente muda, zero dependência de rede é win real em contexto offline-first. Middle ground que preserva offline e permite correções sem app update: migrar de Kotlin objects para `assets/hymns.json` (reduz APK em ~200KB, elimina 7.420 linhas de boilerplate) + background version-check contra documento único no Firestore (1 doc, não 640). Se hash remoto for mais novo, baixa e substitui cache local. `HymnRepository` interface não muda. Esforço estimado: 2-3 dias. (Negado pelo arquiteto, solução Kotlin permanesse até segunda ordem)

---

### D10 - Nenhuma forma de criar lista fora do detalhe do hino [ALTA]

**Arquivos:** `FavoritesScreen.kt`, `HymnListsTabContent.kt`

**Problema:** Única forma de criar lista é via `HymnDetailScreen` (botão "Criar nova lista" no BottomSheet). Na tab "Listas" não há FAB (Floating Action Button) ou botão "Nova Lista".

**Impacto:** Usuário vai em Favoritos → tab Listas → vê "Nenhuma lista de culto", mas não tem como criar uma dali. Precisa navegar para um hino específico primeiro. Fluxo quebrado.

**Solução:** Adicionar FAB ou botão "Nova Lista" no `HymnListsTabContent` com diálogo de criação de lista.

**Comentario de arquiteto** Sim, comentado no item U03. Concordo.

---

### D11 - LazyColumn aninhado no AddToListSuggestionCard [MÉDIA]

**Arquivo:** `AddToListSuggestionCard.kt` (linhas 261-296)

**Problema:** `ListSelectionContent` usa `LazyColumn` aninhado DENTRO de um `Column` pai. A `LazyColumn` tem altura fixa de `200.dp`. Scroll aninhado conflitante.

**Impacto:** Pode causar comportamento de scroll não-intuitivo. Se lista tiver muitos itens, alguns ficam ocultos sem indicador de scroll.

**Solução:** Substituir `LazyColumn` por `Column` com `verticalScroll(rememberScrollState())` dentro de `Modifier.weight(1f)` ou limitar altura do card via `Modifier.heightIn(max = ...)`.

---

### D12 - Selecionador de tema não mostra nome do tema atual [BAIXA]

**Arquivo:** `TopAppBarComponents.kt` (linhas 62-122)

**Problema:** Botão de temas na top bar exibe apenas ícone `Brush`. Nenhum texto indicando qual tema está ativo.

**Impacto:** Usuário precisa abrir dropdown toda vez para saber qual tema está usando.

**Solução:** Mostrar nome abreviado do tema ao lado do ícone (ex: "Tema: Clássico").

---

### D13 - SearchField sem suporte a acessibilidade para leitores de tela [MÉDIA]

**Arquivo:** `SearchField.kt`

**Problema:** `TextField` com `leadingIcon` de busca tem `contentDescription = "Ícone de busca"`. Nenhum `trailingIcon` para leitores de tela e nenhum `contentDescription` informando o estado atual da limpeza.

**Impacto:** Leitores de tela não conseguem interagir com a busca de forma eficiente.

**Solução:** Adicionar `trailingIcon` com `contentDescription` como "Limpar busca", condicional ao estado.

---

### D14 - Deleção de conta não propaga para Firestore [MÉDIA]

**Arquivo:** `ProfileScreen.kt` (linha 197-199)

**Problema:** Além do TODO, mesmo quando implementado, excluir conta via Firebase Auth não limpa Firestore `users/{uid}`. Dados órfãos permanecem em `favorites`, `settings`, `hymnLists`.

**Impacto:** Dados pessoais do usuário nunca são removidos do Firestore, mesmo após "excluir conta".

**Solução:** Ao deletar conta, limpar documento `users/{uid}` no Firestore recursivamente (subcoleções favorites, settings, hymnLists).

**Comentario de arquiteto** Como comentado no item relativo a esse assunto, a menos que exista norma legal para não fazer isso, nós não vamos deletar a conta. Vamos desativa-la, tornando a conta inalcançavel pelo sistema de UI e demais sistemas futuros.

**Revisão:** Vinculado a D03. Se optar por soft-delete: (1) deletar Firebase Auth, (2) no Firestore, anonimizar `users/{uid}` (null em name, email, photoUrl), (3) deletar subcoleções `favorites/` e `settings/` (dados não precisam reter), (4) manter `hymnLists/` apenas se houver plano de listas compartilháveis — nesse caso null em `creatorId`. Sem PII associado, dados anonimizados não são dados pessoais (LGPD Art. 12). Documentar período máximo de retenção (ex: 180 dias) para purga total.

---

### D15 - Sem confirmação ao remover hino de lista [BAIXA]

**Arquivo:** `HymnListDetailScreen.kt`

**Problema:** Se implementar remoção, não há diálogo de confirmação. Remoção acidental pode acontecer.

**Impacto:** Dados perdidos sem chance de reversão.

**Solução:** Adicionar `AlertDialog` de confirmação antes de remover. Opcional: undo Snackbar.

---

### D16 - HymnDetail: gesto de zoom threshold 25% sem feedback visual [BAIXA]

**Arquivo:** `HymnDetailScreen.kt` (linhas 320-333)

**Problema:** Zoom via `TransformableState` exige 25% de variação (`zoomThreshold = 0.25f`) para disparar, sem feedback visual do progresso. Usuário pinça e nada acontece — não sabe quanto mais precisa.

**Impacto:** Gesto não-descoberto. Usuário que poderia usar zoom não sabe que existe ou acha que não funciona.

**Solução:** Reduzir threshold para 0.1f-0.15f. Adicionar indicador visual (fonte pisca ou escala momentânea) ao atingir threshold.

---

### D17 - Cache offline de lista de hinos não existe [MÉDIA]

**Arquivo:** `HymnDataSource.kt`

**Problema:** `HymnRepositoryImpl` carrega `HymnDataSource.allHymns` lazy. Fonte é um objeto Kotlin. Não há cache em disco.

**Impacto:** Funciona em memória. Se app for morto e recriado, dados recarregam (nesse caso, sem problema porque é hardcoded, mas se migrar para remoto, sem cache = sem offline).

**Solução:** Se houver migração para dados remotos, implementar cache local (Room ou DataStore + serialização).

**Refutação de arquiteto** Não será implementado.

---

### D18 - FavoritesScreen tab "Favoritos" não permite gesto de desfavoritar [MÉDIA]

**Arquivo:** `FavoritesScreen.kt`

**Problema:** Tab de favoritos exibe lista de hinos. Não há ação de desfavoritar direto na lista (apenas via `HymnDetailScreen`). Usuário precisa abrir cada hino para desfavoritar.

**Impacto:** "Limpar favoritos" = abrir N hinos, tocar coração, voltar, repetir. Muitos passos.

**Solução:** Adicionar swipe-to-unfavorite no `HymnCardItem` da lista de favoritos ou ícone de coração diretamente no card.

**Comentario de arquiteto** Icone de coração diretamente no card é minha solução favorita. 

**Revisão:** Implementar `IconButton` como `trailingContent` no `HymnCardItem` (ou migrar para `ListItem` do M3). `IconButton` intrinsecamente consome o evento de clique — `Card.onClick` (navegar) não dispara quando toca no ícone. Usar `Icons.Default.Favorite` (filled) vs `Icons.Default.FavoriteBorder` (outlined) para estado. `contentDescription` dinâmico: "Remover dos favoritos" / "Adicionar aos favoritos". `FavoritesViewModel` precisa expor `toggleFavorite(hymnId)` — atualmente só tem leitura (via `favoritesRepository.getFavoriteHymnIds()`), precisa escrever (`favoritesRepository.addFavorite/removeFavorite`). 

---

### D19 - SharedViewModel: `MainSharedViewModel` unifica tema e perfil mas pode vazar escopo [MÉDIA]

**Arquivo:** `MainScreen.kt` (linha 32)

**Problema:** `MainSharedViewModel` é `hiltViewModel()` no escopo da `MainScreen`. Isso significa que ao navegar para fora do `NavHost` interno (ex: `SettingsScreen` que está no `rootNavController`), o `MainSharedViewModel` não é acessível diretamente. Settings cria outro `SettingsViewModel` e `AuthViewModel`.

**Impacto:** Se usuário muda tema em Settings, `MainSharedViewModel` não sabe até recombinação. Possível inconsistência temporária.

**Solução:** Mover temas para um `ViewModel` escopo de Activity (via `hiltViewModel()` no `MainActivity`) ou usar `CompositionLocal`.

**Comentario de arquiteto** Entendo, e concordo. Porem modificações desse escopo podem quebrar o funcionamento da UI, causando quebra visual de elementos na tela. Resultado -> Quebra o que está funcionando. Executar em branch separada e com validação cuidadosa.

**Revisão:** Preocupação procede mas problema alegado não existe. `MainSharedViewModel.currentTheme` e `MainViewModel.currentTheme` ambos leem `settingsRepository.theme` como `StateFlow`. Quando Settings escreve, ambos reagem. O `MainSharedViewModel.currentTheme` não é fonte de verdade — `MainViewModel.currentTheme` (Activity-scoped) que drive `LouveAppTheme`. Ação real: remover `currentTheme` e `selectTheme()` do `MainSharedViewModel` (dead code), manter parâmetro `currentTheme` fluindo de `MainViewModel` → `MainScreen` → `HomeScreen`. Nenhuma mudança de escopo. Baixo risco.

---

### D20 - FavoritesViewModel carrega todos os hinos eager [MÉDIA]

**Arquivo:** `FavoritesViewModel.kt` (linha 29)

**Problema:** `val allHymns: List<Hymn> = hymnRepository.getAllHymns()` chamado eager no `init` do companion object do `StateFlow`. Se repo fosse remoto, bloquearia.

**Impacto:** Atualmente inócuo (dados em memória), mas padrão que quebrará se repo migrar para operação I/O-bound.

**Solução:** Mover para `flow { emit(hymnRepository.getAllHymns()) }` dentro do `combine`.

**Refutação de arquiteto** Não será implementado

---

## Matriz de Priorização

| ID | Tipo | Descrição | Gravidade | Esforço Est. |
|---|---|---|---|---|
| U01 | Usuário | Search sem X | CRÍTICA | 1h |
| U02 | Usuário | Scroll perdido ao voltar | CRÍTICA | 2-4h |
| D03 | Auditoria | Excluir conta é stub | CRÍTICA | 4-8h |
| U03 | Usuário | Listas sem editar/apagar | ALTA | 4h |
| D01 | Auditoria | Lista sem remover hino | ALTA | 2h |
| D06 | Auditoria | Offline sem feedback | ALTA | 4h |
| D07 | Auditoria | Erros sync silenciados | ALTA | 3h |
| D10 | Auditoria | Sem criar lista na tab | ALTA | 2h |
| D02 | Auditoria | Lista sem reordenar | MÉDIA | 4-6h |
| D05 | Auditoria | Descubra placeholder | MÉDIA | 2h |
| D11 | Auditoria | LazyColumn aninhado | MÉDIA | 1h |
| D13 | Auditoria | Acessibilidade search | MÉDIA | 1h |
| D14 | Auditoria | Deleção sem limpar DB | MÉDIA | 2h |
| D17 | Auditoria | Sem cache offline | MÉDIA | 8h |
| D18 | Auditoria | Favoritos sem unfav | MÉDIA | 3h |
| D19 | Auditoria | Escopo ViewModel tema | MÉDIA | 2h |
| D20 | Auditoria | Load eager allHymns | MÉDIA | 1h |
| D04 | Auditoria | Stats "0" placeholder | BAIXA | 1h |
| D08 | Auditoria | Share link placeholder | BAIXA | 0.5h |
| D09 | Auditoria | Hinos hardcoded | BAIXA | 16h |
| D12 | Auditoria | Tema sem label ativa | BAIXA | 0.5h |
| D15 | Auditoria | Remover hino sem conf | BAIXA | 1h |
| D16 | Auditoria | Zoom threshold alto | BAIXA | 1h |

---

## Fluxos Auditados

- ✅ Splash → Main (navegação)
- ✅ Main → Harpa (HomeScreen) → search → hymn detail → voltar
- ✅ Main → Favoritos → tab Favoritos → hymn detail → voltar
- ✅ Main → Favoritos → tab Listas → list detail → hymn detail
- ✅ Main → Discover (placeholder)
- ✅ Main → More → Perfil / Configurações / Sobre / Suporte
- ✅ Settings → temas / login Google
- ✅ Profile → logout / excluir conta
- ✅ Add to list flow (BottomSheet + suggestion card)
- ✅ Sync offline → online (ConnectivityMonitor → SyncWorker)
- ✅ HymnList CRUD (create local → sync remote)
- ✅ Favorite toggle (local → sync remote)

---

## Recomendações Imediatas (Ordem de Prioridade)

1. **U01** Adicionar trailingIcon "X" no SearchField
2. **U02** Persistir scroll position ao voltar do detalhe
3. **U03** Adicionar menu editar/apagar nas listas
4. **D03** Implementar exclusão real de conta (Auth + Firestore + DataStore)
5. **D01** Adicionar remover hino na lista (swipe ou ícone)
6. **D06+D07** Feedback visual para estado online/offline e erros de sync
7. **D10** Adicionar botão "Nova Lista" na tab Listas