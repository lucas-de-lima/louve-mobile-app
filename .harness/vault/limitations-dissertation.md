---
id: limitations-dissertation
type: analysis
tags: [dedicated-harness, louve-app, limitations, technical-debt, quality]
---

# Limitations Deep-Dive — Louve Mobile App

Date: 2026-08-29
Author: Discovery Agent (Dedicated Harness)
Status: Complete
Next Step: Epics extraction

---

## 0. Prólogo — Contexto

O Product Discovery identificou 13 limitações no Louve App. Este documento as disseca
individualmente: causa raiz, impacto real (não teórico), dependências entre si,
solucionabilidade, e custo aproximado de resolução.

As 13 limitações não são independentes. Muitas compartilham a mesma causa raiz.
Endereçar uma pode desbloquear ou simplificar outras. O agrupamento por cluster
de causa raiz é parte fundamental desta análise.

---

## 1. Agrupamento por Causa Raiz

```
CR-1: Infraestrutura de Engenhamento
├── L01 — Ausência de testes
├── L03 — CI/CD inexistente
├── L13 — E2E sem automação em CI

CR-2: Processo de Release
├── L06 — Release totalmente manual
├── L02 — Bus factor = 1 (parcial)

CR-3: Observabilidade
├── L04 — Sem Crashlytics

CR-4: Gestão de Funcionalidades
├── L05 — Sem feature flags

CR-5: Dívida Arquitetural
├── L11 — Violações de Clean Architecture

CR-6: Documentação
├── L12 — Documentação obsoleta

CR-7: Engenharia de Dados (Sync)
├── L07 — Sincronização não incremental
├── L09 — Sem WorkManager

CR-8: Gaps de UX
├── L08 — Sem indicador de sincronização
├── L10 — DiscoverScreen vazia
```

**Observação:** L02 (Bus factor = 1) é uma limitação estrutural que não tem
solução técnica pura. Ela aparece em múltiplos clusters porque amplifica o risco
de todas as outras.

---

## 2. Dissertação de Cada Limitação

### L01 — Ausência de Testes

| Atributo | Valor |
|----------|-------|
| **Causa raiz** | Projeto começou sem cultura de testes; v1.2.0 adicionou 37 testes unitários + 7 Maestro flows, mas cobertura ainda é próxima de zero |
| **Impacto real** | Regression silenciosa a cada mudança. Bugs arquiteturais do v1.1.0 (18 bugs corrigidos no v1.2.0) não foram detectados por testes |
| **O que está em risco** | Cada novo commit pode quebrar funcionalidade sem aviso. Esforço de release aumenta porque validação é manual |
| **Dependências** | Nenhuma para começar — testes unitários podem ser escritos sem CI |
| **Solucionável?** | **Sim**. Já existem 37 testes como baseline. Infraestrutura de teste (JUnit, MockK, Coroutines Test) está configurada. |
| **Complexidade** | Média — requer disciplina contínua, não um esforço único |
| **Custo estimado** | ~40h para cobertura crítica (ViewModels + Repositories + Sync Services) |
| **Notas** | L03 (CI) é necessária para que testes tenham valor real de bloqueio de regressão |

### L02 — Bus Factor = 1

| Atributo | Valor |
|----------|-------|
| **Causa raiz** | Projeto mantido por uma única pessoa (Lucas de Lima) |
| **Impacto real** | Se o mantenedor ficar indisponível, o projeto para: sem releases, sem correções de bugs críticos, sem resposta a issues |
| **O que está em risco** | A continuidade do produto. Keystore local é gerenciado por uma pessoa. Acesso ao Google Play Console é de uma pessoa. |
| **Dependências** | Social/ organizacional — não se resolve apenas com código |
| **Solucionável?** | **Parcialmente**. Mitigação possível via: documentação (já extensa), CI/CD (remove dependência manual), keystore backup em cofre acessível, onboarding de contribuidores. A solução estrutural exige novos mantenedores. |
| **Complexidade** | Alta — envolve gestão de comunidade |
| **Custo estimado** | ~10h para mitigação técnica (CI/CD, keystore backup documentado) |
| **Notas** | CI/CD (L03 + L06) é a mitigação técnica de maior impacto. Não resolve o bus factor, mas reduz as consequências. |

### L03 — CI/CD Inexistente

