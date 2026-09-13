#!/usr/bin/env python3
"""Structural contract tests for LLM-native squad routing.

The LLM makes semantic choices at runtime, so this suite deliberately does
not recreate that judgement with a second heuristic classifier. It validates
the context and evaluation fixtures the LLM needs to make the choices.

Run: python .kilo/squads/test_llm_native_routing.py
"""

import os
import re
import sys


BASE = os.path.dirname(os.path.abspath(__file__))
REGISTRY = os.path.join(BASE, "registry.yaml")
CATALOG = os.path.join(BASE, "CATALOG.md")
ROUTE_COMMAND = os.path.join(BASE, "..", "command", "route.md")
EVALUATION = os.path.join(BASE, "ROUTING_EVALUATION.md")

EXPECTED_SQUADS = {
    "squad-product", "squad-android", "squad-compose",
    "squad-compose-performance", "squad-design-ux",
    "squad-engineering-practices", "squad-gcp-core", "squad-gcp-data",
    "squad-gke", "squad-cloud-ai", "squad-agent-engineering", "squad-adtech",
}

# Acceptance fixtures: a human or an LLM-enabled host evaluates the semantic
# result. The script checks that their squad and representative skills exist
# in the catalog/registry; it never pretends to infer intent itself.
SCENARIOS = [
    ("feature discovery", "squad-product", "discovery-process"),
    ("Android implementation using Room", "squad-android", "android-data-layer"),
    ("Compose UI implementation", "squad-compose", "compose-ui"),
    ("Compose performance diagnosis", "squad-compose-performance", "debugging-recompositions"),
    ("GKE deployment", "squad-gke", "gke-manifest-generation"),
    ("favorites-list discovery", "squad-product", "problem-statement"),
]


def registry_owners():
    text = open(REGISTRY, encoding="utf-8").read()
    entries = re.split(r"(?m)^  - id: ", text)[1:]
    owners = {}
    for entry in entries:
        lines = entry.splitlines()
        skill_id = lines[0].strip().strip('"')
        owner = next((m.group(1) for line in lines[1:]
                      if (m := re.match(r'^    owner: "(.+)"$', line))), None)
        owners[skill_id] = owner
    return owners


def run():
    failures = []
    owners = registry_owners()
    catalog = open(CATALOG, encoding="utf-8").read()
    route = open(ROUTE_COMMAND, encoding="utf-8").read()
    evaluation = open(EVALUATION, encoding="utf-8").read()

    if len(owners) != 314:
        failures.append("registry must contain 314 skills, got %d" % len(owners))
    if set(owners.values()) != EXPECTED_SQUADS:
        failures.append("registry owners do not cover exactly the 12 squads")

    for squad in sorted(EXPECTED_SQUADS):
        manifest = os.path.join(BASE, squad, "SQUAD.yaml")
        if not os.path.isfile(manifest):
            failures.append("missing manifest: %s" % squad)
            continue
        text = open(manifest, encoding="utf-8").read()
        for field in ("mission:", "domains:", "capabilities:", "routing_signals:",
                      "selection_guidance:", "select_when:", "do_not_select_when:",
                      "preferred_skills:", "delegates_to:", "out_of_scope:"):
            if field not in text:
                failures.append("%s lacks %s" % (squad, field))
        if ("## " + squad + " ") not in catalog:
            failures.append("catalog lacks %s" % squad)

    forbidden = ("router.py", "python .kilo/squads", "0.55", "ranking de squads")
    for term in forbidden:
        if term in route.lower():
            failures.append("/route still depends on legacy concept: %s" % term)
    for term in ("CATALOG.md", "reavalie", "antes de considerar **como**", "transição sequencial"):
        if term not in route:
            failures.append("/route lacks LLM-native instruction: %s" % term)

    for label, squad, skill in SCENARIOS:
        if squad not in EXPECTED_SQUADS:
            failures.append("scenario %s names unknown squad %s" % (label, squad))
        if skill not in owners:
            failures.append("scenario %s names unknown skill %s" % (label, skill))
        if squad not in evaluation or skill not in evaluation:
            failures.append("evaluation fixture missing details for %s" % label)

    for old_file in ("router.py", "test_router.py", "test_work_stage.py", "test_adversarial.py"):
        if os.path.exists(os.path.join(BASE, old_file)):
            failures.append("legacy heuristic artifact remains: %s" % old_file)
    cache_dir = os.path.join(BASE, "__pycache__")
    for old_cache in ("router.cpython-314.pyc", "test_router.cpython-314.pyc"):
        if os.path.exists(os.path.join(cache_dir, old_cache)):
            failures.append("legacy heuristic bytecode remains: %s" % old_cache)

    if failures:
        print("LLM-NATIVE ROUTING CONTRACT: FAILED")
        for failure in failures:
            print("- " + failure)
        return 1
    print("LLM-NATIVE ROUTING CONTRACT: PASSED")
    print("Validated 12 squad contexts, 314 owned skills, the /route contract, and 6 acceptance fixtures.")
    return 0


if __name__ == "__main__":
    sys.exit(run())
