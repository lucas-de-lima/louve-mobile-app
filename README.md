# Louve App

**O Louve App** é um aplicativo Android moderno, open source, dedicado à Harpa Cristã, projetado para funcionar 24h em produção com alta estabilidade, performance e experiência de usuário. O projeto adota as melhores práticas de arquitetura, está pronto para evoluir para multiplataforma e é construído para ser referência em qualidade técnica e experiência de produto.

---

## ✨ Visão e Filosofia

O Louve App não é apenas um leitor de hinos. Ele é um ambiente digital de adoração, estudo e personalização, inspirado pela excelência do BibleProject. O objetivo é transformar a experiência com a Harpa Cristã em uma jornada de descoberta, imersão e conexão.

---

## 🚀 Principais Features (2025)

### **✅ Funcionalidades Implementadas**

- **📚 Lista de Hinos**: 54 hinos da Harpa Cristã implementados (dos 640 totais), carregados via código Kotlin em memória
- **🔍 Busca Inteligente**: Pesquisa por título, número e conteúdo, tolerante a acentos e ordem das palavras
- **📖 Detalhe do Hino**: Visualização rica, ajuste de fonte, compartilhamento customizado
- **⭐ Sistema de Favoritos Híbrido**: Marque hinos favoritos com sincronização automática entre dispositivos
- **🎨 Sistema de Temas Dinâmicos**: Escolha entre temas visuais imersivos, com fundos contextuais e experiência edge-to-edge
- **🔐 Autenticação Google**: Login seguro, perfil persistente na nuvem (Firebase Auth + Firestore)
- **💾 Persistência Inteligente**: Dados locais para usuários não logados, sincronização na nuvem para usuários logados
- **🎬 Splash Screen Cinematográfica**: Animação de abertura profissional
- **🧭 Navegação Completa**: 8+ telas com barra de navegação inferior e navegação hierárquica
- **📊 Analytics Integrado**: Firebase Analytics para insights de uso
- **🔄 Sincronização Parcial**: Migração automática de dados ao fazer login (DataMigrationService); sync bidirecional e resolução de conflitos são stubs (ver Epic 11)

### **🆕 Funcionalidades Adicionadas desde v1.0.0**

- **🎨 Sistema de Temas Completo**: Múltiplos temas com persistência local e remota
- **⭐ Sistema de Favoritos**: Favoritos com persistência local + tentativa remota
- **🧭 Barra de Navegação Inferior**: Interface moderna com navegação intuitiva
- **👆 Controle Gestual de Fonte**: Movimento de pinça para ajustar tamanho da fonte
- **💾 Persistência de Preferências**: Tamanho da fonte salvo durante navegação
- **🎨 TopAppBar Especializada**: Barras superiores contextuais por tela
- **⚡ Splash Screen**: Animação de abertura cinematográfica
- **🔧 Performance Otimizada**: Melhorias gerais de velocidade e responsividade

