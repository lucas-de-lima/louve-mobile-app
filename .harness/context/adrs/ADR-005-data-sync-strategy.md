# ADR-005: Data Sync Strategy

## Status

Accepted (implemented)

## Context

Users may have data (favorites, theme preferences) on multiple devices. Data originates locally (DataStore) when offline/unauthenticated and must migrate to cloud upon login. After migration, changes must sync bidirectionally between devices.

Core requirement: never lose user data.

## Decision

Implement a three-service sync pipeline:

1. **ConnectivityMonitorService** — Android `ConnectivityManager.NetworkCallback` detects `onAvailable`/`onLost` events. Triggers sync when connectivity is restored for logged-in users.
2. **DataMigrationService** — On login: backup local data → fetch cloud data → intelligent merge → save to cloud → clear local backup.
3. **BidirectionalSyncService** — Periodic sync of favorites and settings between local and cloud.

**Merge strategies:**
- Favorites: `local.union(remote)` — always preserves superset
- Theme: if local is default → prefer remote; if remote is default → prefer local; if both custom and conflicting → prefer local

## Simplest Viable Architecture

Local-only storage. No sync. Data lost on device change.

## Alternatives Considered

### Cloud-only storage

Rejected because app must work offline without login.

### Last-writer-wins (LWW) without merge

Rejected — would lose data on concurrent modifications.

### Manual sync triggered by user

Rejected — contradicts "excelência na experiência principal" pillar.

## Consequences

### Positive

- Zero data loss on migration (backup-before-migration)
- Union merge never loses favorites
- Automatic sync on connectivity restoration
- Sync logic isolated in three specialized services

### Negative

- Full-data sync (not incremental) — bandwidth waste
- No user-facing sync status or progress indicator
- Silent error handling (TODO comments in catch blocks)
- No WorkManager integration for periodic background sync
- Conflict resolution logged but no user notification

### Operational

- Firestore costs increase with sync frequency
- No sync retry policy documented
- `ConnectivityMonitorService` registered app-wide — never stops monitoring

### Testing

- Sync requires network conditions (offline/online/flaky)
- Conflict scenarios need careful test case design
- Migration must be tested with empty/partial/full local + cloud states

## Known Technical Debt

- Silent catch blocks with TODO markers in `DataMigrationService`
- No incremental sync implementation
- No compression for synced payloads
- No WorkManager scheduled sync (planned for 3-6 months)

## Risks

- ConnectivityMonitorService never stopped — battery impact
- Full re-sync wastes mobile data on large favorite sets
- Silent error handling masks sync failures from both user and developer

## Evidence

- `data/repository/ConnectivityMonitorService.kt`
- `data/repository/DataMigrationService.kt`
- `data/repository/BidirectionalSyncService.kt`
- `docs/11. Sistema de Migração e Sincronização de Dados.md`