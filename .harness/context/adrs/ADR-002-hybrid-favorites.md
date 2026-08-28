# ADR-002: Hybrid Favorites System

## Status

Accepted (implemented)

## Context

The app must work immediately without login (offline-first) while supporting cloud sync for authenticated users. Favorites are a core feature that must be available in both modes with seamless transition.

Two challenges: (a) data loss when local user logs in (local favorites must migrate), (b) conflict resolution when the same user has favorites on multiple devices.

## Decision

Implement a two-tier architecture:
- **Local:** `DataStoreLocalFavoritesRepository` (Jetpack DataStore, `stringSetPreferencesKey`)
- **Remote:** `UserRepository` (Firestore, `/users/{userId}/favorites/hymns/ids`)
- **Mediator:** `DefaultFavoritesRepository` routes reads to local always, writes to appropriate source based on auth state

Migration: `DataMigrationService` copies local data to cloud after login with backup-before-migration pattern.
Sync: `BidirectionalSyncService` uses union merge (`local.union(remote)`) — never loses data.

## Simplest Viable Architecture

Single local-only favorites with DataStore. No cloud sync.

## Alternatives Considered

### Cloud-only favorites

Rejected because app must work offline without login.

### Local-only favorites

Rejected because favorites would be lost on device change.

### Firebase Realtime Database

Not considered — Firestore already in stack.

## Consequences

### Positive

- Works offline without login
- Zero data loss on login (migration preserves all)
- Union merge prevents data loss between devices
- Automatic sync when connectivity restored

### Negative

- Two data sources to maintain and test
- Migration service adds complexity to login flow
- Sync is not incremental (syncs all data, not deltas)
- No user-facing sync status indicator

### Operational

- Firestore read/write costs scale with favorites usage
- DataStore schema changes require migration logic

### Testing

- Mediator pattern requires testing local-only, remote-only, and hybrid states
- Migration must be tested with empty, partial, and conflicting data

## Risks

- Silent error handling in `DataMigrationService` (TODO comments for error handling)
- No incremental sync — bandwidth waste on large datasets
- No WorkManager for periodic background sync (planned for 3-6 months)

## Evidence

- `data/repository/DefaultFavoritesRepository.kt`
- `data/repository/DataStoreLocalFavoritesRepository.kt`
- `data/repository/BidirectionalSyncService.kt`
- `data/repository/DataMigrationService.kt`
- `docs/10. Sistema de Autenticação e Persistência de Dados.MD`
- `docs/11. Sistema de Migração e Sincronização de Dados.md`