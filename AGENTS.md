# Louve Mobile App — Dedicated Harness Entry Point

## ⚠️ GUARDRAIL OBRIGATÓRIO — LEIA ANTES DE QUALQUER AÇÃO

**Todo agente DEVE ler [GUARDRAILS.md](./GUARDRAILS.md) na raiz do projeto antes de executar qualquer operação de merge, PR, branch, release, deploy, CI/CD, ou alteração de infraestrutura.**

> Regra inviolável: nenhum merge ou release sem autorização humana explícita.
> Violar os HITL gates quebra a confiança do processo.

Checkpoints obrigatórios:
1. A tarefa envolve merge/PR/branch/release/deploy? → **PARE, pergunte ao humano**
2. A tarefa altera CI/CD, secrets, ou proteção de branch? → **PARE, pergunte ao humano**
3. A tarefa modifica guardrails ou AGENTS.md? → **PARE, pergunte ao humano**

*Instituído em 2026-08-29. Inviolável sem HG-DESTRUCTIVE.*

---

Este repositório possui um **Dedicated Harness** que é a autoridade máxima para
decisões de engenharia neste projeto. Consulte os documentos abaixo antes de
qualquer alteração significativa.

## Documentos de Entrada

- [`GUARDRAILS.md`](./GUARDRAILS.md) — ⚠️ LEITURA OBRIGATÓRIA antes de merge/PR/release
- [`.harness/README.md`](.harness/README.md) — Visão geral, operação e validação
- [`.harness/manifest.yaml`](.harness/manifest.yaml) — Skills, agentes, ferramentas e workflows habilitados
- [`.harness/vault/_index.md`](.harness/vault/_index.md) — Navegador do Vault (arquitetura, ADRs, políticas, domínio)

## Regras

1. **GUARDRAIL**: O arquivo [`GUARDRAILS.md`](./GUARDRAILS.md) é inviolável. Nenhum merge, PR, branch, release, deploy, CI/CD, ou alteração de infraestrutura sem autorização humana explícita.
2. **Autoridade**: O Dedicated Harness (`.harness/`) é a fonte primária de
   regras de engenharia, policies e arquitetura do projeto. Não duplique
   seu conteúdo em outros arquivos.
3. **Escopo**: Execute mudanças apenas dentro deste repositório. Não explore
   nem modifique projetos irmãos a menos que haja uma dependência técnica
   explícita e necessária.
4. **Executor**: O papel **Android Engineer** (`.harness/agents/android-engineer.md`)
   é o executor especializado para tarefas de implementação Kotlin/Android.
5. **Autossuficiência**: Este Harness é autossuficiente. Não depende do Base
   Harness como dependência runtime.