#!/usr/bin/env python3
"""Clean Architecture compliance checker.
Checks that layer rules are respected in Kotlin source files.
Returns exit code 0 if clean, 1 if violations found.
"""

import os
import re
import sys

PROJECT_ROOT = os.path.join(os.path.dirname(__file__), "..", "..", "app", "src", "main")
SOURCE_DIR = os.path.join(PROJECT_ROOT, "java")

# Patterns to detect Android SDK / framework imports
ANDROID_IMPORTS_RE = re.compile(r"^import\s+(android|androidx|com\.google\.firebase)\b")
LOCAL_IMPORT_RE = re.compile(r"^import\s+com\.lucasdelima\.louveapp\.(\w+)\.(.+)")


def find_kotlin_files(root):
    for dirpath, _, filenames in os.walk(root):
        for f in filenames:
            if f.endswith(".kt"):
                yield os.path.join(dirpath, f)


def check_file(filepath):
    relpath = os.path.relpath(filepath, SOURCE_DIR)
    parts = relpath.replace("\\", "/").split("/")
    layer = parts[0] if parts else ""

    violations = []
    with open(filepath, "r", encoding="utf-8") as f:
        for lineno, line in enumerate(f, 1):
            stripped = line.strip()

            # Rule 1: domain/ must not import Android SDK
            if layer == "domain":
                if ANDROID_IMPORTS_RE.match(stripped):
                    violations.append(
                        f"  {relpath}:{lineno} — domain imports Android SDK: {stripped}"
                    )

            # Rule 2: domain/ must not import from ui/ or data/
            if layer == "domain":
                m = LOCAL_IMPORT_RE.match(stripped)
                if m and m.group(1) in ("ui", "data"):
                    violations.append(
                        f"  {relpath}:{lineno} — domain imports from {m.group(1)}: {stripped}"
                    )

            # Rule 3: data/ must not import from ui/
            if layer == "data":
                m = LOCAL_IMPORT_RE.match(stripped)
                if m and m.group(1) == "ui":
                    violations.append(
                        f"  {relpath}:{lineno} — data imports from ui: {stripped}"
                    )

    return violations


def main():
    if not os.path.isdir(SOURCE_DIR):
        print(f"Source directory not found: {SOURCE_DIR}")
        print("Skipping architecture compliance check.")
        sys.exit(0)

    all_violations = []
    for kt_file in find_kotlin_files(SOURCE_DIR):
        all_violations.extend(check_file(kt_file))

    if all_violations:
        print("Architecture compliance violations found:")
        for v in all_violations:
            print(v)
        print(f"\nTotal: {len(all_violations)} violation(s)")
        sys.exit(1)
    else:
        print("Clean Architecture: all layer rules respected.")
        sys.exit(0)


if __name__ == "__main__":
    main()