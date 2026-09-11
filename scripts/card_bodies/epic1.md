## Contexto

Durante o Product Discovery, identificou-se que o app nao possui nenhum sistema de crash reporting (L04). Firebase Crashlytics nunca foi configurado, o que torna erros de producao completamente invisiveis. Bugs que afetam usuarios reais podem passar semanas sem deteccao, degradando a experiencia silenciosamente.

## Por que isso e critico

Sem visibilidade de erros, nao e possivel priorizar correcoes por impacto real. O mantenedor opera cego. Um crash que ocorre para 10% dos usuarios pode nunca ser descoberto.

## Resolucao

Integrar Firebase Crashlytics ao projeto, configurar inicializacao no LouveApp, e adicionar reporting de erros nao-fatais nos servicos de sincronizacao.