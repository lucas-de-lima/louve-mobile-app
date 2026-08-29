# Domain Context — Louve App

## Domain

**Harpa Cristã** — Brazilian Protestant hymnbook with 640 hymns.

## Core Entities

| Entity | Description |
|--------|-------------|
| Hymn | id, number, title, verses (List<String>), chorus (String?) |
| UserProfile | uid, name, email, photoUrl, createdAt |
| UserSettings | themeId (String) |
| Result<T> | Success(data) | Error(exception) wrapper |

## Domain Repository Contracts

- **AuthRepository** — signIn, signOut, getCurrentUser (Flow<User?>)
- **HymnRepository** — getAllHymns, getHymnById
- **FavoritesRepository** — getFavoriteHymnIds (Flow<Result<Set<String>>>), addFavorite, removeFavorite, isFavorite
- **SettingsRepository** — theme (Flow<String>), saveTheme
- **UserRepository** — getUserProfile, ensureUserStructure, updateUserSettings, addFavorite, etc.
- **LocalFavoritesRepository** — scoped to DataStore
- **LocalSettingsRepository** — scoped to DataStore
- **AnalyticsService** — logEvent