---
id: product-discovery
type: discovery
tags: [dedicated-harness, louve-app, product-discovery]
---

# Product Discovery — Louve Mobile App

Date: 2026-08-29
Author: Discovery Agent (Dedicated Harness)
Status: Complete

---

## 1. Product Discovery

### Purpose

O Louve App é um aplicativo Android open source dedicado à Harpa Cristã (640 hinos). Seu propósito é transformar a experiência com a Harpa Cristã em uma jornada de adoração, descoberta e conexão — não apenas um leitor de hinos, mas um ambiente digital de adoração e estudo.

### Main Actors

| Actor | Description |
|-------|-------------|
| **Usuário não autenticado** | Usa o app sem login. Favoritos e temas salvos localmente. |
| **Usuário autenticado (Google)** | Dados sincronizados na nuvem via Firestore. Perfil persistente. |
| **Desenvolvedor** | Mantém o código, configura temas, compila hinos. |
| **Administrador (Lucas de Lima)** | Único mantenedor. Realiza builds de release, gerencia keystore. |

### Existing Capabilities

1. **Catálogo completo de 640 hinos** — compilado em Kotlin (`HymnDataSource.kt`, 545KB, 7420 linhas), acesso instantâneo sem I/O
2. **Busca inteligente** — tolerante a acentos, ordem de palavras independente, busca em título/número/conteúdo, debounce de 300ms
3. **Visualização de hinos** — verso/ coro separados, controle de fonte (0.5x-2.0x), fundo imersivo, compartilhamento via BottomSheet
4. **Sistema de favoritos híbrido** — local (DataStore) + remoto (Firestore), mediador decide com base no estado de login
5. **8 temas dinâmicos** — Default, Dark, Sweet Candy, Aurora Matinal, Serenidade Noturna, Vida Verde, Chama Sagrada, Céu Celestial, com fundos contextuais e edge-to-edge
6. **Autenticação Google** — GoogleSignInClient + Firebase Auth, tratamento de erros (NetworkError, InvalidCredentials, UserCancelled, FirebaseError, UnknownError)
7. **Navegação completa** — 10+ telas, NavGraph aninhado, BottomNavBar com 4 abas e ícones dinâmicos
8. **Sincronização avançada** — DataMigrationService, BidirectionalSyncService, ConnectivityMonitorService com merge inteligente (union para favoritos, preferência local para tema)
9. **Analytics** — Firebase Analytics trackando login, visualizações, favoritos, buscas, compartilhamento, suporte
10. **Splash screen cinematográfica** — animação em Compose, ~3s
11. **Modo offline completo** — app funcional sem internet, dados locais preservados
12. **Persistência de preferências** — tema salvo, fonte persistida durante navegação
13. **9 ViewModels** — Main, Home, HymnDetail, Settings, Auth, Favorites, Profile, More, Support
14. **7 interfaces de repositório** — Auth, Hymn, Favorites, Settings, User, LocalFavorites, Analytics
15. **9 implementações de repositório** — FirebaseAuth, FirestoreUser, DefaultFavorites, DefaultSettings, DataStoreLocal, LocalSettings, HymnRepository, FirebaseAnalytics, 3 services (DataMigration, BidirectionalSync, ConnectivityMonitor)
16. **3 serviços de sincronização** — DataMigrationService (223 linhas), BidirectionalSyncService (220 linhas), ConnectivityMonitorService (216 linhas)
17. **Componentes reutilizáveis** — SearchField, HymnCardItem, HymnListItem, LouveBottomNavBar, 5 TopAppBar variants
18. **Result Pattern** — `sealed class Result<out T>` para operações assíncronas

### Business Rules

