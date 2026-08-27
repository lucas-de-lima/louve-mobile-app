# Louve Mobile App — Dedicated Harness Entry Point

Este repositório possui um **Dedicated Harness** que é a autoridade máxima para
decisões de engenharia neste projeto. Consulte os documentos abaixo antes de
qualquer alteração significativa.

## Documentos de Entrada

- [`.harness/README.md`](.harness/README.md) — Visão geral, operação e validação
- [`.harness/manifest.yaml`](.harness/manifest.yaml) — Skills, agentes, ferramentas e workflows habilitados
- [`.harness/vault/_index.md`](.harness/vault/_index.md) — Navegador do Vault (arquitetura, ADRs, políticas, domínio)

## Regras

1. **Autoridade**: O Dedicated Harness (`.harness/`) é a fonte primária de
   regras de engenharia, policies e arquitetura do projeto. Não duplique
   seu conteúdo em outros arquivos.
2. **Escopo**: Execute mudanças apenas dentro deste repositório. Não explore
   nem modifique projetos irmãos a menos que haja uma dependência técnica
   explícita e necessária.
3. **Executor**: O papel **Android Engineer** (`.harness/agents/android-engineer.md`)
   é o executor especializado para tarefas de implementação Kotlin/Android.
4. **Autossuficiência**: Este Harness é autossuficiente. Não depende do Base
   Harness como dependência runtime.