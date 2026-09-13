# LLM-native routing evaluation

This is an acceptance rubric for a host that can execute `/route` with an LLM. It is deliberately not a Python classifier test: reproducing semantic role selection with aliases, weights, or scores would recreate the architecture that was removed.

For each scenario, invoke `/route <request>`, inspect the selected Squad and its context, and verify that the response names a primary role, gives a short qualitative reason, and selects skills only after adopting the Squad. No numeric confidence is expected.

## Core selection

| Request | Expected current stage | Primary Squad | Representative skills after adoption |
|---|---|---|---|
| "Quero descobrir se devemos criar múltiplas listas de favoritos." | discovery | `squad-product` | `discovery-process`, `problem-statement`, `jobs-to-be-done` as appropriate |
| "Implemente múltiplas listas usando Room." | implementation | `squad-android` | `android-data-layer`, `android-architecture` |
| "Implemente a tela dessas listas em Compose." | implementation | `squad-compose` | `compose-ui`, `compose-expert` |
| "A tela de listas está com jank e recomposição excessiva." | debugging/optimization | `squad-compose-performance` | `debugging-recompositions`, `diagnosing-compose-stability` |
| "Publique o workload no GKE." | deployment | `squad-gke` | `gke-manifest-generation`, `gke-productionize` |

## Stage transition

For the same favorites-list initiative, verify this sequential transition:

1. Discovery → `squad-product`.
2. PRD/user stories → remain `squad-product` and choose the planning lane.
3. Experience design → transition to `squad-design-ux`.
4. Room implementation → transition to `squad-android`.
5. Compose screen implementation → transition to `squad-compose` only when the UI work becomes primary.
6. Tests/review → adopt `squad-engineering-practices` only when that cross-cutting work becomes primary.

## Ambiguity and cross-domain checks

- A request that merely says "melhore isso" must prompt for clarification after catalog comparison; it must not invent a score.
- "Design system em Compose" should compare `squad-design-ux` and `squad-compose`, then choose according to the current objective (define the system vs implement a UI).
- "Agente Gemini no GKE" should start in `squad-cloud-ai` when the model/product work is primary and transition to `squad-gke` for deployment infrastructure.
- A request containing Android terminology but asking whether a feature should exist must remain in `squad-product` during discovery.

## Real case: multiple favorites lists

Request:

> Quero poder adicionar uma nova lista de favoritos ao APP. Atualmente existe apenas uma forma de salvar favoritos e uma única lista. Quero permitir ao usuário novas listas temporárias de 24 horas, recorrentes, personalizáveis e para cultos.

Expected result:

```yaml
selected_squad:
  id: squad-product
current_stage: discovery
reason: The request describes a user problem and possible product behavior; it does not yet ask for an Android implementation.
skill_discovery:
  candidates: [discovery-process, problem-statement, jobs-to-be-done]
next_transition: Move to squad-android only when implementation decisions are requested.
```
