import subprocess, json

en = [113,114,115,116,117,118,119,120,121,122,123,124,125]
fn = [126,127,128,129,130,131,132,133,134,135,136,137,138,139,140]
un = [141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,162,163]

ft = [
    "F-01: Crashlytics Integration","F-02: Update Stale Documentation","F-03: Sync Status Indicator",
    "F-04: Fix Domain Layer Violations","F-05: Remote Config Foundation","F-06: Periodic WorkManager Sync",
    "F-07: PR Validation Pipeline","F-08: Core Repository Unit Tests","F-09: ViewModel Unit Tests",
    "F-10: Maestro CI Integration","F-11: Automated Release Pipeline","F-12: Incremental Data Sync",
    "F-13: Keystore and Access Recovery","F-14: Contributor Onboarding","F-15: DiscoverScreen Editorial Content",
]

ut = [
    "US 1.1: Add Crashlytics SDK dependency","US 1.2: Configure Crashlytics initialization","US 1.3: Add error reporting to sync services",
    "US 2.1: Rewrite Favoritos.md","US 3.1: Create sync status StateFlow","US 3.2: Add visual sync indicator to TopAppBar",
    "US 4.1: Move AuthUiState to domain layer","US 4.2: Replace DefaultTheme in data services","US 5.1: Configure Firebase Remote Config",
    "US 5.2: Create FeatureFlag wrapper","US 6.1: Create PeriodicSyncWorker","US 6.2: Schedule worker on app init",
    "US 7.1: Build check workflow","US 7.2: Lint check workflow","US 7.3: Unit test workflow",
    "US 8.1: Test repositories and sync services","US 9.1: Test ViewModels","US 10.1: Configure Maestro workflow in CI",
    "US 11.1: Build signed AAB and upload","US 12.1: Implement incremental sync","US 13.1: Backup keystore and document recovery",
    "US 14.1: Create contributor onboarding guide","US 15.1: Add curated content to DiscoverScreen",
]

epic_bodies_raw = [
    "## Resumo\n\nO app nao possui nenhum sistema de crash reporting (L04). Firebase Crashlytics nunca foi configurado, o que torna erros de producao completamente invisiveis. Bugs que afetam usuarios reais passam semanas sem deteccao.\n\n## Features Filhas\n\n- [x] REPLACE_F01\n\n## Non-goals\n\n- Nao implementar APM ou tracing customizado\n- Nao migrar de Firebase Analytics",
    "## Resumo\n\nFavoritos.md esta desatualizado (L12). Foi criado como planejamento pre-implementacao e nunca revisado, enganando novos desenvolvedores sobre a arquitetura real do sistema.\n\n## Features Filhas\n\n- [x] REPLACE_F02\n\n## Non-goals\n\n- Nao revisar toda a documentacao do projeto\n- Nao criar novos documentos",
    "## Resumo\n\nO usuario nao tem visibilidade do estado da sincronizacao (L08). Os servicos de sync sao background invisivel. Sem indicador, o usuario pode fechar o app achando que dados estao salvos quando nao estao.\n\n## Features Filhas\n\n- [x] REPLACE_F03\n\n## Non-goals\n\n- Nao modificar logica de sincronizacao existente\n- Nao adicionar novas capacidades de sync",
    "## Resumo\n\nViolacoes de Clean Architecture (L11). AuthRepository importa AuthUiState do UI layer. DataMigrationService e BidirectionalSyncService importam DefaultTheme do UI.\n\n## Features Filhas\n\n- [x] REPLACE_F04\n\n## Non-goals\n\n- Nao refatorar alem das violacoes registradas\n- Nao introduzir UseCase layer",
    "## Resumo\n\nO projeto nao possui feature flags (L05). Cada feature e ativada para 100% dos usuarios no release, sem kill switch.\n\n## Features Filhas\n\n- [x] REPLACE_F05\n\n## Non-goals\n\n- Nao implementar A/B testing\n- Nao integrar ferramentas externas",
    "## Resumo\n\nA sincronizacao so ocorre quando o app esta aberto e ha mudanca de conectividade (L09). Nao ha worker periodico.\n\n## Features Filhas\n\n- [x] REPLACE_F06\n\n## Non-goals\n\n- Nao modificar ConnectivityMonitorService\n- Nao implementar sync incremental (Epic 11)",
    "## Resumo\n\nO projeto nao tem pipeline CI/CD (L03). Build, lint e testes rodam localmente. No central de dependencias.\n\n## Features Filhas\n\n- [x] REPLACE_F07\n\n## Non-goals\n\n- Nao configurar coverage gate\n- Nao implementar SonarCloud",
    "## Resumo\n\nCobertura de testes proxima de zero (L01). 18 bugs do v1.1.0 nao detectados por testes.\n\n## Features Filhas\n\n- [x] REPLACE_F08\n- [x] REPLACE_F09\n\n## Non-goals\n\n- Nao exigir coverage minima\n- Nao escrever testes de UI",
    "## Resumo\n\n7 fluxos Maestro E2E existem mas rodam manualmente (L13).\n\n## Features Filhas\n\n- [x] REPLACE_F10\n\n## Non-goals\n\n- Nao modificar flows existentes\n- Nao criar novos flows",
    "## Resumo\n\nProcesso de release manual (L06). Build, assinatura, upload dependem de maquina local.\n\n## Features Filhas\n\n- [x] REPLACE_F11\n\n## Non-goals\n\n- Nao configurar beta distribution\n- Nao integrar Fastlane",
    "## Resumo\n\nSync atual e full-sync (L07). Nao escala.\n\n## Features Filhas\n\n- [x] REPLACE_F12\n\n## Non-goals\n\n- Nao modificar merge de favoritos\n- Nao introduzir novo banco local",
    "## Resumo\n\nBus factor = 1 (L02). Keystore e acesso Play Console concentrados em uma pessoa.\n\n## Features Filhas\n\n- [x] REPLACE_F13\n- [x] REPLACE_F14\n\n## Non-goals\n\n- Nao contratar novo dev (fora de escopo)",
    "## Resumo\n\nDiscoverScreen e placeholder vazio (L10). Uma das 4 abas principais sem conteudo.\n\n## Features Filhas\n\n- [x] REPLACE_F15\n\n## Non-goals\n\n- Nao implementar recomendacao\n- Nao integrar API externa",
]