| # | Rule | Source |
|---|------|--------|
| BR-01 | Todo hino possui id único, número, título, versos e coro opcional | domain/model/Hymn.kt |
| BR-02 | Busca deve ignorar acentos e pontuação, e todas as palavras do query devem estar presentes no resultado | HomeViewModel.kt |
| BR-03 | Usuário não autenticado só pode usar armazenamento local (DataStore) | DefaultFavoritesRepository |
| BR-04 | Usuário autenticado usa armazenamento remoto (Firestore) com fallback local offline | DefaultFavoritesRepository |
| BR-05 | Ao fazer login, dados locais devem ser migrados para nuvem com merge union (favoritos) e preferência local (tema) | DataMigrationService |
| BR-06 | Conflitos de sincronização resolvidos por merge union (favoritos) e preferência local (tema) | BidirectionalSyncService |
| BR-07 | Sincronização automática ocorre quando conectividade é restaurada | ConnectivityMonitorService |
| BR-08 | Usuário só pode acessar seus próprios dados no Firestore (`allow read, write: if request.auth.uid == userId`) | firestore.rules |
| BR-09 | Tamanho de fonte ajustável entre 0.5x e 2.0x do padrão | HymnDetailScreen |
| BR-10 | Tema pode ser alterado sem login e persiste localmente | SettingsViewModel |
| BR-11 | Ao criar conta, estrutura de dados (perfil, settings, favorites) é criada automaticamente no Firestore via transação atômica | FirestoreUserRepositoryImpl |
| BR-12 | Login gera evento de analytics; logout também | FirebaseAnalyticsService |

### Product Rules

| # | Rule | Type |
|---|------|------|
| PR-01 | App deve abrir e funcionar imediatamente sem login | Decisão de produto |
| PR-02 | Hinos devem carregar instantaneamente (sem I/O, sem parsing) | Decisão de performance |
| PR-03 | Busca deve ser "humana" — perdoar erros do usuário | Decisão de UX |
| PR-04 | Temas devem refletir a filosofia do produto (imersão, personalização) | Decisão de produto |
| PR-05 | Favoritos devem funcionar offline e sincronizar automaticamente quando online | Decisão de UX |
| PR-06 | Transição entre autenticado/não autenticado deve ser automática e transparente | Decisão de UX |
| PR-07 | Splash screen deve ser cinematográfica e profissional | Decisão de branding |
| PR-08 | Analytics integrado para métricas de produto, sem expor PII | Decisão de operação |
| PR-09 | UI edge-to-edge com cores adaptáveis das barras de sistema | Decisão de imersão visual |
| PR-10 | Navegação com ícones dinâmicos (preenchido=ativo, outlined=inativo) | Decisão de UX |

### Strengths

1. Arquitetura limpa (Clean Architecture + MVVM + UDF) bem implementada e documentada
2. Performance excelente na operação principal (hinos em memória, busca com debounce)
3. Documentação extensa e de alta qualidade (24 arquivos em docs/)
4. Sistema de temas extensível e visualmente rico
5. Sincronização híbrida robusta com merge inteligente
6. Modo offline-first completo
7. Tratamento de estados (Loading, Error, Empty, Success) consistente
8. Injeção de dependência 100% Hilt
9. Analytics integrado
10. Código bem estruturado e comentado

### Limitations

1. **Ausência de testes** — cobertura próxima de 0% (apenas exemplos)
2. **Bus factor = 1** — único mantenedor sem CI build verificando PRs
3. **CI/CD inexistente** — apenas GitHub Release em tag; build manual
4. **Sem Crashlytics** — erros de produção invisíveis
5. **Sem feature flags** — rollout de funcionalidades é binário
6. **Processo de release totalmente manual** — depende de uma pessoa + keystore local
7. **Sincronização não incremental** — sempre full-sync
8. **Sem indicador de sincronização na UI** — usuário não sabe se dados estão sincronizados
9. **Sem WorkManager** — sync não roda em background de forma eficiente
10. **DiscoverScreen vazia** — tela de "descoberta" sem funcionalidade real (apenas placeholder)
11. **Violações de Clean Architecture** — `AuthRepository.kt`, `DataMigrationService`, `BidirectionalSyncService` importam UI layer
12. **Documentação obsoleta** — `Favoritos.md` não reflete implementação real
13. **Sem testes E2E em CI** — Maestro flows existem mas não rodam automatizados

### Gaps

