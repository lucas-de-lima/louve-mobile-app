# EPIC: Correções de Usabilidade - Louve App

Ordem: relatos do usuário → auditoria. Itens rejeitados pelo arquiteto excluídos.

---

## FASE 1 - Relatos do Usuário

### FEAT-U01: Adicionar botão "X" para limpar busca [CRÍTICA]

**Arquivos:** `ui/screens/home/components/SearchField.kt`

**Problema:** `TextField` sem `trailingIcon`. Usuário precisa apagar caractere por caractere.

**Solução:** Adicionar `trailingIcon` condicional — exibe `Icons.Default.Close` quando `query.isNotBlank()`. `onClick` chama `onQueryChanged("")`.

**Critérios de aceite:**
- [ ] `trailingIcon` com `Icons.Default.Close` visível apenas quando query tem texto
- [ ] `contentDescription` = "Limpar busca"
- [ ] `onClick` limpa query completamente
- [ ] `TextField` mantém `singleLine = true`
- [ ] Teste manual: digitar, ver X, tocar X, campo limpo

---

### FEAT-U02: Preservar scroll da lista ao voltar do detalhe do hino [CRÍTICA]

**Arquivos:** `ui/screens/home/HomeScreen.kt`, `ui/screens/main/MainScreen.kt`

**Problema:** `rememberLazyListState()` recria ao recompor HomeScreen. `LaunchedEffect` força scroll ao topo quando query fica vazia (dispara na volta também).

**Solução:** Opção 3 — `LaunchedEffect` que compara previous vs current query. Só rolar ao topo quando query muda DE valor não-vazio PARA vazio (usuário limpou busca), não na recomposição inicial.

**Critérios de aceite:**
- [ ] Rola até topo apenas quando usuário limpa busca manualmente
- [ ] Voltar do detalhe mantém posição de scroll exata
- [ ] `listState` sobrevive à recomposição do backstack

---

### FEAT-U03: Listas de hinos - editar nome, apagar, criar nova [ALTA]

**Arquivos:** `ui/screens/favorites/HymnListsTabContent.kt`, `FavoritesScreen.kt`, `HymnListsViewModel.kt`

**Problema:** `HymnListCard` só tem `onClick` (navegar). Sem editar nome, apagar lista, criar nova.

**Solução:**
- Cada card: menu 3 dots ou `IconButton` com "Editar nome" e "Apagar lista"
- Editar nome: `AlertDialog` com `OutlinedTextField`, chama `renameList(id, name)`
- Apagar lista: `AlertDialog` de confirmação, chama `deleteList(id)`. Dispara sync automaticamente.
- Topo da tab Listas: botão "+" ou FAB para criar nova lista, chama `createList(name, null)`

**Critérios de aceite:**
- [ ] Editar nome abre diálogo com campo preenchido com nome atual
- [ ] Apagar lista mostra confirmação "Tem certeza?"
- [ ] Criar nova lista via FAB na tab Listas
- [ ] Após editar/apagar/criar, lista reflite imediatamente na UI
- [ ] Sync remoto disparado (já implícito no repositório)

---

## FASE 2 - Auditoria

### FEAT-D01: Remover hino da lista de culto [ALTA]

**Arquivos:** `ui/screens/hymnlistdetail/HymnListDetailScreen.kt`

**Problema:** Tela de detalhe da lista não tem ação de remover hino.

**Solução:** Adicionar `IconButton` com `Icons.Default.Delete` (ou `Close`) em cada `HymnCardItem` no `HymnListDetailScreen`. `onClick` chama `removeHymnFromList(listId, hymnId)`.

**Critérios de aceite:**
- [ ] Ícone de remover visível em cada hino na lista
- [ ] Diálogo de confirmação antes de remover (FEAT-D15)
- [ ] Lista atualizada após remoção sem refresh manual
- [ ] Sync remoto disparado

---

### FEAT-D02: Reordenar hinos na lista de culto [MÉDIA]

