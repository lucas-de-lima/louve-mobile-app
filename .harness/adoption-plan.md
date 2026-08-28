# Agentic Adoption Plan — Louve App

## 1. Capacidades do Base Harness que PODEM ser adotadas IMEDIATAMENTE

| Capability | Justification | Risk |
|------------|--------------|------|
| WF-004 Work Item Planning | Já usa GitHub Issues; estruturamento agentic é complementar | Baixo |
| WF-005 Implementation (Android) | Agente pode implementar seguindo Clean Architecture existente | Baixo |
| WF-006 Verification (Code Review) | Revisão agentic de PRs é seguro | Baixo |
| ADR authoring | Documentar decisões arquiteturais retrospectivamente | Baixo |
| HITL gates (HG-MERGE-STORY, HG-MERGE-FEATURE) | Já existe revisão humana; formalizar com gates | Baixo |
| Conventional Commit enforcement | Já adotado; agente pode verificar | Baixo |
| Test infrastructure scaffolding | Test dependencies existem; agente pode criar testes | Baixo |

## 2. Capacidades que exigem ADAPTAÇÃO

| Capability | Adaptation Needed | Risk |
|------------|------------------|------|
| Kotlin engineering skills | Base tem Engineering genéricas; especializar para Android/Kotlin | Médio |
| CI integration | Base espera CI pipe; projeto só tem release action | Médio |
| Release workflow | Base WF-007 espera pipeline CI/CD; projeto é manual | Médio |

## 3. Capacidades que PERMANECEM DESABILITADAS

| Capability | Reason |
|------------|--------|
| WF-007 automated release | Release é manual (Play Store); agente não pode publicar |
| HG-DEPLOY automation | Google Play Console exige humano |
| Automated keystore/signing | Risco de segurança; segredo fora do repositório |
| GitHub Project board sync | Não existe board configurado |
| Docker infrastructure | Projeto não usa Docker |
| Engineering skills (Base-provided) | Projeto define sua própria especialização Android/Kotlin |

## 4. Processos atuais PRESERVADOS

- Git Flow branching: `main` → `develop` → `feat/*` / `fix/*`
- Conventional Commits
- Code review before merge (PR workflow)
- Manual release process (build + sign + Play Console)
- Manual keystore management
- Build via Gradle (no CI build server)

## 5. Melhorias Agentic sem alterar comportamento do produto

- **Test scaffolding:** Agente pode criar testes seguindo padrões existentes
- **Code review automation:** Agente revisa PRs antes do humano
- **ADR generation:** Agente documenta decisões retrospectivamente
- **Changelog generation:** Agente pode manter CHANGELOG baseado em commits
- **Dependency update analysis:** Agente pode sugerir atualizações seguras
- **Architecture compliance checks:** Agente verifica se `domain` não tem deps Android

## 6. Incremental Adoption Plan

### Fase 1 — Foundation (imediata)
1. Instalar Dedicated Harness `.harness/` ✓
2. Criar HITL gates vazios
3. Validar estrutura com `harnessctl`

### Fase 2 — Read-Only Discovery
1. Executar discovery completo via agente
2. Preencher vault com conhecimento detalhado
3. Gerar ADRs para decisões arquiteturais existentes

### Fase 3a — Code Review (auditoria retrospectiva)
1. Agente revisa PRs já mergeados (leitura, sem ação)
2. Produz review reports para validação de qualidade
3. Humano avalia acurácia das revisões do agente

### Fase 3b — Code Review (PRs ativos)
1. Agente revisa PRs abertos antes do humano
2. Produz review reports estruturados
3. Humano continua aprovando merges (HITL gate)

### Safety Gate: Avaliação Humana
**Ponto de decisão:** Humano avalia a qualidade das revisões do agente (Fase 3b). Se consistentemente úteis e precisas, prosseguir para Fase 4. Caso contrário, ajustar skills antes de permitir escrita de código.

### Fase 4a — Bug Fixes (escrita controlada) ✅ 100% (18/18 bugs resolvidos)
1. ✅ Architecture violations resolvidos (BUG-001 a BUG-005)
2. ✅ Silent error handling resolvido (BUG-010, BUG-011)
3. ✅ ConnectivityMonitorService cleanup (BUG-012)
4. ✅ Repository housekeeping (BUG-016, BUG-017, BUG-018)
5. ✅ Build atualizado (Gradle 9.5.0, AGP 9.3.2, Kotlin 2.3.21)
6. ✅ Build verificado: `./gradlew assembleDebug` BUILD SUCCESSFUL
7. ✅ Test debt resolvido (BUG-006 a BUG-009) — 37 testes criados
8. ✅ PR quality policies implementadas (BUG-013, BUG-014, BUG-015)
- **PR agregado:** [#39 fix/bugs-aggregate → develop](https://github.com/lucas-de-lima/louve-mobile-app/pull/39)

### Fase 4b — Feature Implementation
1. Agente implementa stories completas em branches `feat/`
2. Agente escreve testes (unit + instrumentation)
3. ADR gerado se mudança arquitetural
4. Humano revisa e HITL gate para merge

### Fase 5 — Quality Gates (não-bloqueante → bloqueante)
1. Test coverage tracking: medição não-bloqueante
2. Lint automation: sugestões não-bloqueantes
3. Architecture compliance: verificação não-bloqueante
4. Após maturidade: promover para bloqueante com HITL exception policy

### Fase 6 — CI Build Pipeline (opcional, requer decisão humana)
1. Adicionar GitHub Action para build (`./gradlew assembleDebug`)
2. Adicionar GitHub Action para lint (`./gradlew lint`)
3. Adicionar GitHub Action para testes (`./gradlew test`)
4. **Decisão humana necessária:** CI build requer `google-services.json` e `keystore.properties` seguros. Possível abordagem: CI roda `assembleDebug` (não assinado) sem segredos; release continua manual.

## 7. Estrutura de Fases — Mapa de Risco

| Fase | Risco | Dependência |
|------|-------|------------|
| 1 Foundation | Mínimo | Nenhuma |
| 2 Discovery | Mínimo (read-only) | Fase 1 |
| 3a Code Review (auditoria) | Mínimo (read-only) | Fase 2 |
| 3b Code Review (ativo) | Baixo (sem escrita) | Fase 3a |
| Safety Gate | Decisão humana | Fase 3b |
| 4a Bug Fixes | Médio (escrita controlada) | Safety Gate |
| 4b Features | Médio (escrita completa) | Fase 4a |
| 5 Quality Gates | Baixo (não-bloqueante) | Fase 4b |
| 6 CI Pipeline | Médio (infra change) | Decisão humana |

## 8. Riscos e Mitigações

| Risco | Mitigação |
|-------|-----------|
| Agente introduzir dependência Android em domain layer | Política + revisão obrigatória; Fase 5 enforce |
| Agente modificar build/release config | Ferramentas restritas; HITL gate; CI check pós-Fase 6 |
| Agente gerar código inconsistente com temas | Revisão humana obrigatória; Compose preview test |
| Agente sugerir mudanças arquiteturais sem ADR | HITL gate HG-ARCHITECTURE |
| Agente com qualidade de review insuficiente | Safety Gate impede progressão para Fase 4 |
| Agente introduzir regression não testada | Testes obrigatórios por política; Fase 5 coverage tracking |