| Gap | Description | Severity |
|-----|-------------|----------|
| G-01 | Cobertura de testes próxima de zero | Critical |
| G-02 | Sem CI build automatizado | Critical |
| G-03 | Sem crash reporting (Crashlytics) | High |
| G-04 | Sem feature flags / Remote Config | High |
| G-05 | DiscoverScreen é placeholder | Medium |
| G-06 | Sem categorização de hinos | Medium |
| G-07 | Sem histórico de visualização | Medium |
| G-08 | Sem player de áudio | Medium |
| G-09 | Sem contexto/história dos hinos | Medium |
| G-10 | Sem indicador de sincronização na UI | Low |
| G-11 | Sem compartilhamento de listas | Low |
| G-12 | Sem pesquisa avançada por tags/categorias | Low |
| G-13 | Sem suporte a múltiplos idiomas | Low |
| G-14 | Sem onboarding tutorial | Low |
| G-15 | Sem acessibilidade (WCAG) testada | Medium |
| G-16 | Sem testes de performance automatizados | Medium |

### Dependencies

| Dependency | Type | Risk |
|------------|------|------|
| Firebase Auth | External | Lock-in, depende de SDK estável |
| Firebase Firestore | External | Lock-in, custos de operação |
| Firebase Analytics | External | Lock-in |
| Google Sign-In API | External | API changes, deprecation risk |
| Jetpack Compose | Framework | Atualizações frequentes |
| Google Play Store | Distribution | Aprovação, políticas |
| Keystore (local) | Process | Single point of failure |
| Lucas de Lima (bus factor) | Human | Critical |

### Evident Opportunities

| Opportunity | Based on |
|-------------|----------|
| Adicionar testes automatizados | G-01 |
| CI/CD pipeline com validação | G-02 |
| Crashlytics + monitoramento | G-03 |
| Remote Config para rollout | G-04 |
| Categorização dos 640 hinos | Documentação extensa pronta |
| Player de áudio instrumental | Visão do produto |
| Contexto/história por hino | Pilar 3 (Contexto e Descoberta) |
| Listas de culto (temporárias) | Feature spec já documentada |
| Jornadas/trilhas temáticas | Diferenciação competitiva |
| Onboarding guiado | G-14 |

---

## 2. Feature Opportunities Catalog

### F-01: Testes Automatizados (Evolução)

**Problem/Opportunity:** Cobertura de testes próxima de zero. Qualquer mudança pode gerar regressão sem detecção.
**User affected:** Desenvolvedores (e indiretamente todos os usuários)
**Expected value:** Confiança para evoluir o código, prevenção de regressão, base para CI
**Proposed behavior:** Suite de testes unitários (ViewModels, Repositories, Services) + testes de integração + testes E2E Maestro em CI
**Evidence:** Documentação cita bugs arquiteturais corrigidos v1.2.0 que poderiam ser detectados por testes
**Dependencies:** CI pipeline
**Risks:** Tempo investido sem feature visível para usuário final
**Complexity:** Medium
**Classification:** Evolução

### F-02: CI/CD Pipeline Completo (Evolução)

**Problem/Opportunity:** Build e release 100% manual. Bus factor = 1 impede evolução segura.
**User affected:** Desenvolvedores, mantenedor
**Expected value:** Build automatizado, lint, testes, validação de PR, deploy automatizado na Play Store
**Proposed behavior:** GitHub Actions build + test + lint em PR; release pipeline com build AAB + upload Play Store
**Evidence:** Projeto já possui GitHub Actions workflow para tag push
**Dependencies:** Google Play Console access, service account
**Risks:** Manutenção do pipeline, custo de workflows
**Complexity:** High
**Classification:** Evolução

### F-03: Crashlytics + Observabilidade (Evolução)

**Problem/Opportunity:** Erros de produção são invisíveis. Sem Crashlytics, sem monitoramento.
**User affected:** Mantenedor, usuários finais (bugs não detectados)
**Expected value:** Visibilidade de crashes, ANRs, erros de sincronização, tempo de atividade
**Proposed behavior:** Integrar Firebase Crashlytics + Performance Monitoring
**Evidence:** Projeto já usa Firebase (Auth, Firestore, Analytics)
**Dependencies:** Firebase project config
**Risks:** Custo, volume de dados
**Complexity:** Low
**Classification:** Evolução

### F-04: Remote Config / Feature Flags (Evolução)