**Arquivos:** `ui/screens/hymnlistdetail/HymnListDetailScreen.kt`

**Problema:** Hinos aparecem na ordem de inserção, sem reorder.

**Solução:** Implementar drag-and-drop com `sh.calvin.reorderable` ou `org.burnoutcrew.reorderable`. `HymnList.hymnIds` é `List<String>` — ordem persiste naturalmente.

**Critérios de aceite:**
- [ ] Long-press inicia drag
- [ ] Drop persiste nova ordem localmente
- [ ] Sync remoto dispara após reorder
- [ ] UI reflete ordem imediatamente

---

### FEAT-D03: Soft-delete de conta (anonimizar dados, não reter PII) [CRÍTICA]

**Arquivos:** `ui/screens/profile/ProfileScreen.kt`, `data/repository/FirestoreUserRepositoryImpl.kt`, dependências de Auth

**Decisão arquiteto:** Soft-delete. Conta desativada, não deletada. PII deve ser anonimizada.

**Solução:**
1. Deletar `FirebaseAuth.getCurrentUser()?.delete()`
2. No Firestore `users/{uid}`: setar `active: false`, null em `name`, `email`, `photoUrl`
3. Deletar subcoleções `favorites/` e `settings/` (PII indireta)
4. `hymnLists/`: manter registros, null em `creatorId` (se aplicável)
5. Limpar DataStore local (favorites, settings, hymn lists)
6. Remover `DeleteAccountDialog` e substituir por `DeactivateAccountDialog` com texto correto

**Critérios de aceite:**
- [ ] Conta Firebase Auth deletada (login impossível)
- [ ] Documento `users/{uid}` preservado com `active: false` e PII anonimizada
- [ ] Dados locais limpos
- [ ] Diálogo de confirmação requer digitar "desativar" (não "excluir")
- [ ] LGPD Art. 18, VI respeitado (PII eliminada, dados anonimizados retidos sob Art. 16, IV)
- [ ] Período de retenção documentado (ex: 180 dias para purga total)

---

### FEAT-D04: Estatísticas do perfil com valores reais [BAIXA]

**Arquivo:** `ui/screens/profile/ProfileScreen.kt`

**Problema:** Cards mostram `"0"` placeholder (TODO).

**Solução:** Injetar `FavoritesRepository` e `HymnListRepository` no `ProfileViewModel`. Calcular `favorites.size` e `hymnLists.size`.

**Critérios de aceite:**
- [ ] Card "Favoritos" mostra contagem real
- [ ] Card "Streak" pode permanecer "0" (funcionalidade futura)
- [ ] Valores atualizam em tempo real via Flow

---

### FEAT-D06: MVP sistema de retry para sincronização [ALTA]

**Arquivos:** `data/repository/BidirectionalSyncService.kt`, `data/repository/SyncWorker.kt`, `data/repository/DefaultFavoritesRepository.kt`

**Decisão revisão:** DQL completo não justificado. MVP com 0 novas dependências.

**Solução:**
1. Decompor `syncRemoteToLocal()` em métodos por operação (`syncFavoriteItem(id)`, `syncHymnListItem(id)`)
2. `SyncWorker`: ler `inputData` com `retry_count` e `max_retries` (5)
3. Usar `BackoffPolicy.EXPONENTIAL` do WorkManager (já configurado)
4. Ao esgotar tentativas: escrever ID em DataStore `failed_sync_items` + retornar `Result.success()` (não `Result.retry()`)
5. UI: ler DataStore em tela de configurações, exibir badge "N itens com falha na sincronização"

**Critérios de aceite:**
- [ ] Cada operação de sync tem seu próprio tracking de retry
- [ ] Após max_retries, registro persiste em DataStore
- [ ] UI exibe indicador de falhas sem bloquear o usuário
- [ ] Sem novas bibliotecas (Room, Proto)

---

### FEAT-D07: Classificação e propagação seletiva de erros de sync [ALTA]

