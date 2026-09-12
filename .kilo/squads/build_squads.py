#!/usr/bin/env python3
"""Build the Agentic Skill Squads layer from the installed skills.

Reads every SKILL.md under .kilo/skills/, derives metadata conservatively
from the real content (frontmatter description + author keywords), and
generates:

  .kilo/squads/registry.yaml                    (single source of truth, 314)
  .kilo/squads/squad-<id>/SQUAD.yaml            (12 squad manifests)
  .kilo/command/squad-<id>.md                   (12 squad commands)
  .kilo/squads/CATALOG.md                       (LLM-readable squad catalog)
  .kilo/command/route.md                        (LLM-native routing command)

It NEVER modifies, moves or deletes any skill. The registry is the metadata
source; skills keep working exactly as before.
"""

import io
import os
import re
import sys

PROJECT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
SKILLS_DIR = os.path.join(PROJECT, ".kilo", "skills")
OUT_DIR = os.path.join(PROJECT, ".kilo", "squads")
CMD_DIR = os.path.join(PROJECT, ".kilo", "command")


# ---------------------------------------------------------------------------
# Verified skill -> squad assignment (314 skills, 12 squads, no duplicates)
# ---------------------------------------------------------------------------
SQUAD_SKILLS = {
    "squad-android": [
        "agp-9-upgrade", "android-architecture", "android-cli",
        "android-coroutines", "android-data-layer", "android-emulator-skill",
        "android-gradle-logic", "android-intent-security", "android-profiler",
        "android-retrofit", "android-testing", "android-viewmodel",
        "appfunctions", "camerax", "developer-device-platform-basics",
        "engage-sdk-integration", "gradle-build-performance",
        "kotlin-concurrency-expert", "kotlin-tooling-native-build-performance",
        "media3-cast-integration", "play-billing-library-version-upgrade",
        "play-policy-insights", "r8-analyzer", "restore-credentials",
        "rxjava-to-coroutines-migration", "verified-email", "testing-setup",
    ],
    "squad-compose": [
        "adaptive", "android-accessibility", "coil-compose", "compose-expert",
        "compose-navigation", "compose-ui",
        "display-glasses-with-jetpack-compose-glimmer", "edge-to-edge",
        "leanback-to-compose-tv-migration", "migrate-xml-views-to-jetpack-compose",
        "navigation-3", "styles", "wear-compose-m3", "xml-to-compose-migration",
    ],
    "squad-compose-performance": [
        "auditing-compose-performance", "avoiding-subcomposition-pitfalls",
        "choosing-derivedstateof", "collecting-flows-safely",
        "compose-performance-audit", "configuring-lazy-prefetch",
        "configuring-r8-for-compose", "debugging-recompositions",
        "deferring-state-reads", "diagnosing-compose-stability",
        "enforcing-stability-in-ci", "generating-baseline-profiles",
        "iterating-with-ai-and-mcp", "migrating-to-modifier-node",
        "optimizing-lazy-layouts", "ordering-modifier-chains",
        "preserving-state-across-reloads", "setting-up-compose-hotswan",
        "stabilizing-compose-types", "testing-compose-in-release-mode",
        "tracing-recompositions-at-runtime", "understanding-hot-reload-limits",
        "understanding-stability-inference", "using-efficient-effects",
        "using-stability-analyzer-ide-plugin", "using-strong-skipping-correctly",
        "visualizing-recomposition-cascades",
    ],
    "squad-design-ux": [
        "a11y-audit", "apply-aesthetic", "brandkit", "design-code",
        "design-component", "design-qa", "design-review", "design-tokens",
        "figma-integration", "frontend-ui-engineering", "governance",
        "image-to-code", "migrate-design-system", "mobile-app-ui-design",
        "performance", "prototype", "redesign", "token-build", "ux-writing",
    ],
    "squad-product": [
        "acquisition-channel-advisor", "ai-shaped-readiness-advisor",
        "altitude-horizon-framework", "ansoff-matrix",
        "autonomous-investigation", "battle-card-builder",
        "business-health-diagnostic", "company-intel", "company-research",
        "competitive-analysis-process", "competitive-intel-watch",
        "competitive-research-snapshot", "customer-journey-map",
        "customer-journey-mapping-workshop", "derisk-measurement-advisor",
        "director-readiness-advisor", "discovery-interview-prep",
        "discovery-process", "eol-checklist", "eol-internal-enablement",
        "eol-message", "eol-process", "eol-readiness-advisor",
        "eol-stakeholder-sequence", "epic-breakdown-advisor",
        "epic-hypothesis", "executive-onboarding-playbook",
        "feature-investment-advisor", "finance-based-pricing-advisor",
        "finance-metrics-quickref", "intel-discipline-advisor",
        "intelligence-collection-disciplines", "jobs-to-be-done",
        "lean-ux-canvas", "lifecycle-play-advisor", "market-landscape-scan",
        "opportunity-solution-tree", "organic-growth-advisor",
        "pestel-analysis", "pestel-delta-monitor", "pm-skill-creator",
        "pol-probe", "pol-probe-advisor", "porters-five-forces",
        "positioning-statement", "positioning-workshop", "prd-development",
        "press-release", "pricing-packaging-tracker", "prioritization-advisor",
        "problem-framing-canvas", "problem-statement",
        "product-lifecycle-plays", "product-sense-interview-answer",
        "product-strategy-session", "proto-persona", "recommendation-canvas",
        "roadmap-planning", "saas-economics-efficiency-metrics",
        "saas-revenue-growth-metrics", "stakeholder-engagement-advisor",
        "stakeholder-identification", "stakeholder-mapping", "storyboard",
        "swot-analysis", "tam-sam-som-calculator", "user-story",
        "user-story-mapping", "user-story-mapping-workshop",
        "user-story-splitting", "voice-of-customer-miner",
        "vp-cpo-readiness-advisor", "workshop-facilitation",
    ],
    "squad-engineering-practices": [
        "api-and-interface-design", "browser-testing-with-devtools",
        "ci-cd-and-automation", "code-review-and-quality",
        "code-simplification", "constraint-driven-development",
        "context-engineering", "debugging-and-error-recovery",
        "deprecation-and-migration", "documentation-and-adrs",
        "doubt-driven-development", "git-workflow-and-versioning",
        "idea-refine", "incremental-implementation", "interview-me",
        "observability-and-instrumentation", "performance-optimization",
        "planning-and-task-breakdown", "security-and-hardening",
        "shipping-and-launch", "source-driven-development",
        "spec-driven-development", "test-driven-development",
        "using-agent-skills",
    ],
    "squad-gcp-core": [
        "cloud-build-basics", "cloud-logging-configuration-basics",
        "cloud-logging-cross-project-configuration",
        "cloud-logging-query-generation", "cloud-monitoring-chart-generation",
        "cloud-monitoring-list-time-series-request",
        "cloud-monitoring-metric-selection", "cloud-monitoring-promql-query",
        "cloud-run-basics", "firebase-basics", "gcloud",
        "google-cloud-filestore-autoscale",
        "google-cloud-global-frontend-configuration",
        "google-cloud-networking-observability", "google-cloud-recipe-auth",
        "google-cloud-recipe-foundation-builder",
        "google-cloud-recipe-onboarding", "google-cloud-scc-query",
        "google-cloud-slo-alert-configuration",
        "google-cloud-solution-architecture",
        "google-cloud-solution-multi-agent-security",
        "google-cloud-solution-n-tier-serverless-web-app",
        "google-cloud-storage-basics", "google-cloud-storage-fuse",
        "google-cloud-waf-cost-optimization",
        "google-cloud-waf-operational-excellence",
        "google-cloud-waf-performance-optimization",
        "google-cloud-waf-reliability", "google-cloud-waf-security",
        "google-cloud-waf-sustainability", "iam-helper-for-policy-simulator",
        "iam-helper-for-privileged-access-management",
    ],
    "squad-gcp-data": [
        "alloydb-basics", "application-design-center-design-deploy",
        "bigquery-ai-ml", "bigquery-basics", "bigquery-bigframes",
        "bigtable-basics", "cloud-databases-onboarding", "cloud-sql-basics",
        "datalineage-bigquery-asset-impact-analysis", "datalineage-summary",
        "google-cloud-solution-agentic-ai-bidirectional-streaming",
        "google-cloud-solution-agentic-ai-borderless-data-lakehouse",
        "google-cloud-solution-agentic-ai-data-science-workflow",
        "google-cloud-solution-agentic-analytics-spark-knowledge-catalog",
        "google-cloud-solution-hybrid-search-alloydb",
        "google-cloud-solution-rag-enterprise-search-gke-sqldb",
        "managed-airflow-dag-authoring", "managed-airflow-dag-troubleshooting",
        "managed-airflow-migrations", "spanner-basics", "workload-manager-basics",
    ],
    "squad-gke": [
        "gke-ai-troubleshooting-handle-disruption-gpu-tpu",
        "gke-ai-troubleshooting-jobset-interruption",
        "gke-ai-troubleshooting-tpu-dynamic-slices-monitoring",
        "gke-ai-troubleshooting-tpu-metrics-monitoring",
        "gke-ai-troubleshooting-tpu-vbar-oom", "gke-alert-configuration",
        "gke-app-onboarding", "gke-backup-dr", "gke-basics", "gke-batch-hpc",
        "gke-cluster-autoscaler", "gke-cluster-creation",
        "gke-compute-classes", "gke-cost-analysis", "gke-cost-optimization",
        "gke-custom-golden-image-discovery", "gke-golden-path",
        "gke-inference", "gke-manifest-generation", "gke-multitenancy",
        "gke-networking", "gke-observability", "gke-platform-security",
        "gke-productionize", "gke-reliability", "gke-service-networking",
        "gke-storage", "gke-upgrades", "gke-workload-scaling",
        "gke-workload-security", "gke-workload-troubleshooting",
        "google-cloud-solution-guided-gke-ai-migration",
    ],
    "squad-cloud-ai": [
        "agent-platform-alert-configuration", "agent-platform-deploy",
        "agent-platform-endpoint-management", "agent-platform-eval-flywheel",
        "agent-platform-inference", "agent-platform-migrate-from-ai-studio",
        "agent-platform-model-registry", "agent-platform-prompt-management",
        "agent-platform-rag-engine-management", "agent-platform-skill-registry",
        "agent-platform-troubleshooting", "agent-platform-tuning",
        "agent-platform-tuning-management", "gemini-agents-api", "gemini-api",
        "gemini-interactions-api", "gemini-live-api", "genkit-dart",
        "genkit-go", "genkit-js", "genkit-python",
        "google-agents-cli-onboarding",
        "google-cloud-solution-build-deploy-agents",
    ],
    "squad-agent-engineering": [
        "agent-orchestration-advisor", "context-engineering-advisor",
        "finding-google-skills", "incoming-request-advisor",
        "retrieving-developer-knowledge", "skill-authoring-workflow",
    ],
    "squad-adtech": [
        "data-manager-api-audience-ingestion",
        "data-manager-api-event-ingestion", "data-manager-api-setup",
        "detection-engineering-coverage-evaluation",
        "google-ads-api-account-diagnostics", "google-ads-api-mcp-setup",
        "google-ads-api-quickstart", "google-analytics-admin-api-basics",
        "google-analytics-data-api-basics",
        "google-mobile-ads-android-migrate-to-next-gen",
        "google-mobile-ads-banner", "google-mobile-ads-get-started",
        "google-mobile-ads-interstitial", "google-mobile-ads-rewarded",
        "ima-dai-sdk", "ima-sdk-client-side",
    ],
}


