# ADR-006: Clean Architecture with Domain Layer Purity

## Status

Accepted (implemented, with violations)

## Context

The app follows Clean Architecture with three layers: `ui` (Jetpack Compose), `domain` (pure Kotlin), `data` (Firebase, DataStore). The domain layer must have zero Android SDK or framework dependencies to enable future KMP (Kotlin Multiplatform) sharing.

## Decision

Define strict layer boundaries:
- `domain` — pure Kotlin models and repository interfaces only. No `import android.*`. No framework annotations.
- `data` — implements domain interfaces. Depends on `domain` only. Contains Firebase, DataStore, platform code.
- `ui` — depends on `domain` only. Never accesses `data` directly. Uses ViewModels as mediators.

Dependency injection via Hilt `@Binds` maps interfaces to implementations at the DI boundary.

## Simplest Viable Architecture

Single-layer app without separation (rejected — would prevent KMP migration).

## Alternatives Considered

### No layer separation

Rejected — blocks KMP future and reduces testability.

### UseCase layer

Not adopted — intentional simplification. Business logic lives in ViewModels and repositories directly.

## Consequences

### Positive

- Domain layer is KMP-ready (sharable with iOS)
- Repository interfaces enable test doubles
- Layer separation forces explicit data contracts

### Negative

- **Active violations found:**
  1. `AuthRepository.kt` (domain) imports `AuthUiState` from `ui.screens.settings` — circular dependency: domain → ui → domain
  2. `FirebaseAuthRepositoryImpl.kt` (data) imports `AuthUiState` and `AuthError` from UI layer — data should not depend on UI
  3. `DataMigrationService.kt` and `BidirectionalSyncService.kt` (data) import `DefaultTheme` from `ui.theme` — data should not depend on UI
  4. `HomeViewModel.kt` bypasses Hilt DI by directly creating `HymnRepositoryImpl()` — breaks testability

### Operational

- Automated architecture compliance check needed (Phase 5 of adoption plan)
- Violations must be resolved before KMP migration

### Testing

- Domain layer is trivially testable (pure Kotlin)
- Data layer can be tested with fake Firebase instances
- UI layer requires Compose test rules

## Violations Register

| # | Location | Violation | Severity | Fix |
|---|----------|-----------|----------|-----|
| 1 | `domain/repository/AuthRepository.kt:5` | Imports `AuthUiState` from UI | HIGH | Move `AuthUiState`/`AuthError` to domain |
| 2 | `data/repository/FirebaseAuthRepositoryImpl.kt:14-15` | Imports `AuthUiState`/`AuthError` from UI | HIGH | Move types to domain or define domain equivalents |
| 3 | `data/repository/DataMigrationService.kt:7-8` | Imports `DefaultTheme` from UI | MEDIUM | Extract default theme name to domain constant or DI config |
| 4 | `data/repository/BidirectionalSyncService.kt:7-8` | Imports `DefaultTheme` from UI | MEDIUM | Same as #3 |
| 5 | `ui/screens/home/HomeViewModel.kt:38` | Direct instantiation `HymnRepositoryImpl()` | HIGH | Inject via Hilt constructor |

## Future Evolution Triggers

- KMP migration begins — all violations must be resolved first
- AGENTIC code generation — must enforce architecture compliance automatically

## Evidence

- All files listed in violations register above
- `docs/3. Arquitetura de Software.MD`
- `.harness/policies/project-policies.md` (architecture rules)