**Arquivos:** `data/repository/DefaultFavoritesRepository.kt`, `data/repository/DataStoreHymnListRepository.kt`, `ui/screens/hymn/HymnDetailViewModel.kt`

**Decisão revisão:** Arquiteto vence em 3/4. Só mostrar erro se permanente + acionável.

**Solução:**
1. Classificar erros: transiente (timeout, rate-limit) → retry silencioso. Permanente não-acionável (schema) → `Log.e`. Permanente acionável (token expirado) → `ShowSnackbar`. Ação do usuário falha → Snackbar sem ação.
2. `DefaultFavoritesRepository`: propagar erro acionável via callback/event
3. `HymnDetailViewModel`: usar `eventFlow` existente (ShowSnackbar) para erros acionáveis

**Critérios de aceite:**
- [ ] Erros transientes não mostram UI
- [ ] Erros permanentes acionáveis mostram Snackbar com ação
- [ ] Erros de ação do usuário (addToList) mostram Snackbar sem ação
- [ ] `Log.e` apenas para permanentes não-acionáveis

---

### FEAT-D08: Remover placeholder de compartilhamento [BAIXA]

**Arquivo:** `ui/screens/hymn/HymnDetailScreen.kt` (linha 281)

**Decisão revisão:** Remover placeholder. GitHub APK link causa side-loading friction.

**Solução:** Remover linha `(Link para a loja em breve)` do texto de share. Na publicação da Play Store, adicionar URL real.

**Critérios de aceite:**
- [ ] Texto compartilhado sem placeholder
- [ ] Ação de compartilhar completa e limpa

---

### FEAT-D10: (Já coberto em U03) Criar lista na tab Listas [ALTA]

Duplicado com U03. Ver FEAT-U03.

---

### FEAT-D11: Substituir LazyColumn aninhado no AddToListSuggestionCard [MÉDIA]

**Arquivo:** `ui/screens/hymn/components/AddToListSuggestionCard.kt`

**Problema:** `LazyColumn` com altura fixa `200.dp` aninhado em `Column`. Scroll conflitante.

**Solução:** Substituir `LazyColumn` por `Column` com `Modifier.verticalScroll(rememberScrollState())` + `Modifier.heightIn(max = 200.dp)`.

**Critérios de aceite:**
- [ ] Scroll suave, sem conflito com scroll pai
- [ ] Itens truncados são scrolláveis dentro do card
- [ ] Altura máxima respeitada

---

### FEAT-D12: Mostrar nome do tema ativo no seletor [BAIXA]

**Arquivo:** `ui/components/TopAppBarComponents.kt`

**Problema:** Botão "Temas" só mostra ícone Brush. Usuário não sabe qual tema está ativo.

**Solução:** Adicionar `Text` ao lado do ícone com nome curto do tema atual. Ex: "Clássico" se `currentTheme == "default_light"`.

**Critérios de aceite:**
- [ ] Texto ao lado do ícone reflete tema ativo
- [ ] Dropdown permanece funcional

---

### FEAT-D13: SearchField com suporte a acessibilidade [MÉDIA]

**Arquivo:** `ui/screens/home/components/SearchField.kt`

**Problema:** Nenhum `contentDescription` para trailing icon.

**Solução:** Já coberto na FEAT-U01 (trailingIcon com `contentDescription = "Limpar busca"`).

**Critérios de aceite:**
- [ ] Leitores de tela anunciam "Limpar busca" quando X visível
- [ ] `leadingIcon` com `contentDescription` adequado

---

### FEAT-D14: (Vinculado a D03) Anonimizar dados no soft-delete [MÉDIA]

Mesmo que D03. Ver FEAT-D03.

---

### FEAT-D15: Diálogo de confirmação ao remover hino da lista [BAIXA]

**Arquivo:** `ui/screens/hymnlistdetail/HymnListDetailScreen.kt`

**Problema:** Remoção sem confirmação pode ser acidental.