# ---------------------------------------------------------------------------
# Squad definitions
# ---------------------------------------------------------------------------
SQUADS = {
    "squad-android": {
        "name": "Android Engineering",
        "mission": "Engenharia da plataforma Android nativa: arquitetura, estado, rede, build, SDKs, identidade e mídia.",
        "domains": ["android", "kotlin", "mobile", "platform", "identity"],
        "capabilities": ["implementation", "architecture", "migration", "debugging", "testing", "optimization", "security"],
        "routing_signals": ["android", "kotlin", "apk", "retrofit", "room", "viewmodel", "hilt", "gradle", "agp", "billing",
                            "compile", "build", "permission", "intent", "credentials", "authentication", "auth", "login",
                            "camera", "crash", "emulator", "device", "battery", "coroutine", "rxjava", "kotlin/native",
                            "play", "keystore", "proguard", "r8", "test", "email", "passkey", "sign-in", "identity"],
        "lanes": ["architecture", "data-layer", "concurrency", "build-tooling", "identity", "media", "profiling"],
        "delegates_to": ["squad-compose", "squad-engineering-practices", "squad-adtech", "squad-design-ux"],
        "out_of_scope": ["UI composable (squad-compose)", "infraestrutura de nuvem (squad-gcp-*)", "design visual (squad-design-ux)"],
    },
    "squad-compose": {
        "name": "Jetpack Compose UI",
        "mission": "UI declarativa com Jetpack Compose em todos os form factors (mobile, TV, Wear, XR, desktop, web).",
        "domains": ["jetpack-compose", "compose", "ui", "multiplatform", "kotlin"],
        "capabilities": ["implementation", "ui", "migration", "accessibility", "design", "navigation"],
        "routing_signals": ["compose", "composable", "recomposition", "navhost", "material3", "lazycolumn", "modifier",
                            "screen", "ui", "wear", "tv", "xr", "glasses", "glimmer",
                            "activity", "fragment", "views", "xml", "stylesheet", "theme"],
        "lanes": ["foundation", "navigation", "form-factors", "migration", "accessibility", "theming"],
        "delegates_to": ["squad-android", "squad-compose-performance", "squad-design-ux", "squad-engineering-practices"],
        "out_of_scope": ["performance de recomposition diagnóstica (squad-compose-performance)",
                         "design system / tokens (squad-design-ux)", "código nativo Android não-UI (squad-android)"],
    },
    "squad-compose-performance": {
        "name": "Compose Performance",
        "mission": "Diagnóstico e correção de performance, recomposition e stability do Jetpack Compose.",
        "domains": ["jetpack-compose", "compose", "performance", "ui"],
        "capabilities": ["optimization", "debugging", "analysis", "measurement", "refactoring"],
        "routing_signals": ["recompo", "jank", "scroll", "frame", "stability", "skip", "baseline", "macrobenchmark",
                            "trace", "hot reload", "hotswan", "lazy", "modifier", "state", "flow", "r8",
                            "slow", "lag", "fps", "memory", "startup", "benchmark"],
        "lanes": ["measurement", "diagnosis", "stability", "lazy-layouts", "modifiers", "hot-reload"],
        "delegates_to": ["squad-compose", "squad-android", "squad-engineering-practices"],
        "out_of_scope": ["features de UI (squad-compose)", "performance backend (squad-engineering-practices)"],
    },
    "squad-design-ux": {
        "name": "Design Systems & UX",
        "mission": "Design systems, design tokens, design-to-code, acessibilidade visual, UX writing e prototipação.",
        "domains": ["design", "ux", "ui", "accessibility", "tokens", "frontend"],
        "capabilities": ["design", "review", "audit", "accessibility", "code-generation", "prototyping", "governance"],
        "routing_signals": ["design", "ux", "ui", "token", "theme", "brand", "component", "figma", "prototype",
                            "acessib", "wcag", "contrast", "copy", "writing", "aesthetic", "redesign",
                            "style", "color", "typography", "layout"],
        "lanes": ["tokens", "components", "a11y", "design-to-code", "governance", "prototyping", "ux-copy"],
        "delegates_to": ["squad-compose", "squad-product", "squad-android"],
        "out_of_scope": ["estratégia de produto (squad-product)", "performance de runtime Compose (squad-compose-performance)"],
    },
    "squad-product": {
        "name": "Product Management",
        "mission": "Disciplina de produto: discovery, estratégia, crescimento, preço, lifecycle, liderança e planejamento.",
        "domains": ["product", "product-management", "strategy", "growth", "business", "market"],
        "capabilities": ["discovery", "strategy", "planning", "analysis", "decision", "leadership", "pricing", "research"],
        "routing_signals": ["product", "prd", "roadmap", "discovery", "mvp", "persona", "pricing", "competitor",
                            "market", "growth", "retention", "churn", "user", "customer", "interview", "backlog",
                            "epic", "story", "stakeholder", "eol", "sunset", "kpi", "metric",
                            "positioning", "swot", "strategy", "decision", "prioritiz", "workshop",
                            "problem", "issue", "opportunity", "pain", "segment"],
        "lanes": ["discovery", "strategy", "growth", "pricing", "planning", "lifecycle", "leadership", "workshop"],
        "delegates_to": ["squad-design-ux", "squad-android", "squad-adtech", "squad-engineering-practices"],
        "out_of_scope": ["implementação de código (squad-android/compose)", "design visual (squad-design-ux)",
                         "infraestrutura (squad-gcp-*/gke)"],
    },
    "squad-engineering-practices": {
        "name": "Software Engineering Practices",
        "mission": "Práticas transversais de engenharia: review, SDLC, segurança, testes, documentação, git e CI/CD.",
        "domains": ["software-engineering", "engineering", "devops", "security", "testing", "quality"],
        "capabilities": ["review", "implementation", "security", "documentation", "testing", "planning", "debugging", "refactoring"],
        "routing_signals": ["review", "pull request", "pr", "refactor", "security", "harden", "test", "spec", "tdd",
                            "documentation", "adr", "git", "commit", "branch", "ci", "cd", "pipeline", "quality",
                            "observability", "logging", "incident", "api design", "contract", "launch", "release"],
        "lanes": ["code-quality", "security", "testing", "docs", "git", "ci-cd", "delivery"],
        "delegates_to": ["squad-android", "squad-compose", "squad-gcp-core", "squad-cloud-ai", "squad-product"],
        "out_of_scope": ["domínio de produto (squad-product)", "domínio de plataforma específica (squad-gke/cloud-ai/android)"],
    },
    "squad-gcp-core": {
        "name": "Google Cloud Platform - Core",
        "mission": "Fundações GCP: compute, storage, IAM, networking, logging/monitoring, security e WAF.",
        "domains": ["google-cloud", "gcp", "cloud", "iam", "networking", "storage"],
        "capabilities": ["deployment", "configuration", "troubleshooting", "security", "monitoring", "cost", "architecture"],
        "routing_signals": ["gcp", "google cloud", "gcloud", "cloud run", "cloud storage", "iam", "vpc", "load balancer",
                            "logging", "monitoring", "slo", "waf", "security command center", "firebase",
                            "filestore", "cloud build", "cost"],
        "lanes": ["compute", "storage", "iam-security", "observability", "networking", "solutions"],
        "delegates_to": ["squad-gke", "squad-gcp-data", "squad-cloud-ai", "squad-engineering-practices"],
        "out_of_scope": ["kubernetes/GKE (squad-gke)", "dados/BigQuery (squad-gcp-data)", "GenAI (squad-cloud-ai)"],
    },
    "squad-gcp-data": {
        "name": "Google Cloud Data & Analytics",
        "mission": "BDA no Google Cloud: BigQuery, bancos de dados, Airflow, lineage, dataframes e soluções de dados.",
        "domains": ["google-cloud", "gcp", "data", "database", "bigquery", "analytics", "etl"],
        "capabilities": ["analysis", "querying", "migration", "modeling", "etl", "troubleshooting"],
        "routing_signals": ["bigquery", "spanner", "alloydb", "bigtable", "cloud sql", "airflow", "dag",
                            "lineage", "dataframe", "sql", "database", "data warehouse", "lakehouse",
                            "etl", "elt", "query", "analytics", "looker"],
        "lanes": ["data-warehouse", "data-lakes", "databases", "pipeline-orchestration", "data-governance"],
        "delegates_to": ["squad-gcp-core", "squad-gke", "squad-cloud-ai", "squad-engineering-practices"],
        "out_of_scope": ["infraestrutura genérica GCP (squad-gcp-core)", "kubernetes (squad-gke)"],
    },
    "squad-gke": {
        "name": "Google Kubernetes Engine",
        "mission": "Operação de Kubernetes no GKE: clusters, workloads, networking, segurança, custo e IA em infra.",
        "domains": ["gke", "kubernetes", "google-cloud", "gcp", "container"],
        "capabilities": ["deployment", "troubleshooting", "optimization", "security", "networking", "cost", "ai-infra"],
        "routing_signals": ["gke", "kubernetes", "k8s", "pod", "cluster", "node", "helm", "kubectl",
                            "job", "tpu", "gpu", "ingress", "gateway", "autoscaler", "namespace",
                            "workload", "deployment", "container", "docker"],
        "lanes": ["provisioning", "workloads", "networking", "security", "cost", "ai-infra", "ops"],
        "delegates_to": ["squad-gcp-core", "squad-cloud-ai", "squad-engineering-practices"],
        "out_of_scope": ["serviços não-Kubernetes do GCP (squad-gcp-core)", "dados/BigQuery (squad-gcp-data)"],
    },
    "squad-cloud-ai": {
        "name": "Google Cloud AI / GenAI",
        "mission": "IA e GenAI no Google Cloud: Agent Platform, Gemini, Genkit, modelos, tuning, eval e RAG.",
        "domains": ["google-cloud", "ai", "genai", "gemini", "agents", "llm"],
        "capabilities": ["implementation", "inference", "deployment", "tuning", "evaluation", "rag", "prompting"],
        "routing_signals": ["gemini", "genai", "llm", "model", "inference", "agent", "prompt", "rag", "embedding",
                            "tuning", "fine-tune", "eval", "vertex", "agent platform", "genkit", "live api",
                            "interactions api", "model registry"],
        "lanes": ["model-lifecycle", "agents", "rag", "inference", "evaluation", "genkit"],
        "delegates_to": ["squad-gke", "squad-gcp-core", "squad-agent-engineering"],
        "out_of_scope": ["kubernetes (squad-gke)", "infraestrutura genérica (squad-gcp-core)"],
    },
    "squad-agent-engineering": {
        "name": "AI Agent Engineering (meta)",
        "mission": "Especialização em orquestração de agentes, descoberta de conhecimento, contexto e authoring de skills.",
        "domains": ["ai", "agents", "engineering", "context", "orchestration"],
        "capabilities": ["orchestration", "discovery", "context", "authoring", "research"],
        "routing_signals": ["agent", "orchestrat", "workflow", "context", "prompt engineering", "skill",
                            "knowledge", "mcp", "multi-agent", "handoff", "discovery"],
        "lanes": ["orchestration", "discovery", "context", "skill-authoring"],
        "delegates_to": ["squad-cloud-ai", "squad-product", "squad-engineering-practices"],
        "out_of_scope": ["infra de modelos (squad-cloud-ai)", "disciplina de produto (squad-product)"],
    },
    "squad-adtech": {
        "name": "Ads, Analytics & Monetization",
        "mission": "Google Ads, Google Analytics, Google Mobile Ads, IMA e ingestão de dados para monetização.",
        "domains": ["ads", "adtech", "monetization", "analytics", "marketing"],
        "capabilities": ["implementation", "integration", "migration", "monetization", "analytics", "diagnostics"],
        "routing_signals": ["ads", "ad", "advertise", "banner", "interstitial", "rewarded", "ima", "dai",
                            "campaign", "analytics", "account", "remarketing", "audience", "conversion",
                            "monetiz", "play console", "billing", "engage"],
        "lanes": ["google-ads", "mobile-ads", "video-ads", "analytics", "data-ingestion", "policy"],
        "delegates_to": ["squad-android", "squad-product", "squad-engineering-practices"],
        "out_of_scope": ["product strategy (squad-product)", "implementação Android não-anúncio (squad-android)"],
    },
}


