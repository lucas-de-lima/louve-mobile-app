# ADR-004: Navigation Architecture

## Status

Accepted (implemented)

## Context

The app has 10+ screens organized as: splash → main (with bottom navigation containing 4 tabs) → detail/auxiliary screens. The navigation system must support deep linking, backstack management, and edge-to-edge rendering.

Key decisions: (a) single Activity host, (b) Navigation Compose, (c) nested NavHost for bottom nav tabs, (d) dynamic icons (filled vs outlined) instead of Material 3 indicator colors.

## Decision

Architecture:
- Single `MainActivity` as Compose host
- `NavGraph` composable defines all routes as string constants
- `MAIN` route hosts a `MainScreen` with its own `NavHost` for bottom tabs
- Bottom navigation with 4 tabs: Harpa, Favorites, Discover, More
- Icons: filled when tab is active, outlined when inactive (instead of Material 3 color indicator)
- Bar height: 84dp, transparent, positioned absolutely
- Splash screen removed from backstack after navigation (`popUpTo(SPLASH) { inclusive = true }`)
- Auxiliary screens (Settings, Profile, About, Support) navigate outside the bottom nav scope

## Simplest Viable Architecture

Single-level navigation without bottom tabs. No nested NavHost.

## Alternatives Considered

### Material 3 NavigationBar with color indicator

Rejected in favor of custom `LouveBottomNavBar` with dynamic filled/outlined icons for cleaner appearance.

### Multi-Activity navigation

Rejected — Navigation Compose + single Activity is the modern Android standard.

### No nested NavHost (flat routes)

Rejected — bottom tab state would be lost on tab switch without nested NavHost.

## Consequences

### Positive

- Clean separation between main navigation and auxiliary screens
- Bottom tab state preserved across tab switches (nested NavHost)
- Professional appearance with dynamic icons
- Splash screen not reachable via back button

### Negative

- Two NavHost instances to manage (root + bottom nav)
- Bottom nav route changes require touching `BottomNavItem`, `NavGraph`, and `MainScreen`
- HymnDetail route inside bottom nav scope requires passing `rootNavController`

### Operational

- Adding a new bottom tab requires 3 file changes
- Route constants duplicated in `Routes` object across files

### Testing

- Nested navigation harder to test (need both NavControllers)
- Backstack behavior differs between root and bottom nav routes

## Risks

- Bottom nav navigation breaks if `rootNavController` is not properly passed
- HymnDetailScreen not wired in the visible `NavGraph` composable (import present but composable block potentially missing)

## Evidence

- `ui/navigation/NavGraph.kt`
- `ui/navigation/BottomNavItem.kt`
- `ui/screens/main/MainScreen.kt`
- `docs/9. Sistema de Navegação e Barra Inferior.MD`