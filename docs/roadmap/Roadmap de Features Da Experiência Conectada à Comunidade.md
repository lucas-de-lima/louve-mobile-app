Roadmap de Features: Da Experiência Conectada à Comunidade
Este documento resume os próximos épicos de desenvolvimento para o Louve App, organizados por prioridade. Cada tópico representa uma feature ou um pilar técnico que será detalhado em documentação específica.

ÉPICO 1: Fundações da Experiência Conectada (Prioridade Alta)
Objetivo: Reestruturar a navegação para o futuro e construir as funcionalidades centrais de perfil, configurações e suporte, estabelecendo a base técnica para o crescimento do app.

1.1. Reestruturação da Navegação Principal

Tarefa: Mover a lista de hinos da tela principal para uma nova aba "Harpa" (ícone de livro aberto) na barra de navegação inferior.

Telas Envolvidas: HomeScreen (temporariamente se torna a HymnListScreen), BottomNavBar.

Nota: A HomeScreen como um "Hub" será implementada em um épico futuro para garantir que seja lançada com valor real para o usuário.

1.2. Implementação da Tela "Mais" e Sub-Navegação

Tarefa: Criar a nova aba "Mais" (ícone de menu) que servirá como um centro de controle para o usuário.

Conteúdo: Atalho para Perfil, Configurações, Compartilhar App, Sobre e Ajuda.

Ação: Mover o atalho de "Configurações" do topo da lista de hinos para esta tela.

1.3. Criação da Tela de Perfil do Usuário

Tarefa: Desenvolver a tela de perfil acessada a partir da tela "Mais".

Features:

Exibição de foto, nome e e-mail (da conta Google).

CTA (Call to Action) para login se o usuário estiver deslogado.

Botão de "Sair" (Logout).

Botão para "Excluir Conta" (requisito da Google Play).

1.4. Sistema de Ajuda e Report de Bug

Tarefa: Implementar um fluxo de suporte robusto e de alta qualidade.

Arquitetura:

Frontend: Implementar o gesto "Shake-to-Report" (Agitar para Reportar) que captura a tela atual.

UI: Formulário de ajuda com dados pré-preenchidos e anexo da captura de tela.

Backend: Utilizar uma Cloud Function para receber os dados do formulário e enviar um e-mail formatado para o suporte, desacoplando o app do cliente de e-mail.

Armazenamento: Otimizar e salvar a imagem do anexo no Firebase Storage (não no Firestore).

1.5. Fundação Técnica: Camada de Analytics

Tarefa: Criar um AnalyticsService abstrato para centralizar todos os eventos de tracking.

Objetivo: Desacoplar a lógica de negócio do SDK do Firebase, garantir consistência nos eventos e facilitar testes. Este é um pré-requisito técnico para os épicos futuros.

1.6. Telas de Conteúdo Estático

Tarefa: Criar as telas "Sobre" e "Descubra" (Mockup).

"Sobre": Incluir a visão do app, versão e links para Política de Privacidade/Licenças.

"Descubra": Criar a estrutura da tela (ViewModel, Rota) com um mockup inspirador sobre o futuro da feature.

ÉPICO 2: Engajamento e Personalização (Prioridade Média)
Objetivo: Introduzir mecânicas de gamificação saudável e a primeira lógica de backend ativa para enriquecer a experiência do usuário e criar hábitos de uso positivos.

2.1. Gamificação Pessoal: Streaks e Marcos

Tarefa: Implementar o sistema de sequência de acesso diário (streaks).

Backend: Lógica no Firestore para armazenar lastLoginDate e loginStreak.

UI: Exibir a contagem de dias na futura tela de Perfil ou Hub.

Visão Futura: Evoluir para um sistema de "Marcos" e "Distintivos" que celebram o progresso pessoal em vez de competição.

2.2. Backend Ativo v1: Agregação de Dados

Tarefa: Implementar a lógica para calcular os "hinos mais cantados".

Arquitetura:

Coleta: Enviar eventos hymn_viewed para o Firebase Analytics.

Processamento: Configurar a exportação para o BigQuery.

Agregação: Criar uma Cloud Function agendada para processar os dados e calcular os "Top Hinos".

Publicação: Salvar o resultado em um documento público no Firestore para leitura eficiente pelo app.

ÉPICO 3: O Hub da Comunidade e Descoberta (Prioridade Baixa/Visão de Futuro)
Objetivo: Lançar a nova HomeScreen como um verdadeiro hub de boas-vindas e iniciar a implementação das features de descoberta de conteúdo.

3.1. Implementação do Hub de Boas-Vindas

Tarefa: Construir a UI e o ViewModel para a nova tela principal.

Features Iniciais:

Saudação personalizada.

Atalho "Continue de Onde Parou".

Card "Hino do Dia" (controlado via Remote Config).

Exibição dos "Hinos Mais Cantados" (dados do Épico 2.2).

Cards de Ação Rápida ("Surpreenda-me!", busca por número).

3.2. Implementação da Tela "Descubra" v1

Tarefa: Substituir o mockup por funcionalidades reais.

Visão: Começar com "Jornadas de Louvor" (listas de hinos curadas por tema, ex: "Hinos sobre a Graça") e evoluir para a visão completa do "Ecossistema do Hino".