# ---------------------------------------------------------------------------
# Cross-domain consumers (primary owner stays; these squads also consume)
# ---------------------------------------------------------------------------
CONSUMERS = {
    "security-and-hardening": ["squad-android", "squad-gke", "squad-cloud-ai", "squad-adtech"],
    "appfunctions": ["squad-cloud-ai"],
    "browser-testing-with-devtools": ["squad-design-ux"],
    "compose-expert": ["squad-compose-performance", "squad-android"],
    "a11y-audit": ["squad-android"],
    "android-accessibility": ["squad-android", "squad-design-ux"],
    "mobile-app-ui-design": ["squad-product"],
    "performance": ["squad-engineering-practices"],
    "detection-engineering-coverage-evaluation": ["squad-gcp-core"],
    "firebase-basics": ["squad-android"],
    "google-cloud-solution-build-deploy-agents": ["squad-gcp-core", "squad-gke"],
    "google-cloud-solution-guided-gke-ai-migration": ["squad-cloud-ai"],
    "google-cloud-solution-multi-agent-security": ["squad-cloud-ai", "squad-gke"],
    "display-glasses-with-jetpack-compose-glimmer": ["squad-android"],
    "wear-compose-m3": ["squad-android"],
    "kotlin-tooling-native-build-performance": ["squad-compose", "squad-product"],
    "api-and-interface-design": ["squad-android", "squad-compose", "squad-gcp-core"],
    "debugging-and-error-recovery": ["squad-android", "squad-gke", "squad-gcp-data", "squad-compose"],
    "android-profiler": ["squad-compose"],
    "gke-observability": ["squad-gcp-core"],
    "gke-workload-troubleshooting": ["squad-gcp-core"],
    "kotlin-concurrency-expert": ["squad-compose"],
    "compose-ui": ["squad-engineering-practices"],
    "competitive-analysis-process": ["squad-product"],
    "swot-analysis": ["squad-product"],
    "ansoff-matrix": ["squad-product"],
}

