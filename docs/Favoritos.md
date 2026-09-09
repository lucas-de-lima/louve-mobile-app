# Favoritos - Sistema de Favoritos Híbrido

## Visão Geral
Sistema de favoritos com persistência local (DataStore) e tentativa de sincronização na nuvem (Firestore). Utiliza uma arquitetura mediadora que delega ao DataStore sempre e tenta sincronizar com Firestore de forma oportunista.

## Funcionalidades Implementadas
- **Favoritar/Desfavoritar hinos** na tela de detalhes com feedback visual
- **Lista de favoritos** com estado vazio personalizado na tela FavoritesScreen
- **Persistência local** via DataStore para usuários não logados
- **Sincronização oportunista** para usuários logados via Firestore (tentativa silenciosa em cada add/remove)
- **Observação em tempo real** via StateFlow
- **Migração de dados** ao fazer login (DataMigrationService)

## Arquitetura
- `DefaultFavoritesRepository` — Repositório mediador (sempre persiste localmente + tenta remoto)
- `DataStoreLocalFavoritesRepository` — Armazenamento local
- `FirestoreUserRepositoryImpl` — Armazenamento remoto (Firestore)
- `DataMigrationService` — Migração de dados locais para nuvem após login
- `BidirectionalSyncService` — Código completo de sincronização bidirecional, **não utilizado em produção** (código órfão)

## Limitações Conhecidas
- `syncWhenOnline()`, `checkForConflicts()` e `resolveConflicts()` em `DefaultFavoritesRepository` são stubs (retornam sucesso sem executar)
- `BidirectionalSyncService` possui lógica completa mas nunca é instanciado ou chamado
- `ConnectivityMonitorService` chama métodos stub ao reconectar
- Apenas **54 hinos** implementados em `HymnDataSource` (a documentação menciona 640)

## Status
- **Implementado:** Junho/Julho 2025 (parcial — sync stubs desde a implementação inicial)
- **Testes:** Pendentes (ver BUG-007, Epic 8)