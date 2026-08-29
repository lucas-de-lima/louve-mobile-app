# Tools — Louve App

## Gradle

```yaml
command: ./gradlew
authorized: true
authorization: Android Engineer, Reviewer
restrictions:
  - MUST NOT run release signing tasks without HITL
  - MUST NOT modify keystore configuration
```

## Android SDK

```yaml
available: true
path: $ANDROID_HOME
restrictions:
  - MUST NOT deploy APK/AAB automatically
```

## GitHub

```yaml
restrictions:
  - PR creation: automatable
  - PR merge: PROHIBITED without HITL gate
  - Branch creation: automatable
  - Tag creation: HITL gate required
```