# Generic / meta skills - not tightly bound to one domain
GENERIC_TAGS = {
    "using-agent-skills": ["meta", "generic"],
    "skill-authoring-workflow": ["meta"],
    "interview-me": ["generic"],
    "idea-refine": ["generic"],
    "incremental-implementation": ["generic"],
    "planning-and-task-breakdown": ["generic"],
    "code-review-and-quality": ["generic"],
    "autonomous-investigation": ["generic", "meta"],
    "workshop-facilitation": ["generic"],
}

# Duplicates: canonical <- duplicate
DUPLICATES = {
    "compose-performance-audit": "auditing-compose-performance",
    "compose-navigation": "navigation-3",
    "xml-to-compose-migration": "migrate-xml-views-to-jetpack-compose",
}

# Soft overlap (NOT merged)
SOFT_OVERLAP = {"testing-setup": "android-testing"}

# Preferred / high-signal skills per squad (subset; registry still full)
PREFERRED = {
    "squad-android": ["android-architecture", "android-data-layer", "android-viewmodel", "android-testing",
                      "android-coroutines", "gradle-build-performance", "restore-credentials",
                      "verified-email", "debugging-and-error-recovery", "android-profiler",
                      "api-and-interface-design", "kotlin-concurrency-expert", "r8-analyzer",
                      "testing-setup", "rxjava-to-coroutines-migration",
                      "play-billing-library-version-upgrade"],
    "squad-compose": ["compose-expert", "compose-ui", "navigation-3", "migrate-xml-views-to-jetpack-compose", "adaptive"],
    "squad-compose-performance": ["diagnosing-compose-stability", "debugging-recompositions", "auditing-compose-performance",
                                  "generating-baseline-profiles", "optimizing-lazy-layouts"],
    "squad-design-ux": ["design-tokens", "design-code", "a11y-audit", "figma-integration", "ux-writing"],
    "squad-product": ["prd-development", "product-strategy-session", "roadmap-planning", "discovery-process",
                      "prioritization-advisor", "stakeholder-identification", "user-story",
                      "competitive-analysis-process", "swot-analysis", "ansoff-matrix", "market-landscape-scan",
                      "problem-statement", "discovery-interview-prep", "feature-investment-advisor",
                      "epic-breakdown-advisor"],
    "squad-engineering-practices": ["code-review-and-quality", "spec-driven-development", "test-driven-development",
                                    "security-and-hardening", "git-workflow-and-versioning"],
    "squad-gcp-core": ["gcloud", "cloud-logging-query-generation", "google-cloud-storage-basics",
                       "google-cloud-waf-cost-optimization", "google-cloud-slo-alert-configuration"],
    "squad-gcp-data": ["bigquery-basics", "bigquery-ai-ml", "spanner-basics", "datalineage-summary"],
    "squad-gke": ["gke-basics", "gke-cluster-creation", "gke-manifest-generation", "gke-workload-troubleshooting",
                  "gke-productionize", "gke-observability"],
    "squad-cloud-ai": ["gemini-api", "agent-platform-inference", "agent-platform-eval-flywheel", "agent-platform-deploy"],
    "squad-agent-engineering": ["agent-orchestration-advisor", "finding-google-skills", "context-engineering-advisor"],
    "squad-adtech": ["google-ads-api-quickstart", "google-mobile-ads-get-started", "google-analytics-data-api-basics"],
}

