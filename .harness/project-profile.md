# Project Profile — Louve Mobile App

## Identity

- **Name:** Louve App
- **Repository:** github.com/lucas-de-lima/louve-mobile-app
- **Owner/Maintainer:** Lucas de Lima
- **Status:** PRODUÇÃO — v1.1.0 (released on Google Play)

## Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin 2.0.0 |
| UI | Jetpack Compose + Material 3 |
| Architecture | Clean Architecture (ui/domain/data), MVVM, UDF |
| DI | Hilt 2.51.1 (100% coverage) |
| Async | Coroutines + StateFlow |
| Navigation | Navigation Compose 2.9.0 |
| Auth | Firebase Auth + Google Sign-In |
| Database | Cloud Firestore (remote), DataStore Preferences (local) |
| Analytics | Firebase Analytics |
| Images | Coil 2.6.0 |
| Build | Gradle KTS, AGP 8.13.0, KSP |
| Min SDK | 24 |
| Target SDK | 35 |
| Compile SDK | 35 |

## Source Structure

```
app/src/main/java/com/lucasdelima/louveapp/
├── data/           # 9 repository implementations, data sources, services
├── domain/         # 7 interfaces, 4 models (pure Kotlin)
├── ui/             # 10+ screens, navigation, theme, components
├── di/             # Hilt DI modules
└── LouveApp.kt, MainActivity.kt, MainViewModel.kt
```

## Features (implemented)

- 640 hymns with smart search (accent-tolerant, word-order-independent)
- Hybrid favorites system (local DataStore + remote Firestore)
- Google Authentication with automatic data migration
- Dynamic theme system (3 themes with contextual backgrounds)
- Edge-to-edge display with adaptive system bar colors
- Cinematic splash screen
- Analytics integration
- Bidirectional sync and connectivity monitoring

## Development Workflow

- **Branching:** Git Flow — `main` (production), `develop` (integration), `feat/*`, `fix/*`
- **Commits:** Conventional Commits (`feat:`, `fix:`, `refactor:`, `docs:`, `chore:`)
- **PR workflow:** feature branch → PR → review → merge to develop → release to main
- **CI:** GitHub Action on tag push `v*` (creates GitHub Release only)
- **Build:** Manual (Android Studio or `./gradlew assembleRelease`)
- **Release:** Manual — build APK/AAB, sign with keystore, upload to Google Play Console

## Testing Status

- **Unit tests:** 1 example test only (`ExampleUnitTest`)
- **Instrumentation tests:** 1 example test only (`ExampleInstrumentedTest`)
- **Test infrastructure:** Available (JUnit, Espresso, Compose UI Test)
- **Coverage:** Not measured
- **Linting:** Not automated in CI