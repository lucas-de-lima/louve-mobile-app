# Louve App

**O Louve App** é um aplicativo Android moderno, open source, dedicado à Harpa Cristã, projetado para funcionar 24h em produção com alta estabilidade, performance e experiência de usuário. O projeto adota as melhores práticas de arquitetura, está pronto para evoluir para multiplataforma e é construído para ser referência em qualidade técnica e experiência de produto.

---

## ✨ Visão e Filosofia

O Louve App não é apenas um leitor de hinos. Ele é um ambiente digital de adoração, estudo e personalização, inspirado pela excelência do BibleProject. O objetivo é transformar a experiência com a Harpa Cristã em uma jornada de descoberta, imersão e conexão.

---

## 🚀 Principais Features (2025)

### **✅ Funcionalidades Implementadas**

- **📚 Lista Completa de Hinos**: 640 hinos da Harpa Cristã, carregados instantaneamente via código Kotlin gerado
- **🔍 Busca Inteligente**: Pesquisa por título, número e conteúdo, tolerante a acentos e ordem das palavras
- **📖 Detalhe do Hino**: Visualização rica, ajuste de fonte, compartilhamento customizado
- **⭐ Sistema de Favoritos Híbrido**: Marque hinos favoritos com sincronização automática entre dispositivos
- **🎨 Sistema de Temas Dinâmicos**: Escolha entre temas visuais imersivos, com fundos contextuais e experiência edge-to-edge
- **🔐 Autenticação Google**: Login seguro, perfil persistente na nuvem (Firebase Auth + Firestore)
- **💾 Persistência Inteligente**: Dados locais para usuários não logados, sincronização na nuvem para usuários logados
- **🎬 Splash Screen Cinematográfica**: Animação de abertura profissional
- **🧭 Navegação Completa**: 8+ telas com barra de navegação inferior e navegação hierárquica
- **📊 Analytics Integrado**: Firebase Analytics para insights de uso
- **🔄 Sincronização Avançada**: Migração automática de dados, resolução de conflitos, monitoramento de conectividade

### **🆕 Funcionalidades Adicionadas desde v1.0.0**

- **🎨 Sistema de Temas Completo**: Múltiplos temas com sincronização local e remota
- **⭐ Sistema de Favoritos Avançado**: Sincronização híbrida (local + Google Cloud)
- **🧭 Barra de Navegação Inferior**: Interface moderna com navegação intuitiva
- **👆 Controle Gestual de Fonte**: Movimento de pinça para ajustar tamanho da fonte
- **💾 Persistência de Preferências**: Tamanho da fonte salvo durante navegação
- **🎨 TopAppBar Especializada**: Barras superiores contextuais por tela
- **⚡ Splash Screen Otimizada**: 40% redução no tempo de carregamento
- **🎯 Feedback Háptico**: Efeitos táteis nos ícones da navegação
- **🔧 Performance Otimizada**: Melhorias gerais de velocidade e responsividade