# Selection guidance is intentionally concise.  It describes a squad's role
# to the LLM; it is not a lexical classifier and must never be consumed as a
# scoring table by Python.
SELECTION_GUIDANCE = {
    "squad-android": {
        "select_when": "The current work is native Android/Kotlin implementation, platform architecture, data/state, build tooling, identity, media, or device behavior.",
        "do_not_select_when": "The current objective is product discovery, visual/UX design, Compose-only UI, Compose performance diagnosis, or cloud infrastructure.",
    },
    "squad-compose": {
        "select_when": "The current work is implementing or evolving a Jetpack Compose user interface, navigation, theming, accessibility, or form-factor experience.",
        "do_not_select_when": "The work is diagnosing Compose runtime performance, defining a design system, or implementing non-UI Android/platform behavior.",
    },
    "squad-compose-performance": {
        "select_when": "The current work is measuring, diagnosing, or correcting Compose recomposition, stability, rendering, startup, scrolling, or runtime performance.",
        "do_not_select_when": "The request is simply to build a new Compose screen or concerns backend/platform performance outside Compose runtime behavior.",
    },
    "squad-design-ux": {
        "select_when": "The current work is UX/design discovery, visual design, design systems, tokens, accessibility, prototypes, or UX writing.",
        "do_not_select_when": "The current decision is product strategy or the request has moved into Android/Compose implementation or runtime performance diagnosis.",
    },
    "squad-product": {
        "select_when": "The current work is understanding a user problem, discovery, product decision, prioritization, strategy, PRD, roadmap, growth, or planning.",
        "do_not_select_when": "The user is asking to implement code, create a visual design, or operate infrastructure. Technology context alone does not override a discovery or planning objective.",
    },
    "squad-engineering-practices": {
        "select_when": "The current work is cross-cutting engineering practice: review, quality, tests, security, documentation, Git, delivery process, or incident practice.",
        "do_not_select_when": "A request is primarily about a specific platform or product domain and needs that specialist role first.",
    },
    "squad-gcp-core": {
        "select_when": "The current work is general Google Cloud foundations such as Cloud Run, IAM, networking, storage, observability, security, or cost.",
        "do_not_select_when": "The primary domain is Kubernetes/GKE, data and analytics, or Google Cloud AI/GenAI.",
    },
    "squad-gcp-data": {
        "select_when": "The current work is Google Cloud data, databases, BigQuery, SQL, analytics, ETL/ELT, lineage, or Airflow.",
        "do_not_select_when": "The request is general cloud infrastructure, Kubernetes operation, or model/GenAI engineering.",
    },
    "squad-gke": {
        "select_when": "The current work is Kubernetes or GKE clusters, workloads, manifests, networking, security, reliability, cost, or AI infrastructure.",
        "do_not_select_when": "The work concerns non-Kubernetes GCP services, data/BigQuery, or the application/model logic itself.",
    },
    "squad-cloud-ai": {
        "select_when": "The current work is Google Cloud AI/GenAI: Gemini, Genkit, Agent Platform, model inference, evaluation, tuning, prompting, or RAG.",
        "do_not_select_when": "The primary work is Kubernetes infrastructure, generic GCP infrastructure, or agent-context/skill authoring without a model-platform concern.",
    },
    "squad-agent-engineering": {
        "select_when": "The current work is designing or improving agent workflows, context, skill discovery, orchestration, or agent engineering practices.",
        "do_not_select_when": "The request is product discovery, a concrete Cloud AI model workload, or a platform-specific implementation.",
    },
    "squad-adtech": {
        "select_when": "The current work is advertising, monetization, Google Ads, Google Analytics, Mobile Ads, IMA, campaigns, audiences, or conversion measurement.",
        "do_not_select_when": "The request is general Android work without an ads/analytics objective or product strategy without an adtech focus.",
    },
}