feat_bodies_raw = [
    "## Feature: Crashlytics Integration\n\n**Part of:** Epic 1\n\n### Descricao\nFirebase Crashlytics SDK, inicializacao em LouveApp, reporting de erros nao-fatais nos servicos de sync.\n\n### User Stories Filhas\n- [x] REPLACE_US11\n- [x] REPLACE_US12\n- [x] REPLACE_US13\n\n### Criterios de aceite\n- SDK integrado, crashes reportados, erros de sync registrados",
    "## Feature: Update Stale Documentation\n\n**Part of:** Epic 2\n\n### Descricao\nReescrever Favoritos.md.\n\n### User Stories Filhas\n- [x] REPLACE_US21\n\n### Criterios de aceite\n- Documento reflete implementacao real do sistema hibrido",
    "## Feature: Sync Status Indicator\n\n**Part of:** Epic 3\n\n### Descricao\nStateFlow de SyncStatus e indicador visual.\n\n### User Stories Filhas\n- [x] REPLACE_US31\n- [x] REPLACE_US32\n\n### Criterios de aceite\n- SyncStatus: Synced, Syncing, Offline, Error. Indicador com tooltip.",
    "## Feature: Fix Domain Layer Violations\n\n**Part of:** Epic 4\n\n### Descricao\nMover AuthUiState para domain. Substituir DefaultTheme.\n\n### User Stories Filhas\n- [x] REPLACE_US41\n- [x] REPLACE_US42\n\n### Criterios de aceite\n- Projeto compila, KMP desbloqueado",
    "## Feature: Remote Config Foundation\n\n**Part of:** Epic 5\n\n### Descricao\nRemote Config + wrapper FeatureFlag.\n\n### User Stories Filhas\n- [x] REPLACE_US51\n- [x] REPLACE_US52\n\n### Criterios de aceite\n- SDK integrado, wrapper injetavel, fallback offline",
    "## Feature: Periodic WorkManager Sync\n\n**Part of:** Epic 6\n\n### Descricao\nPeriodicWorkRequest para sync em background.\n\n### User Stories Filhas\n- [x] REPLACE_US61\n- [x] REPLACE_US62\n\n### Criterios de aceite\n- Worker funcional, agendamento idempotente",
    "## Feature: PR Validation Pipeline\n\n**Part of:** Epic 7\n\n### Descricao\nWorkflows para build, lint e testes em PR.\n\n### User Stories Filhas\n- [x] REPLACE_US71\n- [x] REPLACE_US72\n- [x] REPLACE_US73\n\n### Criterios de aceite\n- Build, lint e test checks bloqueantes",
    "## Feature: Core Repository Unit Tests\n\n**Part of:** Epic 8\n\n### Descricao\nTestes para repositories e servicos de sync.\n\n### User Stories Filhas\n- [x] REPLACE_US81\n\n### Criterios de aceite\n- HymnRepositoryImpl, Favorites, Services testados",
    "## Feature: ViewModel Unit Tests\n\n**Part of:** Epic 8\n\n### Descricao\nTestes unitarios para ViewModels.\n\n### User Stories Filhas\n- [x] REPLACE_US91\n\n### Criterios de aceite\n- HomeVM, HymnDetailVM, AuthVM testados",
    "## Feature: Maestro CI Integration\n\n**Part of:** Epic 9\n\n### Descricao\nWorkflow com emulador Android executando Maestro.\n\n### User Stories Filhas\n- [x] REPLACE_US101\n\n### Criterios de aceite\n- Workflow funcional nightly ou manual",
    "## Feature: Automated Release Pipeline\n\n**Part of:** Epic 10\n\n### Descricao\nPipeline build AAB signed + upload Play Console.\n\n### User Stories Filhas\n- [x] REPLACE_US111\n\n### Criterios de aceite\n- Pipeline funcional em tag v*",
    "## Feature: Incremental Data Sync\n\n**Part of:** Epic 11\n\n### Descricao\nSync incremental com diff tracking.\n\n### User Stories Filhas\n- [x] REPLACE_US121\n\n### Criterios de aceite\n- Sync incremental funcional",
    "## Feature: Keystore and Access Recovery\n\n**Part of:** Epic 12\n\n### Descricao\nProteger keystore de release.\n\n### User Stories Filhas\n- [x] REPLACE_US131\n\n### Criterios de aceite\n- Processo documentado, backup valido",
    "## Feature: Contributor Onboarding\n\n**Part of:** Epic 12\n\n### Descricao\nGuia de onboarding.\n\n### User Stories Filhas\n- [x] REPLACE_US141\n\n### Criterios de aceite\n- Guia cobre setup, testes, contribuicao",
    "## Feature: DiscoverScreen Editorial Content\n\n**Part of:** Epic 13\n\n### Descricao\nConteudo estatico curado.\n\n### User Stories Filhas\n- [x] REPLACE_US151\n\n### Criterios de aceite\n- Categorias, hinos em destaque, texto introdutorio",
]

