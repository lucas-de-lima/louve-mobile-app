# Architecture Compliance Check Script

## Purpose

Automated validation that Clean Architecture layer rules are respected. Checks that:
- `domain/` has no Android SDK imports
- `domain/` has no imports from `ui/` or `data/`
- `data/` has no imports from `ui/`

## Usage

```bash
python .harness/scripts/check-architecture.py
```

Returns exit code 0 if clean, 1 if violations found.

## Integration

This script is called by:
- QG-003 (Architecture Compliance) — PR gate
- Pre-commit hook (optional)
- CI pipeline (Phase 6)

## Current Status

5 violations active — see BUG-001 to BUG-005 in `.harness/workflows/bugs-registry.md`