**Problem/Opportunity:** Rollout de features é binário. Sem kill switch, sem testes A/B, sem rollout gradual.
**User affected:** Operação, mantenedor
**Expected value:** Feature flags para rollout gradual, kill switch para emergências
**Proposed behavior:** Firebase Remote Config para habilitar/desabilitar features remotamente
**Evidence:** Prática padrão na indústria para apps em produção
**Dependencies:** Firebase project
**Risks:** Complexidade de configuração
**Complexity:** Low
**Classification:** Evolução

### F-05: Categorização dos 640 Hinos (Nova Capacidade)

**Problem/Opportunity:** DiscoverScreen é placeholder. Usuários não conseguem descobrir hinos por tema, momento espiritual ou intensidade.
**User affected:** Todos os usuários
**Expected value:** Transformar navegação em descoberta. Aumentar engajamento (hinos descobertos, tempo no app)
**Proposed behavior:** Implementar sistema de categorização com 20 categorias, 10 intensidades, 10 momentos espirituais. Filtros na tela de busca. IA para categorização automática.
**Evidence:** Documentação extensa já produzida (`Sistema_de_Categorizacao_de_Hinos.md`, `Proposta_Implementacao_Categorizacao_Hinos.md`)
**Dependencies:** Expansão do modelo Hymn, HymnDataSource
**Risks:** Qualidade da categorização, curadoria manual necessária
**Complexity:** High
**Classification:** Nova Capacidade

### F-06: Player de Áudio Instrumental (Nova Capacidade)

**Problem/Opportunity:** App é apenas leitor de letras. Usuários não podem ouvir melodias.
**User affected:** Todos os usuários
**Expected value:** Experiência de adoração mais imersiva, diferenciação de concorrentes
**Proposed behavior:** Player de áudio instrumental dos hinos (offline-first ou streaming), controles na HymnDetailScreen
**Evidence:** Visão documentada no Pilar 3 (Ecossistema do Hino)
**Dependencies:** Direitos autorais/licenciamento das melodias, armazenamento de áudio
**Risks:** Licenciamento, tamanho do app, performance
**Complexity:** Very High
**Classification:** Nova Frente

### F-07: Contexto e História dos Hinos (Nova Capacidade)

**Problem/Opportunity:** Cada hino tem história, autor, contexto teológico — mas o app não mostra nada disso.
**User affected:** Todos os usuários
**Expected value:** Transformar o ato de ler em ato de aprender. Aumentar tempo de uso e conexão emocional
**Proposed behavior:** Aba "Sobre" em cada hino com história do autor, contexto histórico, referências bíblicas
**Evidence:** Visão documentada no Pilar 3, DiscoverScreen já prepara para isso
**Dependencies:** Conteúdo editorial (curadoria), IA para geração
**Risks:** Qualidade e precisão teológica do conteúdo
**Complexity:** High
**Classification:** Nova Capacidade

### F-08: Listas de Culto (Novas Capacidade)

**Problem/Opportunity:** Usuários precisam organizar hinos para eventos/cultos sem poluir favoritos permanentes.
**User affected:** Usuários ativos que participam de cultos/ensaios
**Expected value:** Facilidade de organizar hinos por evento, com expiração automática
**Proposed behavior:** Listas temporárias (efêmeras) com expiração, local-only V1, abas separadas em FavoritesScreen
**Evidence:** Feature spec completa e detalhada (`Feature_-_Listas_de_Culto_V1.md`)
**Dependencies:** Proto DataStore, WorkManager para limpeza
**Risks:** Confusão UX entre favoritos e listas, timezone/clock drift
**Complexity:** Medium
**Classification:** Nova Capacidade

### F-09: Sincronização Incremental com WorkManager (Evolução)

**Problem/Opportunity:** Sincronização atual é full-sync. Para datasets maiores, será ineficiente.
**User affected:** Usuários autenticados com muitos dados
**Expected value:** Sincronização eficiente, menor uso de banda, atualizações periódicas
**Proposed behavior:** WorkManager para sync periódico em background, sincronização incremental de mudanças
**Evidence:** Documentado como próximo passo em `docs/11. Sistema de Migração e Sincronização de Dados.md`
**Dependencies:** Trabalho de engenharia nos serviços de sincronização
**Risks:** Complexidade de diff tracking
**Complexity:** Medium
**Classification:** Evolução

### F-10: Indicador de Sincronização na UI (Evolução)

