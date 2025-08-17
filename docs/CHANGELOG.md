# Changelog - Louve Mobile App

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

## [Unreleased] - 2025

### Adicionado
- Sistema de barra de navegação inferior com 4 rotas principais
- Feature de ícones dinâmicos (preenchidos/vazios) para indicar rotas ativas
- Navegação entre telas Home, Favorites, Discover e More

### Alterado
- **Correção do bug visual do tema Sweet Candy**: Gradientes agora preenchem toda a tela corretamente
- **Eliminação da suavização de cores**: Cores dos temas agora são exibidas com intensidade natural
- **Implementação de edge-to-edge display**: Fundos dos temas agora se estendem até as bordas da tela

### Corrigido
- **Bug visual crítico**: Gradientes não preenchiam toda a tela após implementação da barra de navegação
- **Suavização de cores**: Problema onde cores apareciam "embranquecidas" nas telas Home e Favorites
- **Elemento visual estranho**: Fundo do tema aparecendo por trás da barra de navegação

---

## [2025-08-17] - Correção do Bug Visual e Implementação da Barra de Navegação

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
