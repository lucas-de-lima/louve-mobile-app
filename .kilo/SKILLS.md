# Agentic Skill Squads

This project has 314 skills organized into 12 Squads. The LLM is the agent; a Squad is the operational context it adopts for the current work. Skills are resources discovered after that role is selected.

## Recommended path

```text
User request
  → /route
  → LLM understands problem, desired outcome, and work stage
  → Squad Catalog
  → primary Squad adoption and self-validation
  → scoped Skill Registry discovery
  → execution
```

Use `/route <request>` when the role is not explicit. It directs the LLM to compare the 12 Squad manifests, choose the role best suited to what the user is trying to accomplish, adopt that context, and then discover relevant skills within the selected Squad.

Technology does not decide the role by itself. A request to evaluate whether a new Android feature should exist is product discovery; a request to implement it with Room is Android engineering.

## Explicit roles and direct skills

`/squad-*` commands remain available when the user explicitly chooses a role — for example, `/squad-android` means “adopt Android Engineering context.” Direct skill invocation also remains supported.

When work actually changes specialty, use one primary Squad at a time and transition sequentially. Do not model every Squad as a parallel agent.

## Sources of truth

| Layer | Path | Purpose |
|---|---|---|
| Squad Catalog | `.kilo/squads/CATALOG.md` | Compact LLM-readable selection context for all 12 Squads. |
| Squad manifests | `.kilo/squads/squad-*/SQUAD.yaml` | Mission, scope, capabilities, selection guidance, preferred skills, and transitions. |
| Skill Registry | `.kilo/squads/registry.yaml` | 314 skills with one primary owner and optional consumers/related metadata. |
| Route command | `.kilo/command/route.md` | Operational instructions for LLM-native selection and adoption. |
| Acceptance rubric | `.kilo/squads/ROUTING_EVALUATION.md` | Scenarios for validation on an LLM-enabled host. |

## Validation

```powershell
python .kilo/squads/validate.py
python .kilo/squads/test_llm_native_routing.py
```

These scripts validate the registry and LLM-native routing contract. They intentionally do not simulate semantic intent selection with Python heuristics.
