# Quality Gates — Louve App

## Purpose

Define evidence-based conditions that must be satisfied before work advances through the SDLC pipeline. These gates are enforced by the Dedicated Harness and checked during workflow transitions.

## Gate Catalog

### QG-001: Build Passes

| Field | Value |
|-------|-------|
| **Trigger** | Every PR, every merge to develop/main |
| **Command** | `./gradlew assembleDebug` |
| **Evidence** | Build output (success/failure) |
| **Failure** | Block merge |
| **Automation** | Phase 6 (CI Pipeline) |
| **Current status** | Manual only |

### QG-002: Tests Pass

| Field | Value |
|-------|-------|
| **Trigger** | Every PR, every merge to develop/main |
| **Command** | `./gradlew test` (unit) + `./gradlew connectedCheck` (instrumentation) |
| **Evidence** | Test report XML |
| **Coverage target** | Phase 4a: 40% → Phase 4b: 60% → Phase 5 steady: 80% (domain/data), ViewModels mandatory |
| **Failure** | Block merge |
| **Current status** | ❌ Not enforced (no tests exist) |

### QG-003: Architecture Compliance

| Field | Value |
|-------|-------|
| **Trigger** | Every PR |
| **Command** | `python .harness/scripts/check-architecture.py` (see below) |
| **Checks** | 1. `domain/` has no `android.*` imports. 2. `domain/` has no `ui.*` imports. 3. `data/` has no `ui.*` imports (exception: DI module). 4. ViewModels use `@HiltViewModel` constructor injection. |
| **Failure** | Flag for review, block merge |
| **Current status** | ❌ 5 violations active (BUG-001 to BUG-005) |

### QG-004: Lint Passes

| Field | Value |
|-------|-------|
| **Trigger** | Every PR |
| **Command** | `./gradlew lint` |
| **Evidence** | Lint report HTML |
| **Failure** | Warning → flag. Error → block merge. |
| **Current status** | ❌ Not automated in CI |

### QG-005: PR Size Check

| Field | Value |
|-------|-------|
| **Trigger** | Every PR creation/update |
| **Check** | Max 500 lines changed OR max 20 files |
| **Evidence** | GitHub API diff stats |
| **Failure** | Flag for review (non-blocking, advisory) |
| **Historical exceptions** | PR #4 (7,491 lines from HymnDataSource), PR #10 (80 files, 11,207 lines) |
| **Generated files rule** | Generated files (e.g. HymnDataSource) must be in a separate PR with clear `generated:` label |

### QG-006: Conventional Commit Check

| Field | Value |
|-------|-------|
| **Trigger** | Every PR |
| **Check** | All commits match `<type>(<scope>): <description>` pattern |
| **Types** | `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`, `build` |
| **Failure** | Flag for correction |
| **Current status** | ✅ Already followed in all historical PRs |

### QG-007: Changelog Updated

| Field | Value |
|-------|-------|
| **Trigger** | PR with `feat:` or `fix:` commits |
| **Check** | `CHANGELOG.md` or `CHANGELOG_DETALHADO.md` updated |
| **Failure** | Flag for author |
| **Current status** | ✅ Already followed |

### QG-009: Security Baseline

| Field | Value |
|-------|-------|
| **Trigger** | Every PR |
| **Checks** | 1. No secrets in diff (`.gitignore` patterns). 2. No `google-services.json`. 3. No `keystore.properties`. 4. No hardcoded API keys/tokens. |
| **Automation** | `git diff --check` + pattern scan |
| **Failure** | BLOCK MERGE immediately |
| **Current status** | ✅ Historically clean (no secrets found in PRs) |

## Implementation Status

| Gate | Phase | Current | Target |
|------|-------|---------|--------|
| QG-001 Build | Phase 6 | ✅ Verificado (`./gradlew assembleDebug` BUILD SUCCESSFUL em fix/bugs-aggregate) | CI |
| QG-002 Tests | Phase 5 | ✅ 37 tests (6 classes — auth, sync, search, ViewModel) | CI + coverage gate |
| QG-003 Architecture | Phase 5 | ✅ 0 violações (todas resolvidas, script passa) | Script+CI |
| QG-004 Lint | Phase 5 | ✅ CI (`pr-validation.yml`) | CI |
| QG-005 PR Size | Phase 3a | ✅ CI advisory + generated files rule | Script |
| QG-006 Conventional Commit | Phase 3b | ✅ Manual | CI |
| QG-007 Code Review | Phase 3b | ✅ **Removed** (solo dev — no reviewer available) | N/A |
| QG-008 Changelog | Phase 3b | ✅ Manual | Script |
| QG-009 Security | Phase 5 | ✅ Manual | CI+script |
| **QC-009 SonarCloud** | Phase 6 | ✅ Automatic status checks from SonarCloud analysis | Automatic |

## Architecture Compliance Script

```python
#!/usr/bin/env python3
"""check-architecture.py — Validates Clean Architecture layer rules."""

import os
import sys
import re

ROOT = "app/src/main/java/com/lucasdelima/louveapp"
LAYERS = ["domain", "data", "ui"]
VIOLATIONS = []

def check_imports(filepath, layer, forbidden_patterns):
    relpath = os.path.relpath(filepath, ROOT)
    with open(filepath) as f:
        for i, line in enumerate(f, 1):
            for pattern, desc in forbidden_patterns:
                if re.search(pattern, line):
                    VIOLATIONS.append(f"{relpath}:{i} — {desc}: {line.strip()}")

# Walk source tree
for root, dirs, files in os.walk(ROOT):
    for f in files:
        if not f.endswith(".kt"):
            continue
        path = os.path.join(root, f)
        # Determine layer
        rel = os.path.relpath(path, ROOT)
        parts = rel.split(os.sep)
        layer = parts[0] if parts[0] in LAYERS else None
        if not layer:
            continue
        
        if layer == "domain":
            check_imports(path, layer, [
                (r"^import android\.", "Android SDK import in domain"),
                (r"^import com\.lucasdelima\.louveapp\.ui\.", "UI layer import in domain"),
                (r"^import com\.lucasdelima\.louveapp\.data\.", "Data layer import in domain"),
            ])
        elif layer == "data":
            check_imports(path, layer, [
                (r"^import com\.lucasdelima\.louveapp\.ui\.", "UI layer import in data"),
            ])

if VIOLATIONS:
    print(f"❌ {len(VIOLATIONS)} architecture violation(s) found:")
    for v in VIOLATIONS:
        print(f"  {v}")
    sys.exit(1)
else:
    print("✅ Architecture compliance OK")
    sys.exit(0)
```

Save as `.harness/scripts/check-architecture.py`.