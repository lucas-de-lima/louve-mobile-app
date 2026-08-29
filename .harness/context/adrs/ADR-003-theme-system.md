# ADR-003: Theme Architecture

## Status

Accepted (implemented)

## Context

The app must support multiple visual themes with immersive backgrounds, edge-to-edge rendering, and runtime theme switching without Activity restart. Initial implementation had 3 themes; current version has 8.

Key requirements: (a) backgrounds must extend under system bars, (b) each theme has distinct colors and optional custom backgrounds, (c) theme selection persists across sessions, (d) system bar colors adapt to theme luminance.

## Decision

Implement a centralized theme architecture:
- `LouveThemeData` data class holding `id`, `name`, `category`, `MaterialTheme` colors, `Typography`, `LouveBackgrounds` (two composable lambdas: `screenBackground` + `detailScreenBackground`)
- `LouveAppTheme` composable applies `MaterialTheme` + `CompositionLocalProvider` for `LouveThemeData`
- `LouveTheme` static accessor via `staticCompositionLocalOf`
- Background rendered once in `MainActivity.Box` (not per-screen)
- Screens use `Scaffold(containerColor = Color.Transparent)` — the "glass sheet" effect
- `enableEdgeToEdge()` + `WindowCompat.getInsetsController` for system bar adaptation
- `SideEffect` in `LouveAppTheme` updates status/navigation bar appearance

## Simplest Viable Architecture

Single light theme, no runtime switching, no custom backgrounds.

## Alternatives Considered

### Per-screen background rendering

Rejected due to bug: gradients were truncated, themes didn't fill screen, caused visual artifacts between navigation transitions.

### Multiple Activity themes (themes.xml)

Rejected because Activity restart required for theme change — breaks smooth UX.

## Consequences

### Positive

- Runtime theme switching without Activity restart
- Edge-to-edge rendering consistent across all screens
- Centralized background avoids rendering duplication
- `CompositionLocal` provides ergonomic access from any composable

### Negative

- `containerColor = Color.Transparent` in every Scaffold — easy to forget
- Background composable lambdas stored as `@Composable () -> Unit` — cannot be serialized
- `CompositionLocal` makes theme dependency implicit (harder to reason about in complex compositions)

### Operational

- New themes require: data class instance + colors + backgrounds + registration in `AllThemes` list
- Background lambdas are not serializable — cannot save which background is active

### Testing

- Screens cannot be tested in isolation without `LouveAppTheme` wrapper
- Background composables are hard to snapshot-test

## Future Evolution Triggers

- Need for user-created themes
- Theme export/import between devices
- Animated backgrounds
- Material You dynamic color support

## Risks

- Container colors (`primaryContainer`, `secondaryContainer`, `tertiaryContainer`) are required for Material 3 component consistency — missing them caused visual bugs on hymn numbers
- Background composable lambdas stored in data class prevent serialization/Parcelable
- `SideEffect` for system bars may race with Activity lifecycle

## Evidence

- `ui/theme/LouveThemeData.kt`
- `ui/theme/Theme.kt`
- `ui/theme/AppThemes.kt`
- `ui/theme/Color.kt`
- `docs/4. Guia de UI e Theming - NOVA.MD`
- `docs/4. Guia de UI e Theming - ANTIGA.MD`