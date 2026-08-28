# Dedicated Harness — Louve Mobile App

## Status

Generated from Base Harness v0.32.0 — Phase 31 (Real Project Discovery).

## Purpose

This Dedicated Harness adds an Agentic SDLC layer to the Louve Mobile App
without disrupting the existing production system.

## How this differs from Base Harness

| Aspect | Base | Dedicated |
|--------|------|-----------|
| Implementation | Generic engineering capabilities | Android/Kotlin specialization |
| Architecture | Generic catalog | Clean Architecture selected |
| CI/CD | Generic pipe | Manual release (preserved) |
| Branching | Policy | Git Flow adopted (preserved) |

## Operation

Run `harnessctl` from the project root:

```sh
harnessctl --help
```

### Validation

```sh
make validate
```

## HITL

This harness inherits the Base HITL policy. See `.harness/hitl/`.