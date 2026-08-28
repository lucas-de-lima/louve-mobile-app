# Adoption Plan — Agentic Adoption para Louve App

Ver [[project-profile|Project Profile]] para baseline completo.
Ver [[adoption-plan]] para plano detalhado.

## Status Atual

- Foundation: ✅ Instalada
- Discovery: ✅ Completa
- Code Review (auditoria): ✅ Concluída
- Bug Registry: ✅ 18 bugs registrados
- GitHub Issues: ✅ Criadas (#12-#30), com labels
- GitHub Project Board: ✅ Reutilizado board #1 — renomeado para "Louve App - SDLC"
- Board Items: ✅ 18 issues adicionadas; BUG-001 a BUG-005 em "In Progress"
- Quality Gates: ✅ 8 gates definidos; QG-001 a QG-008 integrados ao CI
- CI Pipeline: ✅ Ativado — `.github/workflows/pr-validation.yml`
- SonarCloud: ✅ Integrado ao pipeline (qualidade verificada em cada PR)

## Ações Realizadas

### GitHub Project Board
- Renomeado de "@lucas-de-lima's untitled project" → "Louve App - SDLC"
- Status columns: Todo, In Progress, Done (existentes)
- 18 issues importadas, 5 em In Progress (BUGs arquiteturais)

### CI Pipeline (`pr-validation.yml`)
- **Build (debug):** `./gradlew assembleDebug`
- **Tests:** `./gradlew test`
- **Lint:** `./gradlew lint`
- **Architecture compliance:** `python .harness/scripts/check-architecture.py`
- **PR size advisory:** alerta se >500 linhas ou >20 arquivos
- **SonarQube Cloud:** análise automática + quality gate check
- **Release:** mantido (cria GitHub Release em tag `v*`)

### Pendente (manual)
- Adicionar `SONAR_TOKEN` nos secrets do repositório
- Adicionar `GOOGLE_SERVICES_JSON` nos secrets (opcional para debug build)
- Configurar branch protection rules no GitHub (develop/main) exigindo status checks

## Próximo Passo (pós-Harness)

1. Criar GitHub Project board com colunas: Backlog → To Do → In Progress → Review → Done
2. Transferir BUG-001 a BUG-018 como Issues com labels apropriadas
3. Mover arquitetura violations (BUG-001 a BUG-005) para To Do
4. Decidir sobre ativação do CI Pipeline (Fase 6)

## Descobertas da Fase 2

### ADRs Gerados (6)
- ADR-001: Compiled hymn data strategy
- ADR-002: Hybrid favorites system
- ADR-003: Theme architecture
- ADR-004: Navigation architecture
- ADR-005: Data sync strategy
- ADR-006: Clean Architecture enforcement

### Violações Arquiteturais Encontradas (5)
- domain → ui import (HIGH): `AuthRepository.kt` importa `AuthUiState`
- data → ui import (HIGH): `FirebaseAuthRepositoryImpl` importa `AuthUiState`
- data → ui import (MEDIUM): `DataMigrationService` importa `DefaultTheme`
- data → ui import (MEDIUM): `BidirectionalSyncService` importa `DefaultTheme`
- ViewModel bypass DI (HIGH): `HomeViewModel` cria `HymnRepositoryImpl()` diretamente

### Documentos Desatualizados
- `Favoritos.md` — não reflete implementação real
- `docs/13. Funcionalidades Implementadas.md` — omite 5 temas (diz 3, código tem 8)

## Riscos Conhecidos

1. Ausência de testes — risco de regression não detectada
2. CI inexistente (build) — sem validação automática pré-merge
3. Keystore único — risco existencial se perdido
4. Bus factor 1 — único desenvolvedor
5. 5 violações de Clean Architecture — bloqueiam KMP e reduzem testabilidade
6. Silent error handling em serviços de sync — bugs podem passar despercebidos