> **📋 Versionamento:** Seguimos [Semantic Versioning](https://semver.org/) (SemVer). A versão atual é **v1.1.0** - uma versão MINOR com múltiplas funcionalidades adicionadas de forma compatível.

### **🏗️ Arquitetura Robusta**

- **Clean Architecture** (Presentation, Domain, Data)
- **MVVM + UDF** (StateFlow, 9 ViewModels reativos)
- **Jetpack Compose (Material 3)**
- **Coroutines + Flow**
- **Hilt para DI** (100% dos componentes)
- **Firebase Auth + Firestore**
- **DataStore Preferences**
- **Navigation Compose**
- **Splash Screen API**
- **Pronto para KMP** (Domain em Kotlin puro)

Veja [docs/3. Arquitetura de Software.MD](docs/3.%20Arquitetura%20de%20Software.MD) para detalhes técnicos e diagramas.

---

## 🔄 Fluxos Principais

- **🎬 Splash e Inicialização**: Animação cinematográfica e transição automática para Home
- **📚 Listagem e Busca de Hinos**: Busca avançada com debounce, UI reativa
- **📖 Detalhe do Hino**: Ajuste de fonte, compartilhamento, sistema de favoritos
- **⭐ Favoritos**: Lista dedicada, sincronização automática, migração de dados
- **🎨 Temas Dinâmicos**: Escolha e persistência de tema com preview visual
- **🔐 Autenticação Google**: Login/logout, perfil na nuvem, migração automática
- **🧭 Navegação Completa**: Entre 8+ telas com barra inferior e navegação hierárquica
- **📱 Perfil e Configurações**: Gerenciamento de conta, preferências, suporte
- **🔄 Sincronização**: Migração automática, resolução de conflitos, modo offline

Veja [docs/Mapa_de_Fluxos_e_Testes.md](docs/Mapa_de_Fluxos_e_Testes.md) para o mapeamento completo de fluxos e sugestões de testes.

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

## 🛠️ Como Contribuir

1. Leia o [docs/6. Guia de Contribuição e Padrões.MD](docs/6.%20Guia%20de%20Contribui%C3%A7%C3%A3o%20e%20Padr%C3%B5es.MD) para entender o fluxo de trabalho, padrões de código e convenções de commit.
2. Siga o [docs/7. Configuração do Ambiente de Desenvolvimento.MD](docs/7.%20Configura%C3%A7%C3%A3o%20do%20Ambiente%20de%20Desenvolvimento.MD) para rodar o projeto localmente.
3. Consulte o [docs/Mapa_de_Fluxos_e_Testes.md](docs/Mapa_de_Fluxos_e_Testes.md) para entender os fluxos e pontos de testes.
4. Abra issues ou pull requests sempre que possível. Nenhuma feature crítica é aceita sem revisão.

---

## 🔒 Segurança

- Segredos nunca são versionados. Use o `keystore.properties` local.
- Regras do Firestore versionadas e auditáveis ([firestore.rules](firestore.rules)).
- Veja [docs/8. Segurança e Lançamento.MD](docs/8.%20Seguran%C3%A7a%20e%20Lan%C3%A7amento.MD) para detalhes.

---

## 📚 Documentação Avançada

- [Visão e Filosofia do Produto](docs/1.%20Vis%C3%A3o%20e%20Filosofia%20do%20Produto.md)
- [Estado Atual e Roadmap](docs/2.%20Estado%20Atual%20e%20Roadmap%20do%20Projeto.MD)
- [Arquitetura de Software](docs/3.%20Arquitetura%20de%20Software.MD)
- [Guia de UI e Theming](docs/4.%20Guia%20de%20UI%20e%20Theming.MD)
- [Camada de Dados e Backend](docs/5.%20Camada%20de%20Dados%20e%20Backend.MD)
- [Guia de Contribuição e Padrões](docs/6.%20Guia%20de%20Contribui%C3%A7%C3%A3o%20e%20Padr%C3%B5es.MD)
- [Configuração do Ambiente](docs/7.%20Configura%C3%A7%C3%A3o%20do%20Ambiente%20de%20Desenvolvimento.MD)
- [Segurança e Lançamento](docs/8.%20Seguran%C3%A7a%20e%20Lan%C3%A7amento.MD)
- [Sistema de Navegação](docs/9.%20Sistema%20de%20Navegação%20e%20Barra%20Inferior.MD)
- [Sistema de Autenticação](docs/10.%20Sistema%20de%20Autenticação%20e%20Persistência%20de%20Dados.MD)
- [Sistema de Migração](docs/11.%20Sistema%20de%20Migração%20e%20Sincronização%20de%20Dados.md)
- [Funcionalidades Implementadas](docs/13.%20Funcionalidades%20Implementadas%20-%20Estado%20Atual.md)
- [Mapa de Fluxos e Testes](docs/Mapa_de_Fluxos_e_Testes.md)

### **📋 Changelog**
- [Changelog Completo](CHANGELOG_DETALHADO.md) - Histórico detalhado seguindo Semantic Versioning (SemVer)

---

## 💡 Expectativas Futuras

- **🎵 Ecossistema do Hino**: contexto histórico, player de áudio, referências bíblicas
- **🔄 Remote Config**: feature flags para rollout seguro
- **📊 Observabilidade**: monitoramento avançado e métricas
- **🌐 Multiplataforma**: evolução para KMP mantendo domínio compartilhado
- **🤖 IA Integrada**: Harpa com linguagem moderna e atualizada

---

## 📢 Contato

Para dúvidas, sugestões ou contribuições, abra uma issue ou entre em contato diretamente com o mantenedor principal:

**Lucas de Lima**  
📧 [dev.lucasdelima@gmail.com](mailto:dev.lucasdelima@gmail.com)  
🔗 [linkedin.com/in/dev-lucasdelima](https://www.linkedin.com/in/dev-lucasdelima/)

---

**Louve App — Excelência, Imersão e Estabilidade em Adoração Digital.** 
