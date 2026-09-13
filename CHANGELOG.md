# Changelog - Louve Mobile App

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

## [1.3.1](https://github.com/lucas-de-lima/louve-mobile-app/compare/v1.3.0...v1.3.1) (2026-09-13)


### Corrigido

* revert release-type from android to simple with extra-files ([#297](https://github.com/lucas-de-lima/louve-mobile-app/issues/297)) ([791b265](https://github.com/lucas-de-lima/louve-mobile-app/commit/791b265b60ccb0e4ce75580ed02d9186e5860ceb))
* stop tracking non-Kotlin tooling artifacts (.kilo, .harness, scripts) ([#293](https://github.com/lucas-de-lima/louve-mobile-app/issues/293)) ([2754505](https://github.com/lucas-de-lima/louve-mobile-app/commit/2754505e3369c67af72bc5038ad2f1645bede005))

## [1.3.0](https://github.com/lucas-de-lima/louve-mobile-app/compare/v1.2.0...v1.3.0) (2026-09-13)


### Adicionado

* release-please migration + guardrail Release PR rule ([#290](https://github.com/lucas-de-lima/louve-mobile-app/issues/290)) ([66eee22](https://github.com/lucas-de-lima/louve-mobile-app/commit/66eee22a3963071dac94144329ce8f1b4b086b15))


### Corrigido

* add chmod +x gradlew to auto-release (Permission denied on ./gradlew) ([3a1e627](https://github.com/lucas-de-lima/louve-mobile-app/commit/3a1e627f313f99c7d9a716d2daf79d407b575c2a))
* add chmod +x gradlew to auto-release pipeline ([285c7f1](https://github.com/lucas-de-lima/louve-mobile-app/commit/285c7f138f063f9df3b5cc0eb023ddfeda52cfe6))
* add chmod +x gradlew to auto-release pipeline ([285c7f1](https://github.com/lucas-de-lima/louve-mobile-app/commit/285c7f138f063f9df3b5cc0eb023ddfeda52cfe6))
* use RELEASE_PLEASE_TOKEN instead of GITHUB_TOKEN for PR creation ([#291](https://github.com/lucas-de-lima/louve-mobile-app/issues/291)) ([ad0649c](https://github.com/lucas-de-lima/louve-mobile-app/commit/ad0649cd8d8aafe1a21a4b430929d94b2f7cf6f4))


### Documentação

* atualiza documentação do projeto — alinhamento com Epic 2 ([#270](https://github.com/lucas-de-lima/louve-mobile-app/issues/270)) ([255a604](https://github.com/lucas-de-lima/louve-mobile-app/commit/255a60483611d04bee04f93ad9e90fe91fcaef3d))


### CI/CD

* add gradle caching and timeout to auto-release ([#263](https://github.com/lucas-de-lima/louve-mobile-app/issues/263)) ([14c7ecc](https://github.com/lucas-de-lima/louve-mobile-app/commit/14c7ecc2830eb722a77629efceeaa84bde411ea7))
* remove PR validation gate (keep auto-release) ([0dfafb2](https://github.com/lucas-de-lima/louve-mobile-app/commit/0dfafb26e2628632a747d9a7140fec9f8464dfde))

## [1.2.0] - 2026-08-29

### Adicionado
- Correção de 18 bugs arquiteturais e de qualidade (BUG-001 a BUG-018)
- 37 testes unitários para camadas domain e data
- Testes E2E com Maestro (7 fluxos de navegação) com detecção de crashes
- Pipeline de validação de PR (build, lint, testes, SonarCloud, arquitetura)
- Pipeline de release automática com tag e GitHub Release
- Guardrails de merge e HITL gates (leia GUARDRAILS.md)

### Corrigido
- Violações de Clean Architecture (domain/data importando UI)
- ViewModel sem injeção de dependência Hilt
- Erros silenciosos em serviços de migração e sincronização
- Vazamento de recursos no ConnectivityMonitorService
- Build desatualizado (Gradle 9.5.0, AGP 9.3.2, Kotlin 2.3.21)

### Melhorado
- Cobertura de testes de 0% para baseline
- CI/CD com validação automatizada de PRs
- Documentação de processo e arquitetura

---

## [1.1.0] - 2025-09-27

### Adicionado
- Sistema completo de temas com múltiplas opções visuais

### 🔧 Correções Implementadas
- **Bug Visual do Tema Sweet Candy**: Corrigido problema de gradientes não preenchendo toda a tela
- **Suavização de Cores**: Eliminada camada intermediária que causava "embranquecimento" das cores
- **Elemento Visual Estranho**: Removida sobreposição entre fundo do tema e barra de navegação

### ✨ Novas Features Implementadas
- **Sistema de Barra de Navegação**: 4 rotas principais com navegação aninhada
- **Ícones Dinâmicos**: Preenchidos para rotas ativas, vazios para inativas
- **Otimização de Altura**: Barra compacta (84dp) com espaçamentos profissionais

### 🏗️ Mudanças na Arquitetura
- **Estrutura de Navegação**: Migração para sistema aninhado com barra inferior
- **Renderização de Fundos**: Fundos renderizados individualmente por tela
- **Componentização**: Novos componentes `LouveBottomNavBar` e `BottomNavItem`

### 📋 Checklist de Validação
- [x] Tema Sweet Candy preenche toda a tela
- [x] Cores exibidas com intensidade natural
- [x] Barra de navegação funcional e responsiva
- [x] Ícones dinâmicos funcionando corretamente
- [x] Navegação entre telas funcionando
- [x] Sem elementos visuais estranhos
- [x] Altura da barra otimizada
- [x] Espaçamentos ajustados corretamente

---

## [2024-12-18] - Implementação Inicial

### Adicionado
- Sistema base de temas (Default, Dark, Sweet Candy)
- Estrutura de navegação básica
- Telas Home, Favorites, Settings e Hymn Detail

### Alterado
- Migração para Jetpack Compose
- Implementação de Clean Architecture
- Sistema de injeção de dependência com Hilt

---

*Este changelog segue o padrão [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/).*