feat_map_idx = [[0],[1],[2],[3],[4],[5],[6],[7,8],[9],[10],[11],[12,13],[14]]
us_map_idx = [[0,1,2],[3],[4,5],[6,7],[8,9],[10,11],[12,13,14],[15],[16],[17],[18],[19],[20],[21],[22]]

for ei, e in enumerate(epic_bodies_raw):
    for fi in feat_map_idx[ei]:
        ftext = f'#{fn[fi]} - {ft[fi]}'
        e = e.replace(f'REPLACE_F{str(fi+1).zfill(2)}', ftext, 1)
    e = e.replace('[x]', ' ')
    subprocess.run(['gh', 'issue', 'edit', str(en[ei]), '--body', e], capture_output=True)
    print(f"Epic #{en[ei]} OK")

for fi, f in enumerate(feat_bodies_raw):
    for ui in us_map_idx[fi]:
        utext = f'#{un[ui]} - {ut[ui]}'
        f = f.replace(f'REPLACE_US{str(ui+1).zfill(2)}' if ui+1 >= 10 else f'REPLACE_US0{ui+1}', utext, 1)
    f = f.replace('[x]', ' ')
    subprocess.run(['gh', 'issue', 'edit', str(fn[fi]), '--body', f], capture_output=True)
    print(f"Feature #{fn[fi]} OK")

print("All done.")