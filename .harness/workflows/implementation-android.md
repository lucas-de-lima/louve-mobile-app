# Workflows — Louve App

## WF-005: Implementation (Android)

### Entry Conditions

- Feature or Task is Ready (acceptance criteria defined)
- Feature branch created from develop (`feat/<name>` or `fix/<name>`)

### Steps

1. Create story branch from feature branch (`story/<name>`) if needed
2. Implement changes following Clean Architecture layers
3. Write tests covering the change
4. Run `./gradlew check` locally
5. Run Maestro E2E smoke tests (all 7 flows must pass)
6. If any Maestro flow fails: diagnose crash from logs, fix root cause, repeat step 5
7. Create PR to feature branch
8. Agent review → human HG-MERGE-STORY gate
9. Repeat for each story
10. Create PR from feature branch to develop
11. Human HG-MERGE-FEATURE gate

### Validation

- Build passes (`./gradlew assembleDebug`)
- Tests pass (`./gradlew test`)
- All 7 Maestro E2E flows pass (`maestro test .maestro/<flow>` each flow, or `scripts/run_maestro_tests.ps1`)
- Maestro crash reports analyzed and resolved before advancing
- No lint errors
- Architecture rules respected (domain has no Android deps)

### Maestro E2E — Crash Recovery Protocol

When a Maestro flow fails:
1. Collect `maestro_reports/` and `maestro_run_*.log`
2. Read logcat output to identify the crash stack trace
3. Fix the root cause in source code
4. Rebuild APK (`./gradlew assembleDebug`)
5. Reinstall on emulator (`./gradlew installDebug`)
6. Re-run the failed flow
7. Repeat until all 7 flows pass

### Output

- Source changes on feature/story branches
- Test evidence
- Maestro execution report (screenshots, logs, JUnit XML)
- Review report

## WF-007: Release Preparation (Android)

### Entry Conditions

- All features for the release are merged to develop
- Release is approved (HG-RELEASE)

### Steps

1. Checkout `main` and merge `develop`
2. Update version name/code in `build.gradle.kts`
3. Update CHANGELOG.md
4. Run `./gradlew bundleRelease` to produce AAB
5. Sign with keystore
6. Create tag `v<version>` (via `scripts/create-release.sh`)
7. GitHub Action creates GitHub Release
8. Upload AAB to Google Play Console manually

### Validation

- Release build succeeds
- App signed correctly
- CHANGELOG updated
- Tag pushed and release created

### Restrictions

- Keystore credentials NEVER in CI or agent context
- Play Store publish is ALWAYS manual (HG-DEPLOY = N/A)