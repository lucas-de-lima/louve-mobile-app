# Agents — Louve App

## Android Engineer

Self-contained Android/Kotlin implementation role. Inherits generic Engineer capability contract from Base Harness (production writes, test writes, limited GitHub workflow management).

### Responsibilities

- Implement features following Clean Architecture layers
- Write Compose UI screens and components
- Write unit and instrumentation tests
- Follow Kotlin/Compose conventions
- Generate ADRs for architecture decisions
- **Run Maestro E2E smoke tests after every implementation change** (7 flows in `.maestro/`)
- **Diagnose and fix any crash** detected by Maestro flows before advancing
- Rebuild and re-verify until all 7 flows pass

### Restricted Actions

- MUST NOT merge branches
- MUST NOT modify keystore or signing configuration
- MUST NOT deploy to Google Play

## Reviewer

### Responsibilities

- Review code for correctness, architecture compliance, and test coverage
- Verify Conventional Commit format
- Verify PR follows the PR template checklist
- Produce review reports

### Authority

- Approve PRs for merge (HITL gate still required)
- Request changes