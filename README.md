# Louve App

**O Louve App** é um aplicativo Android moderno, open source, dedicado à Harpa Cristã, projetado para funcionar 24h em produção com alta estabilidade, performance e experiência de usuário. O projeto adota as melhores práticas de arquitetura, está pronto para evoluir para multiplataforma e é construído para ser referência em qualidade técnica e experiência de produto.

---

## ✨ Visão e Filosofia

O Louve App não é apenas um leitor de hinos. Ele é um ambiente digital de adoração, estudo e personalização, inspirado pela excelência do BibleProject. O objetivo é transformar a experiência com a Harpa Cristã em uma jornada de descoberta, imersão e conexão.

---

## 🚀 Principais Features (2025)

- **Lista Completa de Hinos**: 640 hinos da Harpa Cristã, carregados instantaneamente via código Kotlin gerado.
- **Busca Inteligente**: Pesquisa por título, número e conteúdo, tolerante a acentos e ordem das palavras.
- **Detalhe do Hino**: Visualização rica, ajuste de fonte, compartilhamento customizado.
- **Sistema de Temas Dinâmicos**: Escolha entre temas visuais imersivos, com fundos contextuais e experiência edge-to-edge.
- **Autenticação Google**: Login seguro, perfil persistente na nuvem (Firebase Auth + Firestore).
- **Persistência Local**: Preferências salvas com DataStore Preferences.
- **Splash Screen Cinematográfica**: Animação de abertura profissional.
- **Arquitetura Clean e Modular**: Pronta para KMP e expansão futura.

### **Em desenvolvimento / Futuro próximo**
- **Favoritos e Sincronização na Nuvem**
- **Remote Config para feature flags**
- **Rollout seguro e monitoramento avançado**
- **Migração gradual para multiplataforma (KMP)**
- **Ecossistema do Hino**: contexto histórico, player de áudio, referências bíblicas

---

## 🏗️ Arquitetura

- **Clean Architecture** (Presentation, Domain, Data)
- **MVVM + UDF** (StateFlow, ViewModels reativos)
- **Jetpack Compose (Material 3)**
- **Coroutines + Flow**
- **Hilt para DI**
- **Firebase Auth + Firestore**
- **DataStore Preferences**
- **Navigation Compose**
- **Splash Screen API**
- **Pronto para KMP (Domain em Kotlin puro)**

Veja [docs/3. Arquitetura de Software.MD](docs/3.%20Arquitetura%20de%20Software.MD) para detalhes técnicos e diagramas.

---

## 🔄 Fluxos Principais

- **Splash e Inicialização**: Animação e transição automática para Home.
- **Listagem e Busca de Hinos**: Busca avançada, UI reativa.
- **Detalhe do Hino**: Ajuste de fonte, compartilhamento, favoritos (em breve).
- **Temas Dinâmicos**: Escolha e persistência de tema.
- **Autenticação Google**: Login/logout, perfil na nuvem.
- **Navegação**: Entre Splash, Home, Detalhe, Configurações.
- **Compartilhamento**: Bottom sheet customizada, intent nativa.

Veja [docs/Mapa_de_Fluxos_e_Testes.md](docs/Mapa_de_Fluxos_e_Testes.md) para o mapeamento completo de fluxos e sugestões de testes.

---

## 📂 Estrutura de Pastas

```
app/
 └── src/main/java/com/lucasdelima/louveapp/
      ├── data/        # Implementação de repositórios, fontes de dados
      ├── domain/      # Modelos e interfaces de negócio (Kotlin puro)
      ├── ui/          # Apresentação: screens, navigation, theme
      ├── di/          # Injeção de dependências (Hilt)
      └── MainActivity.kt, MainViewModel.kt
```

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
- [Mapa de Fluxos e Testes](docs/Mapa_de_Fluxos_e_Testes.md)
- [Prompt Técnico Refinado](docs/Prompt_Revisado.md)

---

## 💡 Expectativas Futuras

- Evoluir para multiplataforma (KMP), mantendo domínio compartilhado
- Sincronização de favoritos e preferências na nuvem
- Ecossistema de hinos enriquecido (áudio, contexto, referências)
- Observabilidade e rollout avançados

---

## 📢 Contato

Para dúvidas, sugestões ou contribuições, abra uma issue ou entre em contato com os mantenedores do projeto.

---

**Louve App — Excelência, Imersão e Estabilidade em Adoração Digital.** 