| Atributo | Valor |
|----------|-------|
| **Causa raiz** | Projeto amadureceu de protótipo para produção sem pipeline automatizado |
| **Impacto real** | Build, lint e testes só rodam localmente. Erros de compilação descobertos tarde. Sem validação de PR. Release é sequência manual de passos. |
| **O que está em risco** | Erro humano no processo de release pode publicar build quebrado ou não assinado. |
| **Dependências** | GitHub Actions (infra grátis já disponível). L01 (testes) + lint precisam existir para o pipeline validar algo. |
| **Solucionável?** | **Sim**. Workflow de GitHub Actions para build + lint + testes em PR e push. |
| **Complexidade** | Média |
| **Custo estimado** | ~8h para pipeline básico (build, lint, testes em PR) |
| **Notas** | Este é o **multiplicador de impacto** de L01, L13, L05. Sem CI, testes não bloqueiam regressão. |

### L04 — Sem Crashlytics

| Atributo | Valor |
|----------|-------|
| **Causa raiz** | Não configurado desde o início; Firebase Analytics presente mas sem crash reporting |
| **Impacto real** | Erros de produção são invisíveis. Bugs que afetam usuários reais podem passar semanas sem detecção. |
| **O que está em risco** | Experiência do usuário degrada silenciosamente. Sem capacidade de priorizar correções por impacto real. |
| **Dependências** | Firebase project (já configurado). Conta Google Play. |
| **Solucionável?** | **Sim**. Adicionar Firebase Crashlytics SDK + configuração. |
| **Complexidade** | Baixa |
| **Custo estimado** | ~2h |
| **Notas** | Integração rápida com Firebase já existente. Impacto imediato na qualidade. **Quick Win.** |

### L05 — Sem Feature Flags

| Atributo | Valor |
|----------|-------|
| **Causa raiz** | Projeto nunca precisou de rollout gradual (único desenvolvedor, sem usuários em escala) |
| **Impacto real** | Cada nova funcionalidade é ativada para 100% dos usuários no momento do release. Sem kill switch. Sem testes A/B. |
| **O que está em risco** | Uma funcionalidade problemática não pode ser desativada remotamente. Rollback de versão na Play Store leva horas. |
| **Dependências** | Firebase Remote Config (já disponível no Firebase project). |
| **Solucionável?** | **Sim**. Firebase Remote Config com flag padrão. |
| **Complexidade** | Baixa |
| **Custo estimado** | ~4h (configuração + wrapper no código) |
| **Notas** | **Quick Win.** Depende de L03 (CI) apenas para validação de que flags funcionam antes do merge. |

### L06 — Release Totalmente Manual

| Atributo | Valor |
|----------|-------|
| **Causa raiz** | Processo artesanal: `./gradlew assembleRelease`, assinar com keystore local, upload manual no Google Play Console |
| **Impacto real** | Release depende de uma máquina com keystore, um humano com acesso ao Play Console, e nenhum deles pode errar o passo. |
| **O que está em risco** | Keystore é único ponto de falha. Sem keystore, não há atualização na Play Store. |
| **Dependências** | Keystore protegido. Google Play Console access. Service Account para upload automatizado. |
| **Solucionável?** | **Sim**, com pipeline de release automatizada (GitHub Actions + Google Play Publisher API). |
| **Complexidade** | Alta — envolve credenciais, service account, validação de segurança |
| **Custo estimado** | ~16h (pipeline de release + documentação de contingência) |
| **Notas** | Pipeline de release (L03) é pré-requisito. Não pode ser feito antes de CI básico. |

### L07 — Sincronização Não Incremental

| Atributo | Valor |
|----------|-------|
| **Causa raiz** | Implementação pragmática: merge union sobre conjuntos inteiros. Suficiente para volume atual (favoritos < 640, datas). |
| **Impacto real** | Baixo no momento — dados são pequenos. Cresce com uso (muitos favoritos, listas de culto, histórico). |
| **O que está em risco** | Se o app crescer em dados por usuário (listas de culto, histórico, anotações), full-sync será ineficiente e caro em banda. |
| **Dependências** | Sistemas de sincronização existentes (DataMigrationService, BidirectionalSyncService). |
| **Solucionável?** | **Sim**, mas não prioritário agora. Sync incremental + WorkManager (L09) resolvem juntos. |
| **Complexidade** | Média |
| **Custo estimado** | ~20h (implementação de diff tracking + migração) |
| **Notas** | **Não urgente.** Volume de dados atual não justifica a complexidade. Deve ser feito junto com L09. |

### L08 — Sem Indicador de Sincronização na UI

