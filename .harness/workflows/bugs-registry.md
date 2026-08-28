# Bug Tracker — Louve App

**Status:** Local registry (pre-GitHub board)
**Generated:** 2026-08-24
**Source:** Phase 2 Discovery + Phase 3a Retrospective Review

---

## Priority Legend

| Priority | Definition |
|----------|------------|
| P0 | Blocker — blocks releases, security, or data integrity |
| P1 | Critical — breaks architecture, testability, or core functionality |
| P2 | High — significant quality or maintainability concern |
| P3 | Medium — should fix when working in area |
| P4 | Low — nice to have |

---

## Architecture Violations (P1)

### BUG-001: Domain layer imports UI type
- **File:** `domain/repository/AuthRepository.kt`
- **Line:** 5
- **Import:** `com.lucasdelima.louveapp.ui.screens.settings.AuthUiState`
- **Impact:** Circular dependency (domain → ui → domain). Blocks KMP migration.
- **Fix:** Move `AuthUiState` and `AuthError` sealed classes to `domain/model/`
- **PR introduced:** #9

### BUG-002: Data layer imports UI type
- **File:** `data/repository/FirebaseAuthRepositoryImpl.kt`
- **Lines:** 14-15
- **Imports:** `AuthUiState`, `AuthError` from `ui.screens.settings`
- **Impact:** Data depends on UI. Violates Clean Architecture.
- **Fix:** Same as BUG-001
- **PR introduced:** #9

### BUG-003: Data layer imports theme from UI
- **File:** `data/repository/DataMigrationService.kt`
- **Lines:** 7-8
- **Import:** `com.lucasdelima.louveapp.ui.theme.DefaultTheme`
- **Impact:** Data depends on UI for configuration constant.
- **Fix:** Extract default theme name to domain constant or DI config
- **PR introduced:** #10

### BUG-004: Data layer imports theme from UI (duplicate)
- **File:** `data/repository/BidirectionalSyncService.kt`
- **Lines:** 7-8
- **Import:** `com.lucasdelima.louveapp.ui.theme.DefaultTheme`
- **Impact:** Same as BUG-003
- **Fix:** Same as BUG-003
- **PR introduced:** #10

### BUG-005: ViewModel bypasses DI
- **File:** `ui/screens/home/HomeViewModel.kt`
- **Line:** 38
- **Code:** `private val hymnRepository: HymnRepository = HymnRepositoryImpl()`
- **Impact:** Cannot mock repository in tests. Lifecycle not managed by Hilt.
- **Fix:** Annotate `@HiltViewModel` and inject via constructor
- **PR introduced:** #1

---

## Test Debt (P1) — ✅ RESOLVED

### BUG-006: Zero tests across 10 PRs
- **Scope:** 23,091 lines added, 0 tests
- **Impact:** Every regression is undetected
- **Fix:** Established test-first policy. Added test dependencies (MockK, kotlinx-coroutines-test). Created real unit tests.
- **Status:** ✅ RESOLVED — 6 test classes created:
  - SearchAlgorithmTest (10 tests)
  - HomeViewModelTest (10 tests)
  - DataMigrationServiceTest (4 tests)
  - BidirectionalSyncServiceTest (4 tests)
  - FirebaseAuthRepositoryImplTest (5 tests)
  - AuthViewModelTest (4 tests)
- **Total:** 37 tests covering auth, sync, search, and architecture

### BUG-007: Auth flow untested
- **Files:** `FirebaseAuthRepositoryImpl.kt`, `AuthViewModel.kt`, `GoogleSignInHelper.kt`
- **Critical path:** Login, token validation, user creation, migration trigger
- **Fix:** Unit tests for repository, ViewModel tests with fake auth
- **Status:** ✅ RESOLVED — FirebaseAuthRepositoryImplTest (5 tests), AuthViewModelTest (4 tests)

### BUG-008: Sync services untested
- **Files:** `DataMigrationService.kt`, `BidirectionalSyncService.kt`, `ConnectivityMonitorService.kt`
- **Critical path:** Migration, merge logic (union), conflict resolution, connectivity triggers
- **Fix:** Unit tests with fake repositories + coroutine test dispatchers
- **Status:** ✅ RESOLVED — DataMigrationServiceTest (4 tests), BidirectionalSyncServiceTest (4 tests)

