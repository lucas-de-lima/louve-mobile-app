# Maestro E2E — Skill Reference

## Purpose

Mandatory E2E smoke tests run after every implementation change. Detects crashes and ANRs.

## Prerequisites

- Maestro CLI 2.9.0+ installed at `C:\maestro\bin` (Windows)
- Emulator running (Android Studio AVD)
- APK built (`./gradlew assembleDebug`)
- APK installed (`./gradlew installDebug`)

## Flows (7)

| File | What it tests |
|------|---------------|
| `.maestro/00_splash_and_home.yaml` | Splash → boot → home with bottom nav |
| `.maestro/01_bottom_nav_tabs.yaml` | All 4 bottom nav tabs |
| `.maestro/02_settings_flow.yaml` | Mais → Configurações → back |
| `.maestro/03_profile_flow.yaml` | Mais → Perfil → back |
| `.maestro/04_hymn_detail_flow.yaml` | Home → hymn → detail → back |
| `.maestro/05_about_and_support_flow.yaml` | Mais → Sobre → Ajuda → back |
| `.maestro/06_search_flow.yaml` | Search field → input → clear |

## Running

```powershell
# All flows
.\scripts\run_maestro_tests.ps1

# Single flow
maestro test .maestro/00_splash_and_home.yaml
```

## Crash Recovery Protocol

1. Run `.\scripts\run_maestro_tests.ps1`
2. If any flow fails (`❌ FAILED`): check `maestro_run_*.log` for stack trace
3. Fix root cause in source
4. Rebuild: `.\gradlew.bat assembleDebug`
5. Reinstall: `.\gradlew.bat installDebug`
6. Re-run failed flow: `maestro test .maestro/<failed_flow>.yaml`
7. Repeat until 7/7 pass

## Output Artifacts

- `maestro_reports/*.xml` — JUnit test reports
- `maestro_run_*.log` — Logcat dump
- `C:\Users\<user>\.maestro\tests\<timestamp>\` — screenshots + recordings

## Important

- **Crash = block merge.** Do not advance until all 7 flows pass.
- Logcat always captured after suite execution.
- Screenshots are saved automatically on each `takeScreenshot` command.