**Solução:** `AlertDialog` "Remover hino desta lista?" com confirmar/cancelar. Opcional: Snackbar com "Desfazer" (5s).

**Critérios de aceite:**
- [ ] Diálogo antes de remover
- [ ] Snackbar undo (opcional, documentar trade-off)

---

### FEAT-D16: Ajustar threshold do zoom gestual [BAIXA]

**Arquivo:** `ui/screens/hymn/HymnDetailScreen.kt`

**Problema:** `zoomThreshold = 0.25f` sem feedback intermediário.

**Solução:** Reduzir para `0.15f`. Opcional: animação de escala momentânea no ícone de fonte.

**Critérios de aceite:**
- [ ] Zoom dispara com pinça mais leve
- [ ] Comportamento suave sem falso positivo

---

### FEAT-D18: Ícone de coração para desfavoritar na lista de favoritos [MÉDIA]

**Arquivo:** `ui/screens/favorites/FavoritesScreen.kt`, `FavoritesViewModel.kt`

**Decisão arquiteto:** Ícone de coração no card (não swipe).

**Solução:**
1. Adicionar `IconButton` com `Icons.Default.Favorite` (filled) como `trailingContent` no `HymnCardItem`
2. `FavoritesViewModel`: expor `toggleFavorite(hymnId: Int)` → `addFavorite(id.toString())` / `removeFavorite(id.toString())`
3. `contentDescription` dinâmico: "Remover dos favoritos" / "Adicionar aos favoritos"
4. `IconButton` consome clique nativamente — não conflita com `Card.onClick` (navegar)

**Critérios de aceite:**
- [ ] Ícone coração preenchido indica favoritado
- [ ] Tocar coração desfavorita sem navegar
- [ ] `Card.onClick` (navegar) ainda funciona
- [ ] Estado atualiza imediatamente (otimista)
- [ ] `FavoritesViewModel` tem método de toggle

---

### FEAT-D19: Remover dead code de tema do MainSharedViewModel [MÉDIA]

**Arquivo:** `ui/screens/main/MainSharedViewModel.kt`, `MainScreen.kt`

**Decisão revisão:** Não há bug real. Só remover dead code. Sem mudança de escopo.

**Solução:**
1. Remover `currentTheme: StateFlow<String>` do `MainSharedViewModel`
2. Remover `selectTheme()` do `MainSharedViewModel`
3. Manter `currentTheme` fluindo de `MainViewModel` (Activity) → `MainScreen` → `HomeScreen`
4. `MainSharedViewModel` retém apenas `userProfile` (único dado realmente compartilhado entre tabs)

**Critérios de aceite:**
- [ ] `MainSharedViewModel` não expõe `currentTheme`
- [ ] Tema continua funcional em todas as telas
- [ ] Nenhuma quebra visual
- [ ] Código removido, não comentado

---

## Itens Excluídos (Decisão do Arquiteto)

| Item | Razão |
|---|---|
| D05 - Aba Descubra placeholder | Open beta, manter como está |
| D09 - Hinos hardcoded em memória | Decisão arquitetural, não alterar |
| D17 - Cache offline de hinos | Não será implementado |
| D20 - FavoritesViewModel eager load | Não será implementado |

---

## Ordem de Implementação Sugerida

1. FEAT-U01 (1h)
2. FEAT-U02 (2-4h)
3. FEAT-D08 (0.5h)
4. FEAT-D12 (0.5h)
5. FEAT-D16 (1h)
6. FEAT-U03 (4h)
7. FEAT-D01 (2h) + FEAT-D15 (1h)
8. FEAT-D10 (já em U03)
9. FEAT-D18 (3h)
10. FEAT-D04 (1h)
11. FEAT-D11 (1h)
12. FEAT-D13 (já em U01)
13. FEAT-D03 (4-8h)
14. FEAT-D14 (já em D03)
15. FEAT-D19 (2h)
16. FEAT-D06 (4h) + FEAT-D07 (3h)
17. FEAT-D02 (4-6h)