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
5. Create PR to feature branch
6. Agent review → human HG-MERGE-STORY gate
7. Repeat for each story
8. Create PR from feature branch to develop
9. Human HG-MERGE-FEATURE gate

### Validation

- Build passes (`./gradlew assembleDebug`)
- Tests pass (`./gradlew test`)
- No lint errors
- Architecture rules respected (domain has no Android deps)

### Output

- Source changes on feature/story branches
- Test evidence
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