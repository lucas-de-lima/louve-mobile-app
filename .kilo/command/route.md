---
description: Faz o LLM selecionar e adotar o Squad adequado. Uso: /route <sua tarefa em linguagem natural>.
---
# Route

Leia a solicitação completa. Você é o agente; um Squad é o contexto operacional que você adota, não outro agente a ser chamado.

1. Entenda o problema/objeto, o resultado desejado e o estágio atual do trabalho.
2. Leia `.kilo/squads/CATALOG.md` e compare mission, domains, capabilities, selection_guidance, out_of_scope, preferred_skills e delegates_to dos manifestos candidatos.
3. Escolha o Squad cujo papel melhor atende ao **que** o usuário quer realizar antes de considerar **como** a tecnologia será usada.
4. Carregue `.kilo/squads/<selected-squad>/SQUAD.yaml` e reavalie: este papel ainda é o mais adequado? Se não, volte ao catálogo e selecione outro.
5. Com o Squad adotado, consulte `.kilo/squads/registry.yaml` apenas no contexto desse Squad: skills de sua ownership e, quando necessário, skills que ele consome. Selecione as skills pelo significado da tarefa e leia seus `SKILL.md`.
6. Execute a tarefa. Se ela mudar de especialização, escolha um novo Squad primário e faça uma transição sequencial.

Não use scores, thresholds, aliases, keyword matching ou ranking global de skills. Não exponha as 314 skills antes de restringir o contexto pelo Squad.

Quando a ambiguidade impedir uma escolha responsável após comparar os manifestos, explique a ambiguidade e peça esclarecimento. Não invente uma confiança numérica.

Os comandos `/squad-*` continuam sendo a forma explícita de escolher um papel; `/route` é a entrada para deixar o LLM selecioná-lo.
