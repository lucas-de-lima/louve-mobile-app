#!/usr/bin/env python3
"""Final validation for the Agentic Skill Squads implementation.

Checks the closed-form invariants required by Prompt 3 section 23:
  TOTAL SKILLS == 314
  TOTAL OWNERS == 314
  UNASSIGNED == 0
  DOUBLE ASSIGN == 0
  TOTAL SQUADS == 12
  REGISTRY ENTRIES == 314
  SQUAD ENTRIES == 12
  LLM-native routing contract passes

Run: python .kilo/squads/validate.py
"""

import os
import sys

BASE = os.path.dirname(os.path.abspath(__file__))
SKILLS_DIR = os.path.join(BASE, "..", "skills")
REGISTRY = os.path.join(BASE, "registry.yaml")


def main():
    ok = True

    # 1. Skills on disk
    skills = [d for d in os.listdir(SKILLS_DIR)
              if os.path.isfile(os.path.join(SKILLS_DIR, d, "SKILL.md"))]
    print("TOTAL SKILLS     : %d (expected 314)" % len(skills))
    ok &= (len(skills) == 314)

    # 2. Squads on disk
    squads = [d for d in os.listdir(BASE)
              if d.startswith("squad-") and os.path.isfile(os.path.join(BASE, d, "SQUAD.yaml"))]
    print("TOTAL SQUADS     : %d (expected 12)" % len(squads))
    ok &= (len(squads) == 12)

    # 3. Registry analysis (strict subset we generate)
    import re
    reg_text = open(REGISTRY, encoding="utf-8").read()
    m = re.search(r"^skills:\s*\n(.*)$", reg_text, re.DOTALL | re.MULTILINE)
    body = m.group(1)
    entries = re.split(r"(?m)^  - id: ", body)[1:]
    print("REGISTRY ENTRIES : %d (expected 314)" % len(entries))
    ok &= (len(entries) == 314)

    sid_owner = {}
    double = []
    unassigned = []
    for chunk in entries:
        lines = chunk.splitlines()
        sid = lines[0].replace('"', "").strip()
        owner = ""
        for line in lines[1:]:
            mm = re.match(r"^    owner: \"(.*)\"$", line)
            if mm:
                owner = mm.group(1)
                break
        if not owner:
            unassigned.append(sid)
        elif sid in sid_owner:
            double.append("%s (duplicated registry entry)" % sid)
        else:
            sid_owner[sid] = owner

    print("TOTAL OWNERS     : %d (expected 314)" % len(sid_owner))
    ok &= (len(sid_owner) == 314)
    print("UNASSIGNED       : %d (expected 0)  %s" % (len(unassigned), unassigned[:5]))
    ok &= (len(unassigned) == 0)
    print("DOUBLE ASSIGN    : %d (expected 0)  %s" % (len(double), double[:5]))
    ok &= (len(double) == 0)

    # owners distribution
    from collections import Counter
    dist = Counter(sid_owner.values())
    print("\nOwners distribution:")
    for squad in sorted(dist):
        print("  %-28s %d" % (squad, dist[squad]))

    # skill ids in registry must match skills on disk
    on_disk = set(skills)
    missing_on_disk = [s for s in sid_owner if s not in on_disk]
    print("\nREGISTRY IDS ON DISK: %d missing (expected 0) %s" % (len(missing_on_disk), missing_on_disk[:5]))
    ok &= (len(missing_on_disk) == 0)

    # 4. LLM-native routing contract. Semantic selection is deliberately
    # evaluated on an LLM-enabled host using ROUTING_EVALUATION.md; this test
    # validates that the catalog, manifests, registry, and /route contract
    # needed for that evaluation remain intact.
    sys.path.insert(0, BASE)
    import test_llm_native_routing
    print("\nLLM ROUTING CONTRACT: running...")
    code = test_llm_native_routing.run()
    ok &= (code == 0)

    print("\n" + "=" * 60)
    if ok:
        print("VALIDATION: ALL INVARIANTS HOLD (314/314)")
    else:
        print("VALIDATION: DIVERGENCE FOUND - INVESTIGATE")
    print("=" * 60)
    return 0 if ok else 1


if __name__ == "__main__":
    sys.exit(main())
