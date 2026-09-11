# LLM-native Agentic Skill Routing

## Architecture before

`request → Python tokenization/aliases → work-stage heuristic → weighted squad score → confidence threshold → weighted global skill ranking → result`

The old `router.py` performed semantic interpretation in code. Its 900-line implementation contained aliases, keyword matching, noise penalties, stage detection, confidence bands, score thresholds, and skill ranking.

## Architecture after

`request → /route → LLM understands objective and stage → Squad Catalog → primary Squad adoption → self-validation → scoped Registry discovery → relevant skills → execution`

The LLM is the agent. A Squad is operational context that it adopts; it is not a spawned agent or distributed process. The preferred model is one primary Squad at a time, with a sequential context transition only when the work changes specialization.

## Responsibility boundary

| Keep in infrastructure | LLM responsibility |
|---|---|
| Squad Catalog and manifests | understand the request |
| Registry, ownership, consumers, related metadata | select and self-validate the role |
| direct `/squad-*` commands and direct skill invocation | infer work stage and skill relevance |
| generation and structural validation | choose a primary Squad and transition when needed |

## Migration disposition

| Former component | Disposition | Reason |
|---|---|---|
| `registry.yaml` | KEEP | Structural source of truth for 314 skills, owners, consumers, and metadata. |
| 12 Squad manifests | REFACTOR | Add concise selection guidance so a catalog supports role selection without skill-level knowledge. |
| `/squad-*` commands | REFACTOR | Make explicit Squad adoption clear and remove language that implies calling another agent. |
| `/route` command | REFACTOR | Convert from a Python-classifier entry point into LLM operational instructions. |
| `build_squads.py` | REFACTOR | Generate the catalog, context guidance, and LLM-native commands. |
| `router.py` | REMOVE | All responsibility was heuristic semantic classification and global skill ranking. |
| scorer/stage/adversarial tests | REMOVE | They asserted behavior of the deleted heuristic router. |
| `test_llm_native_routing.py` and `ROUTING_EVALUATION.md` | KEEP | Structural contract plus transparent LLM-host acceptance rubric. |

## Mandatory routing-code re-evaluation

| Component | Classification | Justification |
|---|---|---|
| `route.md`, `CATALOG.md`, and `SQUAD.yaml` | ESSENTIAL | They are the runtime routing mechanism: Markdown/YAML context for LLM reasoning, adoption, self-validation, and scoped discovery. |
| `registry.yaml` | ESSENTIAL | It is the deterministic structural source of skill ownership, consumers, and metadata; it makes scoped discovery possible without deciding relevance. |
| `build_squads.py` | OPTIONAL | It regenerates deterministic catalog/registry artifacts. It is not invoked by `/route` and must not select a Squad or skill for a request. |
| `validate.py` and `test_llm_native_routing.py` | OPTIONAL | They verify invariants and the routing contract; they do not interpret a user request. |
| Legacy routing test names and compiled artifacts | DEPRECATED | Their only purpose was testing the removed score engine; they are not part of the new contract. |
| `router.py`, its scorer, aliases, confidence bands, work-stage detector, and global skill ranker | REMOVE | These duplicated LLM semantic reasoning and have been removed. |

There is no Python runtime on the `/route` execution path. Python's remaining role is mechanical generation and deterministic integrity validation only.

## Compatibility

Direct skill invocation remains available. `/squad-*` remains the explicit choice to adopt a particular role. `/route` is now the recommended route for LLM-selected context.