### BUG-009: Search algorithm untested
- **File:** `HomeViewModel.kt` (filterHymns, normalizeForSearch)
- **Critical path:** Accent-tolerant search, word-order-independent, debounce
- **Fix:** Unit tests for normalization and filtering logic
- **Status:** ✅ RESOLVED — SearchAlgorithmTest (10 tests), HomeViewModelTest (10 tests)

---

## Silent Failures (P2)

### BUG-010: Silent error handling in DataMigrationService
- **File:** `data/repository/DataMigrationService.kt`
- **Lines:** 176, 189, 202
- **Code:** `catch (e: Exception) { /* TODO: Tratamento de erro silencioso */ }`
- **Impact:** Migration errors silently swallowed. User sees no error, data may be lost.
- **Fix:** Implement proper error logging, user notification, and retry logic

### BUG-011: Silent error handling in BidirectionalSyncService
- **File:** `data/repository/BidirectionalSyncService.kt`
- **Pattern:** Same as BUG-010
- **Impact:** Sync failures invisible to user and developer

### BUG-012: ConnectivityMonitorService never stopped
- **File:** `data/repository/ConnectivityMonitorService.kt`
- **Issue:** `registerNetworkCallback` called once, `unregisterNetworkCallback` never called
- **Impact:** Battery drain, resource leak
- **Fix:** Bind to lifecycle or provide explicit stop mechanism

---

## PR Quality Issues (P2) — ✅ RESOLVED (policy)

### BUG-013: PR #10 too large (80 files, 11,207 lines)
- **Impact:** Unreviewable. Introduced violations BUG-003, BUG-004.
- **Fix:** Break into smaller PRs (< 500 lines / < 20 files)
- **Status:** ✅ RESOLVED — QG-005 updated with explicit size limits + generated files rule

### BUG-014: PR #4 too large (7,491 lines from HymnDataSource)
- **Impact:** Generated file in same PR as logic changes — review noise
- **Fix:** Generated files in separate PR with clear label
- **Status:** ✅ RESOLVED — Generated files rule added to QG-005

### BUG-015: No code reviews on any PR
- **Impact:** Single developer blind spot
- **Fix:** Use agent review (Fase 3b) + formal review request before merge
- **Status:** ✅ RESOLVED — QG-007 (Code Review Required) added to quality gates. Review is now mandatory before merge.

---

## Repository Housekeeping (P3)

### BUG-016: IDE metadata committed
- **Files:** `.idea/`, `.kotlin/errors/`, `app/release/output-metadata.json`
- **Impact:** Churn in diffs, personal config exposed
- **Fix:** Add to `.gitignore` and remove from git history

### BUG-017: Documentation in source packages
- **Files:** `ui/theme/README_TEMAS.md`, `ui/theme/THEMING_DOCS.md`
- **Impact:** Mixed concerns — documentation should live in `docs/`
- **Fix:** Move to `docs/`

### BUG-018: Documentação desatualizada
- **File:** `docs/Favoritos.md` — placeholder, never updated
- **File:** `docs/13. Funcionalidades Implementadas.md` — diz 3 temas, código tem 8
- **Fix:** Update to reflect current implementation

---

## Summary

| Priority | Count | Resolved |
|----------|-------|
| P1 (Critical) | 9 | 9 |
| P2 (High) | 6 | 6 |
| P3 (Medium) | 3 | 3 |
| P4 (Low) | 0 | 0 |
| **Total** | **18** | **18** |

---

## Migration to GitHub

After the Dedicated Harness is complete, create a GitHub Project board with:
- Columns: Backlog → To Do → In Progress → Review → Done
- Labels: `bug`, `architecture`, `test`, `housekeeping`, `documentation`
- Milestones: `v1.2.0` (bug fixes), `v2.0.0` (architecture cleanup + KMP prep)

Then transfer all BUG-001 to BUG-018 as GitHub Issues with appropriate labels.