STOPWORDS = {
    "the","and","for","use","uses","used","using","when","this","that","with","from","into","which","have","has",
    "will","can","should","your","you","are","was","were","not","but","all","any","each","its","youre",
    "also","more","most","than","then","them","they","their","these","those","about","after","before","between",
    "provide","provides","providing", "help","helps","helping","work","works","working","want","needs","need",
    "guide","guides","guided","guidance","skill","skills","agent","agents","app","apps","android","compose",
    "want","make","making","building","build","built","create","creating","created","new","how","what","where",
    "who","why","when","there","here","including","includes","included","such","like","well","good","better",
    "still","may","might","must","through","during","under","over","within","without","following","follows",
    "documentation","reference","references","information","information's","coverage","coverage's","contribute",
    "question","questions","answer","answers","best","practice","practices","method","methods","process","processes",
    "problem","problems","issue","issues","case","cases","example","examples","usually","typically","often",
    "commonly","generally","https","com","github","raw","default","defaults","number","numbers","set","sets","setting",
    "settings","config","configs","configuration","configurations","framework","frameworks","library","libraries",
    "module","modules","system","systems","feature","features","scenario","scenarios","specific","specifics",
    "possible","possibility","potentially","potential","related","relationship","relationship's","content",
    "header","headers","body","submit","input","subset","api","apis","apk","sdk","sdks","tool","tools","cli",
    "official","officially","primarily","primary","secondary","alternate","alternative","alternative's",
}


# ---------------------------------------------------------------------------
# Frontmatter parsing (minimal, tolerant)
# ---------------------------------------------------------------------------
def parse_frontmatter(text):
    """Return (name, description, keywords) from the YAML frontmatter."""
    m = re.match(r"^---\s*\n(.*?)\n---", text, re.DOTALL)
    if not m:
        return None, "", []
    block = m.group(1)

    name = ""
    dm = re.search(r"(?m)^name:\s*(.+?)\s*$", block)
    if dm:
        name = dm.group(1).strip().strip('"\'')
        # multipart name:
        name = re.split(r"\s*(?:\n|$)", name)[0].strip()

    desc = ""
    dm = re.search(r"(?ms)^description:\s?(\S.*?)(?=^[a-zA-Z_][\w-]*:\s*|\Z)", block)
    if dm:
        raw = dm.group(1)
        raw = re.sub(r"\n\s+", " ", raw).strip()
        desc = raw.strip().strip('"')
        desc = desc.replace('\\n', ' ').strip()

    keywords = []
    km = re.search(r"(?ms)^metadata:\s*\n.*?keywords:\s*\n((?:\s*-.*\n?)+)", block)
    if km:
        for line in km.group(1).splitlines():
            k = re.match(r"\s*-\s*(.+?)\s*$", line)
            if k:
                keywords.append(k.group(1).strip())
    return name, desc, keywords


def salient_tokens(text, limit=16):
    """Extract content-grounded tokens from skill text (name + desc + keywords)."""
    words = re.findall(r"[a-zA-Z][\w\-]{3,}", text.lower().replace("_", "-"))
    seen = set()
    out = []
    for w in words:
        wc = w.rstrip("-")
        if wc in STOPWORDS or len(wc) < 4:
            continue
        key = wc
        if key in seen:
            continue
        seen.add(key)
        out.append(wc)
        if len(out) >= limit:
            break
    return out


