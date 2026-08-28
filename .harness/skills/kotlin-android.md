# Skills — Louve App

## kotlin-android

Android/Kotlin implementation skill for the Louve App project. Covers the full Android development lifecycle using the project's specific technology stack.

### Technology Stack

- **Build system:** Gradle KTS (Kotlin DSL)
- **DI pattern:** Hilt (Dagger-based, constructor injection)
- **Concurrency:** Coroutines + Flow (structured concurrency)
- **UI framework:** Jetpack Compose (declarative, Material 3)
- **Navigation:** Navigation Compose (type-safe routes)
- **Testing:** JUnit 4, Espresso, Compose UI Test
- **Architecture:** Clean Architecture (ui/domain/data) + MVVM + UDF

## compose-ui

### Guidelines

- Use Material 3 components
- Follow `Scaffold(containerColor = Color.Transparent)` pattern for theme backgrounds
- Use `StateFlow` + `collectAsState()` for reactive state
- Use `remember` and `derivedStateOf` for derived state
- Keep Composables stateless when possible; hoist state to ViewModel
- Use `BottomNavItem` sealed class for navigation items

## android-testing

### Tools

- JUnit 4 for unit tests
- Compose UI Test for screen tests
- Hilt Testing for DI in tests

### Patterns

- Test ViewModels with fake repositories
- Test UI with compose test rules
- Prefer `runTest` + `TestDispatcher` for coroutine testing