**Problem/Opportunity:** Usuário não sabe se dados estão sincronizados.
**User affected:** Usuários autenticados
**Expected value:** Transparência e confiança no estado dos dados
**Proposed behavior:** Ícone/indicador visual mostrando status da sincronização (synced, syncing, offline, error)
**Evidence:** Documentado como próximo passo nas docs de sincronização
**Dependencies:** Serviços de sincronização existentes
**Risks:** Baixo
**Complexity:** Low
**Classification:** Evolução

### F-11: Onboarding Guiado (Nova Capacidade)

**Problem/Opportunity:** Primeiro uso pode ser desorientador. Usuário não sabe que pode favoritar, mudar temas ou fazer login.
**User affected:** Novos usuários
**Expected value:** Redução de churn no primeiro uso, aumento de ativação (login, favoritos, tema)
**Proposed behavior:** Tutorial interativo de 3-4 passos no primeiro uso, explicando busca, favoritos, temas, login
**Evidence:** App não tem onboarding — usuário descobre funcionalidades sozinho
**Dependencies:** SplashScreen existente
**Risks:** Usuários avançados podem achar intrusivo
**Complexity:** Low
**Classification:** Nova Capacidade

### F-12: Acessibilidade (Evolução)

**Problem/Opportunity:** Contraste de temas, tamanho de fonte, suporte a TalkBack não foram testados formalmente.
**User affected:** Usuários com deficiência visual, mobilidade reduzida
**Expected value:** App utilizável por todos, conformidade com WCAG
**Proposed behavior:** Auditoria de acessibilidade, testes com TalkBack, contraste mínimo validado em todos os temas
**Evidence:** Requisitos legais e de boas práticas para apps públicos
**Dependencies:** UI components audit
**Risks:** Esforço de adequação de temas existentes
**Complexity:** Medium
**Classification:** Evolução

### F-13: Jornadas/Trilhas Temáticas (Nova Capacidade)

**Problem/Opportunity:** Não há descoberta guiada. Usuário só navega por busca ou lista completa.
**User affected:** Todos os usuários
**Expected value:** Transformar navegação plana em jornada de descoberta. Aumentar tempo no app e conexão temática.
**Proposed behavior:** Trilhas temáticas (ex: "Hinos sobre Graça", "Canções de Consolo") com progressão sugerida
**Evidence:** Visão documentada no Pilar 3 (Contexto e Descoberta), DiscoverScreen placeholder menciona Jornadas de Louvor
**Dependencies:** Sistema de categorização (F-05)
**Risks:** Qualidade da curadoria das trilhas
**Complexity:** Medium
**Classification:** Nova Capacidade

### F-14: Personalização Avançada (Nova Capacidade)

**Problem/Opportunity:** Personalização limitada a tema e favoritos. Não há histórico, recomendações, ou perfis de uso.
**User affected:** Usuários frequentes
**Expected value:** Experiência adaptativa que aprende com o usuário
**Proposed behavior:** Histórico de visualização, hinos mais lidos, recomendações baseadas em favoritos, modo noturno automático
**Evidence:** Analytics já coleta dados de uso que poderiam alimentar personalização
**Dependencies:** Analytics data, categorização, storage expansion
**Risks:** Privacidade, complexidade de implementação de ML
**Complexity:** High
**Classification:** Nova Frente

### F-15: Compartilhamento Avançado (Evolução)

**Problem/Opportunity:** Compartilhamento atual é só de hino individual (texto). Não há compartilhamento de listas ou contexto.
**User affected:** Usuários sociais
**Expected value:** Compartilhar listas de culto, favoritos, hino com contexto
**Proposed behavior:** Deep links para hinos, compartilhar listas de culto, compartilhar devocional (hino + contexto)
**Evidence:** Já existe BottomSheet de compartilhamento — expandir é natural
**Dependencies:** Listas de culto (F-08), categorização (F-05)
**Risks:** Complexidade de deep linking
**Complexity:** Low
**Classification:** Evolução

---

## 3. Priorization

### Quick Win (Alto valor, baixo esforço)

| Feature | Justification |
|---------|---------------|
| F-03: Crashlytics | Já tem Firebase; integração rápida; valor crítico |
| F-04: Remote Config | Já tem Firebase; baixo esforço; habilita rollout seguro |
| F-10: Indicador de sincronização | Baixo esforço; alta transparência para usuário |
| F-11: Onboarding | Baixo esforço; impacto direto em retenção de novos usuários |
| F-15: Compartilhamento avançado | Expansão de funcionalidade existente |