CAPABILITY_LEXICON = {
    "implementation": ["implement", "create", "build", "add", "integrat", "generat", "setup", "configure", "sdk", "code"],
    "migration": ["migrat", "upgrade", "convert", "transform", "adopt"],
    "debugging": ["debug", "troubleshoot", "root-cause", "error", "fail", "hang", "crash", "fix", "resolve", "issue"],
    "optimization": ["optimiz", "performance", "speed", "jank", "slow", "lazy", "baseline", "r8", "memory", "startup"],
    "analysis": ["analy", "audit", "diagnos", "investigat", "assess", "evaluat", "review", "inspect", "research", "measure"],
    "security": ["secur", "harden", "vuln", "threat", "permission", "privacy", "intent", "sanitiz"],
    "testing": ["test", "coverage", "unit", "instrument", "screenshot", "assert"],
    "design": ["design", "token", "theme", "style", "component", "typography", "color", "brand", "ux", "layout"],
    "strategy": ["strateg", "roadmap", "position", "competit", "market", "pricing", "business", "growth"],
    "discovery": ["discover", "interview", "persona", "journey", "opportun", "validation"],
    "deployment": ["deploy", "launch", "rollout", "release", "pipeline", "provision", "publish"],
    "documentation": ["document", "kdoc", "adr", "report", "write"],
    "planning": ["plan", "breakdown", "task", "backlog", "epic", "story", "workshop"],
    "ai-lifecycle": ["inference", "genai", "model", "prompt", "eval", "tuning", "rag", "llm", "agent"],
    "architecture": ["architecture", "module", "clean", "layer", "di", "structure"],
}


def infer_capabilities(desc, keywords):
    text = (desc + " " + " ".join(keywords)).lower()
    caps = set()
    for cap, terms in CAPABILITY_LEXICON.items():
        for t in terms:
            if t in text:
                caps.add(cap)
    return sorted(caps)


def build_registry():
    errors = []
    entries = {}
    for squad, names in SQUAD_SKILLS.items():
        squad_def = SQUADS[squad]
        for skill_name in names:
            path = os.path.join(SKILLS_DIR, skill_name, "SKILL.md")
            if not os.path.isfile(path):
                errors.append("missing skill dir: %s" % skill_name)
                continue
            with io.open(path, "r", encoding="utf-8", errors="replace") as fh:
                text = fh.read()
            name, desc, keywords = parse_frontmatter(text)
            if not name:
                name = skill_name
            combined = skill_name + " " + name + " " + desc + " " + " ".join(keywords)

            capabilities = infer_capabilities(desc, keywords)
            if not capabilities:
                capabilities = [squad_def["capabilities"][0]]

            signals = salient_tokens(combined)
            unique_signals = list(dict.fromkeys(signals))

            tags = []
            if skill_name in GENERIC_TAGS:
                tags = GENERIC_TAGS[skill_name]
            if skill_name in DUPLICATES:
                tags.append("duplicate")
            if skill_name in CONSUMERS:
                tags.append("cross-domain")

            entry = {
                "id": skill_name,
                "name": name,
                "desc": desc[:300],
                "owner": squad,
                "domains": squad_def["domains"],
                "capabilities": capabilities,
                "signals": unique_signals,
                "consumers": CONSUMERS.get(skill_name, []),
                "related": [],
                "tags": tags,
            }
            # related from duplicate/soft-overlap
            if skill_name in DUPLICATES:
                entry["related"] = [DUPLICATES[skill_name]]
                entry["dup_of"] = DUPLICATES[skill_name]
            if skill_name in SOFT_OVERLAP:
                entry["related"] = list(dict.fromkeys(entry["related"] + [SOFT_OVERLAP[skill_name]]))
                entry["soft_overlap_with"] = SOFT_OVERLAP[skill_name]
            entries[skill_name] = entry

    # dangling duplicate references → add related to canonical
    for dup, canonical in DUPLICATES.items():
        if canonical in entries and dup in entries:
            entries[canonical]["related"] = list(dict.fromkeys(
                entries[canonical]["related"] + [dup]))
    return entries, errors


# ---------------------------------------------------------------------------
# Strict YAML emitter (simple subset: scalars, lists, nested maps)
# ---------------------------------------------------------------------------
def q(s):
    return '"%s"' % s.replace("\\", "\\\\").replace('"', '\\"')


def emit_entry(e):
    lines = []
    lines.append("  - id: " + q(e["id"]))
    lines.append("    name: " + q(e["name"]))
    lines.append("    owner: " + q(e["owner"]))
    for key in ("domains", "capabilities", "signals", "consumers", "related", "tags"):
        val = e.get(key, [])
        if val:
            lines.append("    %s:" % key)
            for v in val:
                lines.append("      - " + q(v))
    if "dup_of" in e:
        lines.append("    dup_of: " + q(e["dup_of"]))
    if "soft_overlap_with" in e:
        lines.append("    soft_overlap_with: " + q(e["soft_overlap_with"]))
    return "\n".join(lines)


def emit_squad(squad_id, s, skill_ids):
    lines = []
    lines.append("id: " + q(squad_id))
    lines.append("name: " + q(s["name"]))
    lines.append("mission: " + q(s["mission"]))
    lines.append("domains:")
    for d in s["domains"]:
        lines.append("  - " + q(d))
    lines.append("capabilities:")
    for c in s["capabilities"]:
        lines.append("  - " + q(c))
    lines.append("routing_signals:")
    for r in s["routing_signals"]:
        lines.append("  - " + q(r))
    guidance = SELECTION_GUIDANCE[squad_id]
    lines.append("selection_guidance:")
    lines.append("  select_when: " + q(guidance["select_when"]))
    lines.append("  do_not_select_when: " + q(guidance["do_not_select_when"]))
    lines.append("lanes:")
    for l in s["lanes"]:
        lines.append("  - " + q(l))
    lines.append("skill_count: %d" % len(skill_ids))
    lines.append("preferred_skills:")
    for p in PREFERRED.get(squad_id, []):
        lines.append("  - " + q(p))
    lines.append("delegates_to:")
    for d in s["delegates_to"]:
        lines.append("  - " + q(d))
    lines.append("out_of_scope:")
    for o in s["out_of_scope"]:
        lines.append("  - " + q(o))
    return "\n".join(lines)