| Atributo | Valor |
|----------|-------|
| **Causa raiz** | Sync foi implementado como serviço de background invisível para o usuário |
| **Impacto real** | Usuário não sabe se favoritos foram sincronizados. Experiência de confiança reduzida. |
| **O que está em risco** | Usuário pode fechar o app achando que dados estão salvos, quando na verdade houve falha de sync silenciosa. |
| **Dependências** | Serviços de sincronização existentes (já implementados). |
| **Solucionável?** | **Sim**. Adicionar StateFlow de sync status e indicador visual no TopAppBar ou BottomNavBar. |
| **Complexidade** | Baixa |
| **Custo estimado** | ~4h |
| **Notas** | **Quick Win.** Gera confiança imediata para usuários logados. |

### L09 — Sem WorkManager

| Atributo | Valor |
|----------|-------|
| **Causa raiz** | ConnectivityMonitorService faz sync manual via NetworkCallback. Não há worker periódico. |
| **Impacto real** | Sync só ocorre quando app está aberto e conectividade muda. Se app ficar muito tempo em background sem mudança de rede, dados não sincronizam. |
| **O que está em risco** | Dispositivos que ficam online por longos períodos sem mudança de conectividade podem atrasar sincronização. |
| **Dependências** | WorkManager (já disponível na AndroidX). |
| **Solucionável?** | **Sim**. Adicionar PeriodicWorkRequest para sync periódico + cleanup de listas expiradas. |
| **Complexidade** | Baixa |
| **Custo estimado** | ~6h |
| **Notas** | Pode ser combinado com L08 (indicador na UI) e L07 (sync incremental) para uma evolução coesa. |

### L10 — DiscoverScreen Vazia

| Atributo | Valor |
|----------|-------|
| **Causa raiz** | Tela criada como placeholder para funcionalidades futuras que nunca foram implementadas |
| **Impacto real** | Uma das 4 abas principais da navegação não tem conteúdo útil. Usuário que clica em "Descubra" vê apenas cards informativos. |
| **O que está em risco** | Experiência de descoberta é zero. Oportunidade perdida de engajamento. |
| **Dependências** | Sistema de categorização (F-05 do Product Discovery) é o conteúdo natural para esta tela. |
| **Solucionável?** | **Sim**, como parte de uma iniciativa maior (categorização). Não faz sentido preencher com conteúdo artificial. |
| **Complexidade** | Baixa (placeholder) / Alta (se for preenchida com categorização real) |
| **Custo estimado** | ~2h para conteúdo estático temporário; ~80h para categorização completa |
| **Notas** | **Decisão necessária:** manter como placeholder até categorização ficar pronta, ou preencher com conteúdo editorial mínimo. |

### L11 — Violações de Clean Architecture

| Atributo | Valor |
|----------|-------|
| **Causa raiz** | Pragmatismo durante implementação: `AuthRepository.kt` importa `AuthUiState` do UI layer; `DataMigrationService` e `BidirectionalSyncService` importam `DefaultTheme` |
| **Impacto real** | Domain layer tem dependências de Android/UI, o que impede a migração KMP e dificulta testes unitários puros. |
| **O que está em risco** | Bloqueio da evolução para KMP. Testes do domain precisam de mocks de Android. |
| **Dependências** | `AuthUiState` e `ThemeDefaults` precisam ser movidos para domain ou removidos. |
| **Solucionável?** | **Sim**. Mover `AuthUiState` para domain (já deveria estar lá). Substituir referências a `DefaultTheme` por constantes no domain (`ThemeDefaults` já existe). |
| **Complexidade** | Baixa |
| **Custo estimado** | ~4h (refatoração + verificação) |
| **Notas** | **Dívida pequena, impacto médio.** ADR-006 já registra as violações. Correção é direta. |

### L12 — Documentação Obsoleta

| Atributo | Valor |
|----------|-------|
| **Causa raiz** | `Favoritos.md` foi criado como documento de planejamento antes da implementação e nunca foi atualizado |
| **Impacto real** | Novo desenvolvedor lê `Favoritos.md` e tem expectativas incorretas sobre o que está implementado. |
| **O que está em risco** | Decisões tomadas com base em documentação desatualizada podem ser equivocadas. |
| **Dependências** | Nenhuma. É esforço isolado. |
| **Solucionável?** | **Sim**. Revisar `Favoritos.md` para alinhar com implementação real. |
| **Complexidade** | Baixa |
| **Custo estimado** | ~1h |
| **Notas** | **Quick Win.** Baixíssimo esforço, elimina risco de desinformação. |

### L13 — Testes E2E sem Automação em CI