### Incremental (Médio valor, médio esforço)

| Feature | Justification |
|---------|---------------|
| F-01: Testes automatizados | Fundação para evolução segura; custo sem retorno imediato |
| F-09: Sync incremental c/ WorkManager | Documentado como próximo passo natural |
| F-08: Listas de Culto | Feature spec completa; V1 local-only reduz complexidade |
| F-12: Acessibilidade | Requisito de conformidade; expande base de usuários |

### Strategic (Alto valor, alto esforço)

| Feature | Justification |
|---------|---------------|
| F-02: CI/CD pipeline | Remove bus factor; permite release seguro; fundamental para evolução |
| F-05: Categorização de hinos | Desbloqueia F-13 (Jornadas), F-14 (Personalização), transforma DiscoverScreen |
| F-07: Contexto/História dos hinos | Realiza o Pilar 3 da visão do produto |
| F-14: Personalização avançada | Diferenciação competitiva significativa |

### Exploratory (Valor incerto, requer validação)

| Feature | Justification |
|---------|---------------|
| F-06: Player de áudio | Alto risco de licenciamento; valor incerto; requer validação com usuários |
| F-13: Jornadas temáticas | Dependente de categorização; requer curadoria; valor depende de F-05 |

---

## 4. Summary

### Capabilities Identified: 18
1. Catálogo completo de hinos
2. Busca inteligente
3. Visualização de hinos com controle de fonte
4. Favoritos híbrido (local + nuvem)
5. 8 temas dinâmicos com fundos contextuais
6. Autenticação Google
7. Navegação completa (10+ telas, BottomNavBar)
8. Sincronização avançada (migração, bidirecional, monitoramento)
9. Analytics integrado
10. Splash screen cinematográfica
11. Modo offline completo
12. Persistência de preferências
13. 9 ViewModels
14. 7 interfaces de repositório
15. 9 implementações de repositório
16. 3 serviços de sincronização
17. Componentes reutilizáveis
18. Result Pattern

### Business Rules Identified: 12

### Product Rules Identified: 10

### Feature Opportunities: 15

### Main Fronts Discovered:
1. **Engineering Foundations** (F-01, F-02, F-03, F-04) — testes, CI/CD, observabilidade, feature flags
2. **Content Discovery** (F-05, F-07, F-13) — categorização, contexto, jornadas temáticas
3. **User Engagement** (F-08, F-11, F-15) — listas de culto, onboarding, compartilhamento
4. **Operational Excellence** (F-09, F-10, F-12) — sync incremental, indicadores, acessibilidade
5. **Product Evolution** (F-06, F-14) — áudio, personalização avançada

### Hypotheses Requiring Human Validation

| H | Hypothesis | Evidence |
|---|------------|----------|
| H-01 | Usuários querem descobrir hinos por tema/momento espiritual | Documentação extensa mas sem pesquisa com usuários |
| H-02 | Player de áudio instrumental aumentaria engajamento significativamente | Visão do produto, mas sem validação de mercado |
| H-03 | Listas de culto resolveriam necessidade real (não apenas desejável) | Feature spec detalhada, sem user research |
| H-04 | Onboarding reduziria churn no primeiro uso | Hipótese geral, não testada no contexto do Louve |
| H-05 | Usuários pagariam por versão sem anúncios ou por conteúdos exclusivos | Sem estudo de monetização |
| H-06 | A categorização IA seria precisa o suficiente sem curadoria humana extensa | Proposta técnica detalhada, sem validação de qualidade |
| H-07 | Existe demanda por versão com linguagem moderna da Harpa | Visão de longo prazo, sem pesquisa |
| H-08 | O formato "Jornadas/trilhas temáticas" aumentaria tempo de uso | Inspirado no BibleProject, sem validação direta |
| H-09 | O público-alvo se importa com edge-to-edge e imersão visual | Decisão de produto já implementada, não validada |
| H-10 | Usuários usariam múltiplos dispositivos para acessar favoritos | Funcionalidade implementada, uso não mensurado|