def emit_command(squad_id, s):
    guidance = SELECTION_GUIDANCE[squad_id]
    return "\n".join([
        "---",
        "description: Adota o contexto operacional de " + s["name"] + ".",
        "---",
        "# " + squad_id,
        "",
        "Adote o contexto operacional de **" + squad_id + "**. Este Squad não chama outro agente; ele define o papel que o mesmo LLM incorpora.",
        "",
        "**Mission:** " + s["mission"],
        "",
        "**Domains:** " + ", ".join(s["domains"]),
        "",
        "**Capabilities:** " + ", ".join(s["capabilities"]),
        "",
        "**Selecione quando:** " + guidance["select_when"],
        "",
        "**Não selecione quando:** " + guidance["do_not_select_when"],
        "",
        "**Lanes:** " + ", ".join(s["lanes"]),
        "",
        "**Preferred skills:**",
        "",
    ]) + "\n".join("- `/` + `" + p + "` → [`" + p + "`](.kilo/skills/" + p + "/SKILL.md)" for p in PREFERRED.get(squad_id, [])) + "\n" + "\n".join([
        "",
        "**Out of scope:** " + "; ".join(s["out_of_scope"]),
        "",
        "Carregue o **Squad Context** completo em `.kilo/squads/" + squad_id + "/SQUAD.yaml` e a lista de skills em `.kilo/squads/registry.yaml` (owner `" + squad_id + "`). Não duplique conteúdo de skills; apenas referencie.",
        "",
        "Se a natureza do trabalho mudar, reavalie o papel e faça uma transição sequencial, quando necessária, para: " + ", ".join("`/" + d + "`" for d in s["delegates_to"]) + ".",
        "",
        "Caminho preferido: **request → /route → Squad adotado → Skill discovery → execução**.",
    ])


def emit_catalog():
    """Emit the compact context that /route asks the LLM to inspect first."""
    lines = [
        "# Squad Catalog",
        "",
        "This catalog supports LLM-native role selection. It is context, not a scoring model: understand the user's objective and current work stage before considering implementation technology.",
        "",
        "Choose one primary squad, load its manifest, and self-validate the choice. When the work genuinely changes specialization, transition sequentially; squads are not parallel agents.",
        "",
    ]
    for squad_id, squad in SQUADS.items():
        guidance = SELECTION_GUIDANCE[squad_id]
        lines.extend([
            "## " + squad_id + " — " + squad["name"],
            "",
            "- Mission: " + squad["mission"],
            "- Select when: " + guidance["select_when"],
            "- Do not select when: " + guidance["do_not_select_when"],
            "- Domains: " + ", ".join(squad["domains"]),
            "- Capabilities: " + ", ".join(squad["capabilities"]),
            "- Related transitions: " + ", ".join(squad["delegates_to"]),
            "- Manifest: `.kilo/squads/" + squad_id + "/SQUAD.yaml`",
            "",
        ])
    return "\n".join(lines)


def main():
    entries, errors = build_registry()
    if errors:
        for e in errors:
            print("ERROR:", e)
        sys.exit(1)

    if not os.path.isdir(OUT_DIR):
        os.makedirs(OUT_DIR)
    if not os.path.isdir(CMD_DIR):
        os.makedirs(CMD_DIR)

    # registry.yaml
    lines = ["# Skill Registry - single source of truth (generated by build_squads.py)",
             "# Every skill has exactly one owner (squad). Consumers are references only.",
             "skills:"]
    for squad in SQUADS:
        for skill_id in SQUAD_SKILLS[squad]:
            lines.append(emit_entry(entries[skill_id]))
    with io.open(os.path.join(OUT_DIR, "registry.yaml"), "w", encoding="utf-8") as fh:
        fh.write("\n".join(lines) + "\n")

    # squad manifests + commands
    for squad_id, s in SQUADS.items():
        sdir = os.path.join(OUT_DIR, squad_id)
        if not os.path.isdir(sdir):
            os.makedirs(sdir)
        with io.open(os.path.join(sdir, "SQUAD.yaml"), "w", encoding="utf-8") as fh:
            fh.write(emit_squad(squad_id, s, SQUAD_SKILLS[squad_id]) + "\n")
        with io.open(os.path.join(CMD_DIR, squad_id + ".md"), "w", encoding="utf-8") as fh:
            fh.write(emit_command(squad_id, s) + "\n")

    with io.open(os.path.join(OUT_DIR, "CATALOG.md"), "w", encoding="utf-8") as fh:
        fh.write(emit_catalog() + "\n")

    # route command
    route = "\n".join([
        "---",
        "description: Faz o LLM selecionar e adotar o Squad adequado. Uso: /route <sua tarefa em linguagem natural>.",
        "---",
        "# Route",
        "",
        "Leia a solicitação completa. Você é o agente; um Squad é o contexto operacional que você adota, não outro agente a ser chamado.",
        "",
        "1. Entenda o problema/objeto, o resultado desejado e o estágio atual do trabalho.",
        "2. Leia `.kilo/squads/CATALOG.md` e compare mission, domains, capabilities, selection_guidance, out_of_scope, preferred_skills e delegates_to dos manifestos candidatos.",
        "3. Escolha o Squad cujo papel melhor atende ao **que** o usuário quer realizar antes de considerar **como** a tecnologia será usada.",
        "4. Carregue `.kilo/squads/<selected-squad>/SQUAD.yaml` e reavalie: este papel ainda é o mais adequado? Se não, volte ao catálogo e selecione outro.",
        "5. Com o Squad adotado, consulte `.kilo/squads/registry.yaml` apenas no contexto desse Squad: skills de sua ownership e, quando necessário, skills que ele consome. Selecione as skills pelo significado da tarefa e leia seus `SKILL.md`.",
        "6. Execute a tarefa. Se ela mudar de especialização, escolha um novo Squad primário e faça uma transição sequencial.",
        "",
        "Não use scores, thresholds, aliases, keyword matching ou ranking global de skills. Não exponha as 314 skills antes de restringir o contexto pelo Squad.",
        "",
        "Quando a ambiguidade impedir uma escolha responsável após comparar os manifestos, explique a ambiguidade e peça esclarecimento. Não invente uma confiança numérica.",
        "",
        "Os comandos `/squad-*` continuam sendo a forma explícita de escolher um papel; `/route` é a entrada para deixar o LLM selecioná-lo.",
    ])
    with io.open(os.path.join(CMD_DIR, "route.md"), "w", encoding="utf-8") as fh:
        fh.write(route + "\n")

    total = sum(len(v) for v in SQUAD_SKILLS.values())
    print("Registry entries: %d" % len(entries))
    print("Squad manifests:  %d" % len(SQUADS))
    print("Total skills:     %d" % total)
    print("Output dir: %s" % OUT_DIR)
    print("Command dir: %s" % CMD_DIR)


if __name__ == "__main__":
    main()
