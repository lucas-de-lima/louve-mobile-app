# Favoritos - Sistema de Favoritos Híbrido

## Visão Geral
Sistema de favoritos totalmente implementado com persistência local (DataStore) e sincronização na nuvem (Firestore). Utiliza uma arquitetura mediadora que decide automaticamente entre armazenamento local e remoto baseado no estado de login do usuário.

## Funcionalidades Implementadas
- **Favoritar/Desfavoritar hinos** na tela de detalhes com feedback visual
- **Lista de favoritos** com estado vazio personalizado na tela FavoritesScreen
- **Persistência local** via DataStore para usuários não logados
- **Sincronização automática** para usuários logados via Firestore
- **Observação em tempo real** via StateFlow
- **Migração de dados** ao fazer login (DataMigrationService)
- **Merge inteligente** que preserva todos os favoritos durante sincronização

## Arquitetura
- `DefaultFavoritesRepository` — Repositório mediador (decide entre local/remoto)
- `DataStoreLocalFavoritesRepository` — Armazenamento local
- `FirestoreUserRepositoryImpl` — Armazenamento remoto (Firestore)
- `DataMigrationService` — Migração de dados locais para nuvem após login
- `BidirectionalSyncService` — Sincronização bidirecional e resolução de conflitos

## Status
- **Implementado:** Junho/Julho 2025
- **Testes:** Pendentes (ver BUG-007)