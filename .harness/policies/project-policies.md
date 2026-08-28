# Policies — Louve App

## Branching

Adopted from Base: main → develop → feat/*, fix/*. No change required.

## HITL

All mandatory Base HITL gates apply. HITL está **ativo** desde a instalação do Dedicated Harness.

| Gate | Applicable | Expected Authority | Ativo |
|------|-----------|-------------------|-------|
| HG-PRODUCT | Yes | Lucas de Lima | Sim |
| HG-ARCHITECTURE | Yes | Lucas de Lima | Sim |
| HG-SCOPE | Yes | Lucas de Lima | Sim |
| HG-MERGE-STORY | Yes | Lucas de Lima | Sim |
| HG-MERGE-FEATURE | Yes | Lucas de Lima | Sim |
| HG-MERGE-DEVELOP | Yes | Lucas de Lima | Sim |
| HG-RELEASE | Yes | Lucas de Lima | Sim |
| HG-SECURITY-EXCEPTION | Yes | Lucas de Lima | Sim |
| HG-DESTRUCTIVE | Yes | Lucas de Lima | Sim |
| HG-DEPLOY | Yes | Lucas de Lima | Sim |

### Regras de Ativação

- Feature PRs (`feat/*` → `develop`) exigem **HG-MERGE-FEATURE**
- Merge `develop` → `main` exige **HG-MERGE-DEVELOP**
- Tags de release exigem **HG-RELEASE**
- Publicação na Play Store exige **HG-DEPLOY**
- Fix PRs (`fix/*` → `develop`) também exigem **HG-MERGE-FEATURE**

## Code Quality

- All new code must include tests (unit and/or instrumentation)
- **Cobertura mínima de 80%** nas classes novas/modificadas das camadas `domain` e `data`
- **Testes unitários obrigatórios** para novos ViewModels
- Testes de UI recomendados mas não obrigatórios para novas telas
- All PRs must pass `./gradlew check` before merge
- Use `ktlint` formatting before commits
- Follow architecture rules: `domain` is Android-free
- **Maestro E2E smoke tests (all 7 flows) MUST pass** after every implementation — crash = block merge
- **Crash recovery is mandatory**: agent diagnoses logcat, fixes root cause, re-runs flows until all pass

## Security

- Keystore credentials NEVER committed (in `.gitignore`)
- `google-services.json` NEVER committed
- Firestore rules versioned in `firestore.rules`
- Secrets injected via `keystore.properties` + `resValue`