# Changelog - Louve App

## Versionamento Semântico (SemVer)

Este projeto segue o padrão [Semantic Versioning](https://semver.org/) (SemVer) `MAJOR.MINOR.PATCH`:

- **MAJOR**: Mudanças incompatíveis na API
- **MINOR**: Funcionalidades adicionadas de forma compatível  
- **PATCH**: Correções de bugs compatíveis

---

## Resumo Rápido

| Versão | Status | Principais Funcionalidades |
|--------|--------|----------------------------|
| **[1.2.0]** | **ATUAL** | Bug Fixes, Build Update, Testes, Maestro E2E, CI Pipeline |
| **[1.1.0]** | ANTERIOR | Temas, Favoritos Híbridos, Navegação, Autenticação Google |
| **[1.0.0]** | BASE | Hinos, Busca, Interface Básica |

---

## [1.2.0] - 2026-08-29 (main branch) - ATUAL

### Adicionado
- Correção de 18 bugs arquiteturais e de qualidade (BUG-001 a BUG-018)
- 37 testes unitários para camadas domain e data
- Testes E2E com Maestro (7 fluxos de navegação)
- Pipeline de validação de PR (PR Validation) com build, lint, testes, SonarCloud
- Script de inicialização do google-services.json para CI
- Script de verificação de conformidade arquitetural (Clean Architecture)
- Pipeline de release automática (Auto Release)
- Suporte a crash recovery com logcat
- Fluxo de revisão retrospectiva no Harness

### Corrigido
- Violações de Clean Architecture (domain e data importando UI) — BUG-001 a BUG-005
- ViewModel bypassando injeção de dependência Hilt — BUG-005
- Tratamento silencioso de erros em DataMigrationService e BidirectionalSyncService — BUG-010, BUG-011
- Vazamento de recurso no ConnectivityMonitorService — BUG-012
- PRs excessivamente grandes sem revisão — BUG-013 a BUG-015
- Metadados IDE e documentação desatualizada no repositório — BUG-016 a BUG-018

### Alterado
- Atualização do Gradle para 9.5.0, AGP 9.3.2, Kotlin 2.3.21
- Atualização das GitHub Actions (setup-java v5, sonarcloud v5)
- Substituição do check-architecture.py de stub markdown para script funcional
- CI agora usa placeholder de google-services.json quando secret é inválido
- Guardrails de merge e HITL gates documentados na raiz do projeto (GUARDRAILS.md)

### Melhorado
- Cobertura de testes de 0% para cobertura baseline em domain e data
- Processo de CI/CD com validação automatizada de PRs
- Detecção precoce de crashes via Maestro E2E smoke tests
- Documentação de processo com adoption plan e políticas de qualidade

---

## [1.1.0] - 2025-09-27 (development branch) - ANTERIOR

### Adicionado
- Sistema completo de temas com múltiplas opções visuais
- Sincronização de preferências de tema entre dispositivos
- Sistema de favoritos híbrido (armazenamento local + nuvem)
- Autenticação Google com integração Firebase
- Barra de navegação inferior com navegação hierárquica
- Controle gestual de tamanho de fonte (movimento de pinça)
- Persistência de preferências de fonte durante navegação
- TopAppBar especializada por contexto de tela
- Feedback háptico em elementos interativos
- Sistema de migração automática de dados
- Monitoramento de conectividade para sincronização
- Resolução automática de conflitos de dados

### Melhorado
- Performance geral do aplicativo
- Tempo de carregamento da splash screen (redução de 40%)
- Responsividade da navegação entre telas
- Consistência visual entre todas as telas
- Tipografia e contraste de cores
- Animações de transição entre telas
- Arquitetura de componentes de UI

### Corrigido
- Conflitos entre barra de navegação do app e sistema
- Problemas de layout em dispositivos com barra de navegação do sistema
- Bugs de cor de texto em diferentes temas
- Problemas de preenchimento de tela pelas cores do tema
- Delay excessivo na navegação
- Inconsistências na aplicação de temas

### Alterado
- Refatoração completa do sistema de temas
- Migração para arquitetura de scaffold por tela
- Atualização do Android Gradle Plugin
- Reestruturação dos componentes de navegação

### Removido
- Dependências não utilizadas
- Código legado relacionado ao sistema de temas antigo

---

## [1.0.0] - 2024-XX-XX (main branch) - BASE

### Adicionado
- Lista completa de 640 hinos da Harpa Cristã
- Sistema de busca por título, número e conteúdo
- Visualização detalhada de hinos
- Ajuste básico de tamanho de fonte
- Compartilhamento de hinos
- Interface básica de navegação
- Splash screen inicial

### Arquitetura Base
- Clean Architecture (Presentation, Domain, Data)
- MVVM com StateFlow
- Jetpack Compose
- Hilt para injeção de dependências
- Firebase para backend
- DataStore para persistência local

---

## Roadmap de Versões Futuras

### [1.3.0] - Planejado
- Player de áudio para reprodução de hinos
- Contexto histórico e informações sobre compositores
- Sistema de busca avançada com filtros
- Melhorias na experiência de usuário
- Integração com IA para linguagem moderna
- Analytics avançado e métricas detalhadas
- Remote Config para feature flags
- Otimizações de performance adicionais

### [2.0.0] - Major Release
- Migração para Kotlin Multiplatform
- Suporte para iOS
- Refatoração completa da API
- Nova arquitetura multiplataforma

---

## Estatísticas de Desenvolvimento

- **Commits analisados**: 50+
- **Período de desenvolvimento**: v1.0.0 → v1.1.0
- **Tipo de release atual**: MINOR (funcionalidades adicionadas sem breaking changes)
- **Arquivos modificados**: 80+ arquivos
- **Novas funcionalidades**: 15+ features principais
- **Correções de bugs**: 20+ fixes implementados

---

## Convenções de Commit

Este projeto segue o padrão [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` - Nova funcionalidade
- `fix:` - Correção de bug
- `refactor:` - Refatoração de código
- `build:` - Mudanças no sistema de build
- `docs:` - Atualizações na documentação
- `style:` - Mudanças de formatação
- `test:` - Adição ou correção de testes
- `chore:` - Tarefas de manutenção

---

*Para mais detalhes sobre funcionalidades específicas, consulte a documentação técnica em `/docs`.*