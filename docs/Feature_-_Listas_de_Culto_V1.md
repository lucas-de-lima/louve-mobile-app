# Feature: Listas de Culto (V1)

## Visão Geral
- Objetivo: permitir que usuários organizem hinos para eventos/cultos específicos em **listas efêmeras** com expiração automática.
- Princípios: offline-first, UX simples, isolamento do favorito permanente, DX segura e incremental.
- Escopo V1: armazenamento **local-only** (Proto DataStore), sem sync com Firestore; limpeza automática com WorkManager.

## Motivação e Problema
Usuários precisam marcar hinos importantes para um culto/ensaio, sem confundí-los com favoritos permanentes. Uma lista temporária por evento dá contexto e evita poluir a lista de favoritos.

## Terminologia
- Lista de Culto (Evento): conjunto nomeado de hinos com data/hora de expiração.
- Expiração: instante em que a lista deixa de ser considerada ativa e pode ser removida.

## UX/Fluxos
### HymnDetailScreen
- Um único botão: "Adicionar a..." (substitui o toggle direto de favorito).
- Abre BottomSheet/Diálogo:
  - Meus Favoritos (permanente) – usa fluxo atual
  - Listas de Culto existentes (ativas)
  - Criar nova Lista de Culto...
- Ao escolher uma lista, o hino é adicionado (idempotente). Ao criar nova lista, sugerir nome e expiração padrão (amanhã 04:00) com opção de editar.

### FavoritesScreen
- Abas: "Permanentes" e "Listas de Culto".
- Abas independentes (não misturar conteúdos).
- Listas de Culto: cards com nome, expiração, contagem de hinos e alguns títulos (preview). Ações por lista: Renomear, Alterar Expiração, Limpar, Remover lista.
- Dentro de uma lista: exibir hinos, permitir remover, ordenar e compartilhar (compartilhar opcional pós-V1).

### Estados e Mensagens
- Badge "expira em Xh"/"expira amanhã" nos cards.
- Expiradas não aparecem na UI (filtradas). A limpeza efetiva é feita por Worker.

## Regras de Negócio
- Expiração padrão: amanhã às 04:00 (configurável futuramente).
- Re-adicionar hino na mesma lista renova/atualiza o registro (idempotência por (listaId, hymnId)).
- Hino pode existir em múltiplas listas e também pode ser favorito permanente.
- Limites: até 100 listas e 500 hinos por lista (soft caps; validar em runtime e orientar usuário).

## Modelo de Dados (Local)
- Armazenamento: **Proto DataStore** dedicado.
- Schema (conceitual):
  - EventList {
    - id: String (UUID)
    - name: String
    - expiresAtEpochMs: int64 (UTC)
    - hymnIds: repeated string
  }
  - Root {
    - lists: repeated EventList
    - lastModifiedEpochMs: int64
  }

## Contratos (Domain)
- `TemporaryFavoritesRepository` (nome técnico mantido para clareza com o brainstorm; pode ser `EventListsRepository`):
  - Flows:
    - getActiveLists(): Flow<List<EventList>>
    - getAllLists(): Flow<List<EventList>>
    - getActiveHymnIds(): Flow<Set<String>> (todos os hinos de todas as listas ativas)
  - Comandos:
    - createList(name: String, expiresAt: Long): Result<String> // retorna id
    - renameList(id: String, name: String): Result<Unit>
    - rescheduleList(id: String, expiresAt: Long): Result<Unit>
    - deleteList(id: String): Result<Unit>
    - addHymn(id: String, hymnId: String): Result<Unit>
    - removeHymn(id: String, hymnId: String): Result<Unit>
    - clearExpired(): Result<Unit>
    - clearAll(): Result<Unit>

## Implementação (Data)
- `DataStoreTemporaryFavoritesRepository` (ou `DataStoreEventListsRepository`):
  - Proto DataStore com `updateData { ... }` atômico.
  - Filtros de expiração aplicados nos Flows de leitura.
  - Deduplicação por par (listaId, hymnId).
  - Validação de limites (listas e itens) com erros amigáveis.

