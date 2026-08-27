# Architecture — Louve Mobile App

## Selected Architecture

**Clean Architecture (ui/domain/data) + MVVM + Unidirectional Data Flow (UDF)**

## Layer Diagram

```
┌─────────────────────────────────────────────┐
│  UI LAYER                                    │
│  Screens · Components · Navigation · Theme   │
│  ViewModels (StateFlow) · Hilt-injected      │
│  Jetpack Compose · Material 3                │
├─────────────────────────────────────────────┤
│  DOMAIN LAYER                                 │
│  Models · Repository Interfaces · Pure Kotlin │
│  No Android imports                           │
├─────────────────────────────────────────────┤
│  DATA LAYER                                   │
│  Repository Impl · DataSources · Services    │
│  Firebase Auth · Firestore · DataStore       │
│  Migration · Sync · Connectivity            │
└─────────────────────────────────────────────┘
```

## Key Decisions

1. **Clean Architecture** — Business logic (`domain`) isolated from platform details
2. **Repository Pattern** — All data access mediated by interfaces
3. **Hilt DI** — 100% dependency injection for testability
4. **StateFlow** — Reactive state management with UDF
5. **Firebase Suite** — Auth, Firestore, Analytics (locked-in but encapsulated in `data`)
6. **Hymn Data in Code** — 640 hymns compiled as Kotlin (not JSON) for startup performance
7. **Hybrid Favorites** — Local-first with cloud sync when authenticated
8. **Edge-to-Edge** — System bars adapt to theme for immersion

## Architecture Constraints

- `domain` layer MUST NOT depend on Android SDK or any framework
- `data` layer depends on `domain` only (inversion of control)
- `ui` layer depends on `domain` only (never directly on `data`)
- All dependencies injected via Hilt constructor injection
- No UseCase layer (business logic lives in ViewModels and repositories)