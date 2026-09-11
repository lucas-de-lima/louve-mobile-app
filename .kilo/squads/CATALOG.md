# Squad Catalog

This catalog supports LLM-native role selection. It is context, not a scoring model: understand the user's objective and current work stage before considering implementation technology.

Choose one primary squad, load its manifest, and self-validate the choice. When the work genuinely changes specialization, transition sequentially; squads are not parallel agents.

## squad-android — Android Engineering

- Mission: Engenharia da plataforma Android nativa: arquitetura, estado, rede, build, SDKs, identidade e mídia.
- Select when: The current work is native Android/Kotlin implementation, platform architecture, data/state, build tooling, identity, media, or device behavior.
- Do not select when: The current objective is product discovery, visual/UX design, Compose-only UI, Compose performance diagnosis, or cloud infrastructure.
- Domains: android, kotlin, mobile, platform, identity
- Capabilities: implementation, architecture, migration, debugging, testing, optimization, security
- Related transitions: squad-compose, squad-engineering-practices, squad-adtech, squad-design-ux
- Manifest: `.kilo/squads/squad-android/SQUAD.yaml`

## squad-compose — Jetpack Compose UI

- Mission: UI declarativa com Jetpack Compose em todos os form factors (mobile, TV, Wear, XR, desktop, web).
- Select when: The current work is implementing or evolving a Jetpack Compose user interface, navigation, theming, accessibility, or form-factor experience.
- Do not select when: The work is diagnosing Compose runtime performance, defining a design system, or implementing non-UI Android/platform behavior.
- Domains: jetpack-compose, compose, ui, multiplatform, kotlin
- Capabilities: implementation, ui, migration, accessibility, design, navigation
- Related transitions: squad-android, squad-compose-performance, squad-design-ux, squad-engineering-practices
- Manifest: `.kilo/squads/squad-compose/SQUAD.yaml`

## squad-compose-performance — Compose Performance

- Mission: Diagnóstico e correção de performance, recomposition e stability do Jetpack Compose.
- Select when: The current work is measuring, diagnosing, or correcting Compose recomposition, stability, rendering, startup, scrolling, or runtime performance.
- Do not select when: The request is simply to build a new Compose screen or concerns backend/platform performance outside Compose runtime behavior.
- Domains: jetpack-compose, compose, performance, ui
- Capabilities: optimization, debugging, analysis, measurement, refactoring
- Related transitions: squad-compose, squad-android, squad-engineering-practices
- Manifest: `.kilo/squads/squad-compose-performance/SQUAD.yaml`

## squad-design-ux — Design Systems & UX

- Mission: Design systems, design tokens, design-to-code, acessibilidade visual, UX writing e prototipação.
- Select when: The current work is UX/design discovery, visual design, design systems, tokens, accessibility, prototypes, or UX writing.
- Do not select when: The current decision is product strategy or the request has moved into Android/Compose implementation or runtime performance diagnosis.
- Domains: design, ux, ui, accessibility, tokens, frontend
- Capabilities: design, review, audit, accessibility, code-generation, prototyping, governance
- Related transitions: squad-compose, squad-product, squad-android
- Manifest: `.kilo/squads/squad-design-ux/SQUAD.yaml`

## squad-product — Product Management

- Mission: Disciplina de produto: discovery, estratégia, crescimento, preço, lifecycle, liderança e planejamento.
- Select when: The current work is understanding a user problem, discovery, product decision, prioritization, strategy, PRD, roadmap, growth, or planning.
- Do not select when: The user is asking to implement code, create a visual design, or operate infrastructure. Technology context alone does not override a discovery or planning objective.
- Domains: product, product-management, strategy, growth, business, market
- Capabilities: discovery, strategy, planning, analysis, decision, leadership, pricing, research
- Related transitions: squad-design-ux, squad-android, squad-adtech, squad-engineering-practices
- Manifest: `.kilo/squads/squad-product/SQUAD.yaml`

## squad-engineering-practices — Software Engineering Practices

- Mission: Práticas transversais de engenharia: review, SDLC, segurança, testes, documentação, git e CI/CD.
- Select when: The current work is cross-cutting engineering practice: review, quality, tests, security, documentation, Git, delivery process, or incident practice.
- Do not select when: A request is primarily about a specific platform or product domain and needs that specialist role first.
- Domains: software-engineering, engineering, devops, security, testing, quality
- Capabilities: review, implementation, security, documentation, testing, planning, debugging, refactoring
- Related transitions: squad-android, squad-compose, squad-gcp-core, squad-cloud-ai, squad-product
- Manifest: `.kilo/squads/squad-engineering-practices/SQUAD.yaml`