## Limpeza (WorkManager)
- `TempFavoritesCleanupWorker`:
  - PeriodicWorkRequest: 24h (flex 6h), sem constraints pesadas.
  - Ação: remover listas expiradas do DataStore.
- Agendamento: uma vez durante inicialização do app (ex.: `MainActivity` ou `SplashScreen`), idempotente.
- Garantia de UX: mesmo sem rodar o worker (ex.: bateria restrita), a UI filtra expiradas.

## Integração com o Sistema Atual
- Favoritos permanentes: inalterados.
- `HymnDetailViewModel`: substituir o toggle de favorito por ação "Adicionar a..." que delega para favorito permanente ou listas de culto.
- `FavoritesViewModel`/`FavoritesScreen`: adicionar aba de Listas de Culto consumindo `TemporaryFavoritesRepository`.
- Analytics: adicionar eventos `event_list_create`, `event_list_add_hymn`, `event_list_remove_hymn`, `event_list_cleanup`.

## Impacto em Arquivos
- Criar (Domain):
  - `domain/model/EventList.kt`
  - `domain/repository/TemporaryFavoritesRepository.kt` (ou `EventListsRepository.kt`)
- Criar (Data):
  - `data/repository/DataStoreTemporaryFavoritesRepository.kt`
  - `main/proto/event_lists.proto`
  - `data/work/TempFavoritesCleanupWorker.kt`
- Alterar (DI):
  - `di/RepositoryModule.kt` (bind do novo repo)
  - `di/AppModule.kt` (provider do Proto DataStore, WorkerFactory se necessário)
- Alterar (UI/Presentation):
  - `ui/screens/hymn/HymnDetailViewModel.kt` e `HymnDetailScreen.kt` (ação unificada)
  - `ui/screens/favorites/FavoritesViewModel.kt` e `FavoritesScreen.kt` (abas)
  - `ui/screens/favorites/EventListsScreen.kt` (lista de listas; pode ser composable novo)
  - `ui/components/SelectDestinationBottomSheet.kt` (diálogo/bottom-sheet de destino)
- Alterar (Inicialização):
  - `ui/screens/splash/SplashScreen.kt` ou `MainActivity` para agendar o worker
- Alterar (Analytics):
  - `data/analytics/FirebaseAnalyticsService.kt`

## Telemetria
- Eventos e parâmetros (sem PII):
  - `event_list_create` { name, expiresInHours }
  - `event_list_add_hymn` { listId }
  - `event_list_remove_hymn` { listId }
  - `event_list_cleanup` { removedListsCount }

## Critérios de Aceitação
- Criar lista com nome e expiração padrão; renomear e reprogramar expiração.
- Adicionar/remover hinos em lista, offline.
- Listas expiradas não aparecem na UI; worker remove dados expirados.
- Favoritos permanentes inalterados, UX com ação unificada de destino.
- Performance: operações instantâneas; sem travar UI; DataStore estável.

## Plano de Testes
- Unidade (Data): create/rename/reschedule/delete/add/remove/clearExpired; limites; idempotência.
- Unidade (Domain): validações de entrada e erros de negócio.
- Integração (UI): fluxo da bottom-sheet, abas de favoritos, filtros de expiração.
- Worker: teste de execução e remoção de expirados (usando TestDriver do WorkManager).
- E2E manual: cenários com mudança de data/horário e sem conectividade.

## Roadmap Futuro (V2+)
- Sync opcional por usuário logado com Firestore `users/{uid}/events/{eventId}`.
- Compartilhamento de lista (deep link/intent).
- Templates de culto, ordenação personalizada por lista, notas por hino.

## Riscos e Mitigações
- Confusão de UI: ação unificada + explicação curta no diálogo.
- Timezone/clock drift: armazenar UTC; renderizar com timezone do device.
- Crescimento de dados: limites + worker periódico.

---
Este documento deve ser atualizado conforme decisões de design/implementação evoluírem durante o desenvolvimento da feature.
