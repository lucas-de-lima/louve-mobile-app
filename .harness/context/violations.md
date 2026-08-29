# Architecture Violations Register — Louve App

## Context

The project follows Clean Architecture with `domain` layer required to be pure Kotlin (no Android imports). This document tracks all violations discovered during Phase 2 discovery.

## Active Violations

### #1: Domain imports UI type

| Field | Value |
|-------|-------|
| **File** | `domain/repository/AuthRepository.kt` |
| **Line** | 5 |
| **Import** | `com.lucasdelima.louveapp.ui.screens.settings.AuthUiState` |
| **Method** | `fun getAuthState(): Flow<AuthUiState>` |
| **Severity** | HIGH |
| **Impact** | Creates circular dependency: domain → ui → domain. Blocks KMP migration. |
| **Fix** | Move `AuthUiState` and `AuthError` sealed classes to `domain/model/` or define domain equivalents |

### #2: Data imports UI types

| Field | Value |
|-------|-------|
| **File** | `data/repository/FirebaseAuthRepositoryImpl.kt` |
| **Lines** | 14-15 |
| **Imports** | `AuthUiState`, `AuthError` from `ui.screens.settings` |
| **Severity** | HIGH |
| **Impact** | Data layer depends on UI layer. Violates Clean Architecture dependency rule. |
| **Fix** | Same as #1 — move types to domain |

### #3: Data imports theme from UI

| Field | Value |
|-------|-------|
| **File** | `data/repository/DataMigrationService.kt` |
| **Line** | 7-8 |
| **Import** | `com.lucasdelima.louveapp.ui.theme.DefaultTheme` |
| **Severity** | MEDIUM |
| **Impact** | Data layer depends on UI layer for a configuration constant |
| **Fix** | Extract default theme name to domain constant or inject via DI |

### #4: Data imports theme from UI (duplicate)

| Field | Value |
|-------|-------|
| **File** | `data/repository/BidirectionalSyncService.kt` |
| **Line** | 7-8 |
| **Import** | `com.lucasdelima.louveapp.ui.theme.DefaultTheme` |
| **Severity** | MEDIUM |
| **Fix** | Same as #3 |

### #5: ViewModel bypasses DI

| Field | Value |
|-------|-------|
| **File** | `ui/screens/home/HomeViewModel.kt` |
| **Line** | 38 |
| **Code** | `private val hymnRepository: HymnRepository = HymnRepositoryImpl()` |
| **Severity** | HIGH |
| **Impact** | Breaks testability (cannot mock repository). Not `@HiltViewModel` — lifecycle not managed by Hilt. |
| **Fix** | Annotate as `@HiltViewModel` and inject `HymnRepository` via constructor |

## Resolved Violations

None yet.

## Monitoring

These violations are tracked here for resolution during adoption phases. Resolution is **required** before KMP migration (Phase 6 of roadmap) and **recommended** before Phase 4b (Feature Implementation) of the Agentic Adoption Plan.