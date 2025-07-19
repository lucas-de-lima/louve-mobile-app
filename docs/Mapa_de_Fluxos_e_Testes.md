# Mapa de Fluxos e Possíveis Testes - Louve App

Este documento mapeia os fluxos principais do Louve App e sugere pontos de testes unitários e de integração para cada um. Serve como referência para cobertura de testes futura.

---

## 1. Fluxo de Splash e Inicialização
- **Descrição:** Animação de splash, transição automática para Home.
- **Testes sugeridos:**
  - Integração: Splash exibe animação e navega para Home após o tempo correto.
  - Integração: Splash não trava o app em caso de erro.

---

## 2. Fluxo de Listagem e Busca de Hinos
- **Descrição:** Exibe lista de hinos, permite busca por nome/número/conteúdo, busca tolerante a acentos e ordem das palavras.
- **Testes sugeridos:**
  - Unitário: Função de normalização e filtragem de busca (HomeViewModel).
  - Unitário: Estado inicial do HomeUiState.
  - Integração: Busca exibe resultados corretos na UI.
  - Integração: Clique em item navega para detalhes.

---

## 3. Fluxo de Detalhe do Hino
- **Descrição:** Exibe detalhes do hino, permite ajuste de fonte, compartilhar e (futuramente) favoritar.
- **Testes sugeridos:**
  - Unitário: Carregamento de hino por ID (HymnDetailViewModel).
  - Unitário: Manipulação do estado de erro e loading.
  - Unitário: Funções de aumentar/diminuir fonte.
  - Integração: Compartilhamento gera texto correto e dispara intent.
  - Integração: Placeholder de favoritos exibe snackbar.

---

## 4. Fluxo de Temas Dinâmicos
- **Descrição:** Usuário pode escolher tema, preferência é persistida localmente (DataStore), UI reflete tema selecionado.
- **Testes sugeridos:**
  - Unitário: Persistência e leitura de tema (SettingsRepository).
  - Unitário: Estado do SettingsViewModel ao trocar tema.
  - Integração: Seleção de tema reflete na UI imediatamente.

---

## 5. Fluxo de Autenticação (Login/Logout)
- **Descrição:** Login com Google, integração com Firebase Auth, persistência de perfil no Firestore, logout.
- **Testes sugeridos:**
  - Unitário: Chamada de signIn/signOut no AuthViewModel.
  - Unitário: Manipulação de estados de usuário (userProfile).
  - Integração: Login bem-sucedido atualiza UI e Firestore.
  - Integração: Logout limpa estado e UI.
  - Integração: Tratamento de erro de login.

---

## 6. Fluxo de Navegação
- **Descrição:** Navegação entre Splash, Home, Detalhe, Configurações.
- **Testes sugeridos:**
  - Integração: Navegação correta entre telas via NavGraph.
  - Integração: Parâmetros de navegação (ex: id do hino) são passados corretamente.

---

## 7. Compartilhamento de Hino
- **Descrição:** Usuário pode compartilhar hino a partir da tela de detalhes, com pré-visualização customizada.
- **Testes sugeridos:**
  - Unitário: Geração do texto de compartilhamento.
  - Integração: Clique em compartilhar exibe bottom sheet e dispara intent.

---

## 8. (Futuro) Favoritos e Sincronização na Nuvem
- **Descrição:** Usuário poderá favoritar hinos, favoritos serão sincronizados com Firestore.
- **Testes sugeridos:**
  - Unitário: Manipulação de favoritos no ViewModel e Repository.
  - Integração: Favoritar/desfavoritar reflete na UI e backend.

---

## Observações Gerais
- Testes unitários devem focar em lógica de ViewModel, Repository e funções utilitárias.
- Testes de integração devem cobrir navegação, persistência, UI e integração com serviços externos (Firebase, DataStore).
- Cobertura de erros e estados de loading é fundamental para resiliência.

---

**Este documento deve ser atualizado sempre que novos fluxos ou features forem adicionados ao app.** 