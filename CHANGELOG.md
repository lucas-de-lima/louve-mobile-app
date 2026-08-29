# Changelog - Louve Mobile App

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

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
