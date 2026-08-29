# Baseline — Discovery Evidence

## Observações (fato observado)

1. Projeto em produção (v1.1.0) na Google Play Store
2. Código-fonte bem estruturado com Clean Architecture
3. Documentação técnica extensa e atualizada
4. Testes mínimos (apenas exemplos)
5. CI apenas para criar GitHub Release em tag push
6. Build e release totalmente manuais
7. Git Flow com apenas `main` e `develop` ativos
8. Single developer (Lucas de Lima)
9. Keystore gerenciado manualmente fora do repositório
10. Firestore rules versionadas em `firestore.rules`
11. 8 temas implementados (documentação indica 3, código revela 8)
12. `Favoritos.md` é documento desatualizado (não reflete implementação real)
13. `HomeViewModel` cria `HymnRepositoryImpl()` diretamente (bypass DI)
14. `AuthRepository.kt` importa `AuthUiState` do UI layer (violação arquitetural)
15. `DataMigrationService` e `BidirectionalSyncService` importam `DefaultTheme` do UI layer

## Inferências

1. Processo de release é manual e dependente de uma única pessoa
2. Ausência de testes sugere risco de regression não detectada
3. Sem CI build, erros de compilação só aparecem localmente
4. Boa documentação indica disciplina de engenharia
5. Arquitetura foi evoluída além da documentação (8 temas vs 3 documentados)
6. Violações de Clean Architecture são resultado de pragmatismo, não desconhecimento
7. Documento `Favoritos.md` é obsoleto — planejamento anterior à implementação

## Decisões Existentes

1. Clean Architecture (ui/domain/data) — deliberada e documentada
2. Hymn data compiled as Kotlin (not JSON) — decisão de performance (ADR-001)
3. Hybrid favorites (local-first, cloud sync) — decisão arquitetural (ADR-002)
4. Centralized theme system with CompositionLocal (ADR-003)
5. Nested NavHost navigation with dynamic icons (ADR-004)
6. Three-service sync pipeline with union merge (ADR-005)
7. No UseCase layer — simplificação intencional
8. Google Sign-In via `GoogleSignInClient` (API estável) — deliberado

## Restrições

1. Min SDK 24 (Android 7.0)
2. Compile/Target SDK 35 (Android 15)
3. Keystore é único ponto de falha para updates na Play Store
4. Domain layer não pode ter dependências Android (com violações)
5. Firebase encapsulado na camada data
6. `domain` layer 100% Kotlin puro para compatibilidade KMP futura

## Dívida Técnica

1. Cobertura de testes: crítica (apenas exemplos)
2. CI build pipeline: inexistente
3. Lint automation: não configurada
4. Crash reporting: não implementado (sem Crashlytics)
5. ProGuard/R8 configurado sem verificação automatizada
6. Silent error handling com TODO em DataMigrationService
7. ConnectivityMonitorService nunca parado (vazamento de recurso)
8. Documento Favoritos.md desatualizado (pode enganar novos devs)
9. AuthUiState/AuthError no UI layer (deveria estar no domain)

## Lacunas

1. Observabilidade/monitoramento de produção
2. Testes de regressão e integração
3. Pipeline CI/CD automatizada
4. Feature flags / Remote Config
5. Multi-environment deployment (staging, production)
6. Capacidade de rollback
7. WorkManager para sync periódico em background
8. Indicador de sincronização na UI
9. Sincronização incremental (vs full-sync)

## Riscos

| Risco | Probabilidade | Impacto | Mitigação |
|-------|--------------|---------|-----------|
| Keystore perdido | Baixa | Crítico | Backup em múltiplos locais seguros |
| Bus factor = 1 | Alta | Alto | Documentação extensa mitiga parcialmente |
| Regression sem testes | Média | Alto | Fase 5 quality gates + obrigatoriedade de testes |
| Violação Clean Architecture bloqueia KMP | Média | Médio | ADR-006 registra violações para correção |
| Silent error handling esconde bugs | Média | Médio | TODO markers permitem rastreamento |
| Sync nunca é incremental | Alta | Baixo | Impacto apenas em datasets muito grandes |