> **📋 Versionamento:** Seguimos [Semantic Versioning](https://semver.org/) (SemVer). A versão atual é **v1.2.0**.

### **🏗️ Arquitetura**

- **Clean Architecture** (Presentation, Domain, Data) — com violações conhecidas (ver ADR-006)
- **MVVM + StateFlow** (9 ViewModels)
- **Jetpack Compose (Material 3)**
- **Coroutines + Flow**
- **Hilt para DI** (100% dos componentes)
- **Firebase Auth + Firestore**
- **DataStore Preferences**
- **Navigation Compose**
- **Splash Screen API**

> ⚠️ **Nota de arquitetura:** Não há camada de Use Cases. `AuthUiState` no domain contém callback de UI (`retry`). Sync services parcialmente stubs. Consulte [ADR-006](docs/ADRS.md) e [Epic 4](https://github.com/lucas-de-lima/louve-mobile-app/issues/116).

---

## 🔄 Fluxos Principais

- **🎬 Splash e Inicialização**: Animação cinematográfica e transição automática para Home
- **📚 Listagem e Busca de Hinos**: Busca avançada com debounce, UI reativa
- **📖 Detalhe do Hino**: Ajuste de fonte, compartilhamento, sistema de favoritos
- **⭐ Favoritos**: Lista dedicada, persistência local, tentativa remota
- **🎨 Temas Dinâmicos**: Escolha e persistência de tema com preview visual
- **🔐 Autenticação Google**: Login/logout, perfil na nuvem, migração automática
- **🧭 Navegação Completa**: Entre 8+ telas com barra inferior e navegação hierárquica
- **📱 Perfil e Configurações**: Gerenciamento de conta, preferências, suporte
- **🔄 Sincronização**: Migração ao login (DataMigrationService); sync bidirecional pendente (Epic 11)

---

## 📂 Estrutura de Pastas

```
app/
 └── src/main/java/com/lucasdelima/louveapp/
      ├── data/        # 9 implementações de repositórios, fontes de dados
      ├── domain/      # 7 interfaces e modelos de negócio (Kotlin puro)
      ├── ui/          # 8+ telas, navegação, temas, componentes
      ├── di/          # Injeção de dependências (Hilt)
      └── MainActivity.kt, MainViewModel.kt
```

---

## 🎯 Telas Implementadas

### **Telas Principais**
- **🎬 SplashScreen**: Animação cinematográfica com silhuetas e efeitos
- **🏠 HomeScreen**: Lista de hinos com busca em tempo real
- **⭐ FavoritesScreen**: Lista de hinos favoritos com estado vazio
- **🔍 DiscoverScreen**: Preview de funcionalidades futuras
- **⚙️ MoreScreen**: Ações rápidas e informações do usuário

### **Telas de Detalhes**
- **📖 HymnDetailScreen**: Visualização completa com controles de fonte
- **👤 ProfileScreen**: Perfil do usuário com estatísticas
- **⚙️ SettingsScreen**: Configurações e seleção de temas
- **ℹ️ AboutScreen**: Informações do app e filosofia
- **🆘 SupportScreen**: Formulário de suporte completo

---

## 🛠️ Scripts de Automação

Para facilitar o desenvolvimento, foram criados scripts para automatizar tarefas comuns:

- **Build com Logs**: `./scripts/build_app.sh`
  - Compila a aplicação e salva logs detalhados em `build.log`.
- **Execução com Logcat**: `./scripts/run_app.sh`
  - Instala, inicia o app e captura os logs do Logcat filtrados pelo PID da aplicação em tempo real, salvando também em `app.log`.

*Nota: Certifique-se de ter o `adb` configurado no seu PATH e um dispositivo/emulador conectado.*

---

## 🛠️ Como Contribuir

1. Leia o [GUARDRAILS.md](GUARDRAILS.md) para entender os processos de merge/release.
2. Consulte o [Mapa de Fluxos e Testes](docs/Mapa_de_Fluxos_e_Testes.md) para entender os fluxos.
3. Abra issues ou pull requests sempre que possível. Nenhuma feature crítica é aceita sem revisão.

---

## 🔒 Segurança

- Segredos nunca são versionados. Use o `keystore.properties` local.
- Regras do Firestore versionadas e auditáveis ([firestore.rules](firestore.rules)).

---

## 📚 Documentação

- [Visão e Filosofia do Produto](docs/1.%20Vis%C3%A3o%20e%20Filosofia%20do%20Produto.md)
- [Guia de UI e Theming](docs/4.%20Guia%20de%20UI%20e%20Theming%20-%20NOVA.md)
- [THEMING_DOCS.md](docs/THEMING_DOCS.md)
- [Funcionalidades Implementadas](docs/13.%20Funcionalidades%20Implementadas%20-%20Estado%20Atual.md)
- [Mapa de Fluxos e Testes](docs/Mapa_de_Fluxos_e_Testes.md)
- [Sistema de Migração](docs/11.%20Sistema%20de%20Migra%C3%A7%C3%A3o%20e%20Sincroniza%C3%A7%C3%A3o%20de%20Dados.md)
- [Favoritos](docs/Favoritos.md)
- [Play Store Descriptions](docs/PLAY_STORE_DESCRIPTIONS.md)
- [Política de Privacidade](docs/POLITICA_DE_PRIVACIDADE.md)

### **📋 Changelog**
- [Changelog Detalhado](CHANGELOG_DETALHADO.md) - Histórico detalhado seguindo Semantic Versioning (SemVer)

---

## 💡 Expectativas Futuras

- **🎵 Ecossistema do Hino**: contexto histórico, player de áudio, referências bíblicas
- **🔄 Remote Config**: feature flags para rollout seguro
- **📊 Observabilidade**: monitoramento avançado e métricas
- **🌐 Multiplataforma**: evolução para KMP mantendo domínio compartilhado
- **🤖 IA Integrada**: Harpa com linguagem moderna e atualizada

---

## 🧪 Testes E2E com Maestro

Testes end-to-end são implementados com [**Maestro**](https://maestro.mobile.dev/), framework YAML declarativo que detecta crashes e ANRs automaticamente.

### Pré-requisitos

- Java 17+ (`JAVA_HOME` configurado)
- Maestro CLI instalado em `C:\maestro\bin` ([download](https://github.com/mobile-dev-inc/maestro/releases/latest/download/maestro.zip))
- Emulador Android rodando

### Estrutura dos flows

```
.maestro/
├── config.yaml                        # Config global (recording, tags)
├── 00_splash_and_home.yaml           # Splash → Home
├── 01_bottom_nav_tabs.yaml           # Navegação entre abas
├── 02_settings_flow.yaml             # Mais → Config → Voltar
├── 03_profile_flow.yaml              # Mais → Perfil → Voltar
├── 04_hymn_detail_flow.yaml          # Home → Hino → Voltar
├── 05_about_and_support_flow.yaml    # Mais → Sobre → Ajuda → Voltar
└── 06_search_flow.yaml               # Busca de hinos
```

### Como executar

```powershell
.\scripts\run_maestro_tests.ps1
```

O script:
1. Verifica se Maestro e emulador estão disponíveis
2. Executa todos os flows sequencialmente
3. Coleta logs do Logcat
4. Gera relatórios JUnit em `maestro_reports/`

### Executar um flow individual

```powershell
maestro test .maestro/00_splash_and_home.yaml
```

### Detecção de crashes

Maestro detecta crashes e ANRs automaticamente. Se um crash ocorrer:
- O flow falha imediatamente
- Logcat é capturado nos relatórios
- Screenshots são salvas no diretório de teste

---

Para dúvidas, sugestões ou contribuições, abra uma issue ou entre em contato diretamente com o mantenedor principal:

**Lucas de Lima**  
📧 [dev.lucasdelima@gmail.com](mailto:dev.lucasdelima@gmail.com)  
🔗 [linkedin.com/in/dev-lucasdelima](https://www.linkedin.com/in/dev-lucasdelima/)

---

**Louve App — Excelência, Imersão e Estabilidade em Adoração Digital.** 
