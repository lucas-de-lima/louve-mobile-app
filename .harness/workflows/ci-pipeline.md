# CI Pipeline — Louve App

## Status

✅ **ATIVADO** — 2026-08-24

Workflow file: `.github/workflows/pr-validation.yml`

## Jobs

| Job | Trigger | Description |
|-----|---------|-------------|
| `validate` | PR to develop/main + push to develop/main | Build (debug), tests, lint, architecture compliance, PR size check, SonarCloud analysis |
| `release` | Tag push `v*` | GitHub Release creation (existing workflow) |
| `sonar-quality-gate` | PR only (after validate) | Checks SonarCloud Quality Gate result |

## Gates Embutidos

- ✅ Build passes (`./gradlew assembleDebug`)
- ✅ Unit tests pass (`./gradlew test`)
- ✅ Lint passes (`./gradlew lint`)
- ✅ Architecture compliance (`.harness/scripts/check-architecture.py`)
- ✅ PR size advisory (< 500 lines / < 20 files)
- ✅ SonarQube Cloud analysis (quality gate)

## Secrets Required

| Secret | Purpose | Status |
|--------|---------|--------|
| `SONAR_TOKEN` | SonarCloud authentication | ❌ PENDENTE — adicionar no GitHub |
| `GOOGLE_SERVICES_JSON` | Firebase config for CI | ⭕ Opcional (debug build funciona sem) |

## SonarCloud Integration

- Project key: `lucas-de-lima_louve-mobile-app`
- Organization: `lucas-de-lima`
- Already connected via GitHub App (comentou no PR #10)
- Quality gate é verificado mas **não bloqueia merge** (`continue-on-error: true`)
- Para bloquear merge, adicionar como required status check nas regras de branch

## Notas

- Release build (assinado) NÃO roda em CI — keystore não exposto
- `google-services.json` opcional — debug build compila sem Firebase config
- SonarCloud quality gate é informativo inicialmente; pode ser promovido a bloqueante após BUGs arquiteturais resolvidos