## squad-gcp-core — Google Cloud Platform - Core

- Mission: Fundações GCP: compute, storage, IAM, networking, logging/monitoring, security e WAF.
- Select when: The current work is general Google Cloud foundations such as Cloud Run, IAM, networking, storage, observability, security, or cost.
- Do not select when: The primary domain is Kubernetes/GKE, data and analytics, or Google Cloud AI/GenAI.
- Domains: google-cloud, gcp, cloud, iam, networking, storage
- Capabilities: deployment, configuration, troubleshooting, security, monitoring, cost, architecture
- Related transitions: squad-gke, squad-gcp-data, squad-cloud-ai, squad-engineering-practices
- Manifest: `.kilo/squads/squad-gcp-core/SQUAD.yaml`

## squad-gcp-data — Google Cloud Data & Analytics

- Mission: BDA no Google Cloud: BigQuery, bancos de dados, Airflow, lineage, dataframes e soluções de dados.
- Select when: The current work is Google Cloud data, databases, BigQuery, SQL, analytics, ETL/ELT, lineage, or Airflow.
- Do not select when: The request is general cloud infrastructure, Kubernetes operation, or model/GenAI engineering.
- Domains: google-cloud, gcp, data, database, bigquery, analytics, etl
- Capabilities: analysis, querying, migration, modeling, etl, troubleshooting
- Related transitions: squad-gcp-core, squad-gke, squad-cloud-ai, squad-engineering-practices
- Manifest: `.kilo/squads/squad-gcp-data/SQUAD.yaml`

## squad-gke — Google Kubernetes Engine

- Mission: Operação de Kubernetes no GKE: clusters, workloads, networking, segurança, custo e IA em infra.
- Select when: The current work is Kubernetes or GKE clusters, workloads, manifests, networking, security, reliability, cost, or AI infrastructure.
- Do not select when: The work concerns non-Kubernetes GCP services, data/BigQuery, or the application/model logic itself.
- Domains: gke, kubernetes, google-cloud, gcp, container
- Capabilities: deployment, troubleshooting, optimization, security, networking, cost, ai-infra
- Related transitions: squad-gcp-core, squad-cloud-ai, squad-engineering-practices
- Manifest: `.kilo/squads/squad-gke/SQUAD.yaml`

## squad-cloud-ai — Google Cloud AI / GenAI

- Mission: IA e GenAI no Google Cloud: Agent Platform, Gemini, Genkit, modelos, tuning, eval e RAG.
- Select when: The current work is Google Cloud AI/GenAI: Gemini, Genkit, Agent Platform, model inference, evaluation, tuning, prompting, or RAG.
- Do not select when: The primary work is Kubernetes infrastructure, generic GCP infrastructure, or agent-context/skill authoring without a model-platform concern.
- Domains: google-cloud, ai, genai, gemini, agents, llm
- Capabilities: implementation, inference, deployment, tuning, evaluation, rag, prompting
- Related transitions: squad-gke, squad-gcp-core, squad-agent-engineering
- Manifest: `.kilo/squads/squad-cloud-ai/SQUAD.yaml`

## squad-agent-engineering — AI Agent Engineering (meta)

- Mission: Especialização em orquestração de agentes, descoberta de conhecimento, contexto e authoring de skills.
- Select when: The current work is designing or improving agent workflows, context, skill discovery, orchestration, or agent engineering practices.
- Do not select when: The request is product discovery, a concrete Cloud AI model workload, or a platform-specific implementation.
- Domains: ai, agents, engineering, context, orchestration
- Capabilities: orchestration, discovery, context, authoring, research
- Related transitions: squad-cloud-ai, squad-product, squad-engineering-practices
- Manifest: `.kilo/squads/squad-agent-engineering/SQUAD.yaml`

## squad-adtech — Ads, Analytics & Monetization

- Mission: Google Ads, Google Analytics, Google Mobile Ads, IMA e ingestão de dados para monetização.
- Select when: The current work is advertising, monetization, Google Ads, Google Analytics, Mobile Ads, IMA, campaigns, audiences, or conversion measurement.
- Do not select when: The request is general Android work without an ads/analytics objective or product strategy without an adtech focus.
- Domains: ads, adtech, monetization, analytics, marketing
- Capabilities: implementation, integration, migration, monetization, analytics, diagnostics
- Related transitions: squad-android, squad-product, squad-engineering-practices
- Manifest: `.kilo/squads/squad-adtech/SQUAD.yaml`

