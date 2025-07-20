# Prompt Revisado - Louve App

Estou desenvolvendo um app Android moderno (Louve App) que opera 24h em produção, utilizando os seguintes pilares arquiteturais:

## **Arquitetura Atual (Implementada):**
- **Clean Architecture** com camadas bem definidas (presentation, domain, data)
- **Jetpack Compose (Material 3)** como UI toolkit
- **MVVM + UDF** com StateFlow para reatividade
- **Coroutines + Flow** para concorrência e operações assíncronas
- **Hilt** para injeção de dependência
- **Firebase Auth + Firestore** para autenticação e dados na nuvem
- **DataStore Preferences** para persistência local
- **Navigation Compose** para navegação
- **Splash Screen API** para transições suaves

## **Preparação para Futuro (KMP Ready):**
- **Camada Domain** em Kotlin puro (pronta para KMP)
- **Repository Pattern** com interfaces agnósticas de plataforma
- **Estrutura modular** preparada para separação em features

## **Considerando esse cenário atual e a visão de futuro**, quero que você atue como **um colaborador técnico experiente** e me ajude a aprofundar as seguintes frentes:

### **1. Pontos Críticos de Atenção na Arquitetura Atual**
- Onde há riscos ou armadilhas comuns na operação 24h?
- Como garantir resiliência a falhas de rede, reinicializações e perda de energia?
- Quais são os gargalos de performance na busca de 640 hinos em memória?

### **2. Decisões Técnicas para Operação Contínua**
- Quando implementar WorkManager vs Foreground Services?
- Como otimizar o consumo de bateria mantendo funcionalidade?
- Estratégias para sincronização offline-first com Firestore

### **3. Preparação para Kotlin Multiplatform**
- Como estruturar a migração gradual para KMP?
- Quais módulos separar primeiro (features vs camadas)?
- Estratégias para compartilhar lógica mantendo performance

### **4. Testabilidade e Observabilidade**
- Como estruturar testes unitários e de integração para cada fluxo?
- Estratégias de monitoramento para apps que operam 24h
- Como instrumentar corretamente cada camada (UI, Domain, Data)

### **5. Melhorias de Qualidade e Estabilidade**
- Implementação de Remote Config para feature flags
- Estratégias de rollout e rollback seguros
- Monitoramento de crashes e performance em produção

**Organize a resposta por tópico, com explicações claras, sugestões objetivas e, quando útil, com snippets de código ou estrutura de pastas.**

**Evite sugestões genéricas — assuma que o time já conhece Android moderno profundamente e precisa apenas de direção refinada e precisão técnica para um app que precisa operar 24h com estabilidade.** 