| Atributo | Valor |
|----------|-------|
| **Causa raiz** | Maestro flows existem (7 arquivos) mas são executados manualmente via script PowerShell |
| **Impacto real** | E2E tests rodam apenas quando desenvolvedor decide executar. Sem bloqueio automático em PR. |
| **O que está em risco** | Regression visual ou de fluxo passa despercebida até teste manual. |
| **Dependências** | CI/CD pipeline (L03) precisa existir. Maestro CLI precisa estar disponível no runner. |
| **Solucionável?** | **Parcialmente**. Maestro tem suporte a GitHub Actions, mas emulador Android em CI é caro e lento. |
| **Complexidade** | Média-Alta (configuração de emulador em runner) |
| **Custo estimado** | ~8h (configuração + execução em CI) |
| **Notas** | Execução de Maestro em CI pode ser relegada a pipeline noturna ou manual, não necessariamente bloqueante em PR. |

---

## 3. Classificação de Solucionabilidade

| Limitação | Solucionável | Nota |
|-----------|-------------|------|
| L01 | Sim | Requer disciplina contínua |
| L02 | Parcialmente | CI/CD mitiga, mas não resolve |
| L03 | Sim | Pré-requisito para L06, L13 |
| L04 | Sim | Quick Win |
| L05 | Sim | Quick Win |
| L06 | Sim | Pós-requisito de L03 |
| L07 | Sim | Não urgente |
| L08 | Sim | Quick Win |
| L09 | Sim | Baixo esforço |
| L10 | Sim | Depende de categorização |
| L11 | Sim | Divídazinha arquitetural |
| L12 | Sim | Quick Win |
| L13 | Parcialmente | Caro, pode ser não-bloqueante |

---

## 4. Mapa de Dependências Entre Limitações

```
L03 (CI/CD) ─── desbloqueia ─── L06 (Release automático)
                                └── L13 (E2E em CI)
                                └── L01 (testes com valor real)

L05 (Feature flags) ─── usa ─── L03 (para validação em PR)

L07 (Sync incremental) ─── pode usar ─── L09 (WorkManager)

L10 (DiscoverScreen) ─── depende de iniciativa de categorização

L02 (Bus factor) ─── mitigado por ─── L03 + L06 + L04 + documentação
```

**Conclusão das dependências:** L03 (CI/CD) é o nó central. Sem ela:
- L01 (testes) existe mas não bloqueia regressão
- L06 (release) continua manual
- L13 (E2E) continua manual

---

## 5. Priorização Recomendada

### Fase 1 — Quick Wins (1-2 dias cada, independentes)
| Ordem | Limitação | Esforço | Benefício |
|-------|-----------|---------|-----------|
| 1 | L04 — Crashlytics | 2h | Visibilidade de erros em produção |
| 2 | L12 — Documentação obsoleta | 1h | Elimina desinformação |
| 3 | L08 — Indicador de sincronização | 4h | Confiança do usuário |
| 4 | L11 — Violações Clean Architecture | 4h | Desbloqueia KMP |
| 5 | L05 — Feature flags | 4h | Rollout seguro |
| 6 | L09 — WorkManager | 6h | Sync eficiente |

### Fase 2 — Fundação de Engenharia (1-2 semanas)
| Ordem | Limitação | Esforço | Benefício |
|-------|-----------|---------|-----------|
| 7 | L03 — CI/CD pipeline | 8h | Base para tudo |
| 8 | L01 — Testes automatizados | 40h | Prevenção de regressão |
| 9 | L13 — E2E em CI (parcial) | 8h | Cobertura de fluxos |

### Fase 3 — Release & Resiliência (2-4 semanas)
| Ordem | Limitação | Esforço | Benefício |
|-------|-----------|---------|-----------|
| 10 | L06 — Release automatizado | 16h | Remove dependência manual |
| 11 | L07 — Sync incremental | 20h | Eficiência futura |

### Não Bloqueante / Estrutural
| Limitação | Ação |
|-----------|------|
| L02 — Bus factor | Mitigação via Fase 2 + Fase 3. Solução estrutural requer novos mantenedores. |
| L10 — DiscoverScreen vazia | Resolver como parte de iniciativa de conteúdo (fora do escopo de Limitations). |

---

## 6. Resumo

| Métrica | Valor |
|---------|-------|
| Total de limitações analisadas | 13 |
| Quick Wins (Fase 1) | 6 |
| Fundação de Engenharia (Fase 2) | 3 |
| Release & Resiliência (Fase 3) | 2 |
| Não bloqueante / estrutural | 2 (L02, L10) |
| L03 (CI/CD) identificado como | Nó central de dependências |

---

*Dissertação completa. Próximo passo: extrair Epics para cada limitation solucionável.*