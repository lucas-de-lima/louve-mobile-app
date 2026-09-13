import subprocess, json, re, time

project_id = "PVT_kwHOBn6SPs4BhJgi"
delete_mutation = "mutation($p:ID!,$i:ID!){deleteProjectV2Item(input:{projectId:$p itemId:$i}){clientMutationId}}"

# Delete any remaining draft items
q = open(r'D:\Projetos\harness-agentic-sdlc-base\project\louve-mobile-app\scripts\get_items.graphql').read()
r = subprocess.run(['gh', 'api', 'graphql', '-f', f'query={q}'], capture_output=True, text=True)
data = json.loads(r.stdout)
items = data['data']['node']['items']['nodes']
to_delete = []
for item in items:
    content = item.get('content')
    if content and content.get('__typename') == 'DraftIssue':
        t = content.get('title','')
        if t.startswith('Epic') or t.startswith('F-') or t.startswith('US'):
            to_delete.append(item['id'])

print(f"=== Step 1: Delete {len(to_delete)} draft items ===")
for d in to_delete:
    subprocess.run(['gh', 'api', 'graphql', '-f', f'query={delete_mutation}', '-F', f'p={project_id}', '-F', f'i={d}'], capture_output=True)
print("Deleted.")

# Data
epics_data = [
    {"title": "Epic 1: Observabilidade - Crashlytics", "body": "## Resumo\n\nO app nao possui nenhum sistema de crash reporting (L04). Firebase Crashlytics nunca foi configurado, o que torna erros de producao completamente invisiveis. Bugs que afetam usuarios reais passam semanas sem deteccao.\n\n## Features Filhas\n\n- {} F-01: Crashlytics Integration\n\n## Non-goals\n\n- Nao implementar APM ou tracing customizado\n- Nao migrar de Firebase Analytics"},
    {"title": "Epic 2: Higiene de Documentacao", "body": "## Resumo\n\nFavoritos.md esta desatualizado (L12). Foi criado como planejamento pre-implementacao e nunca revisado, enganando novos desenvolvedores sobre a arquitetura real do sistema.\n\n## Features Filhas\n\n- {} F-02: Update Stale Documentation\n\n## Non-goals\n\n- Nao revisar toda a documentacao do projeto\n- Nao criar novos documentos"},
    {"title": "Epic 3: Feedback de Sincronizacao", "body": "## Resumo\n\nO usuario nao tem visibilidade do estado da sincronizacao (L08). Os servicos de sync sao background invisivel. Sem indicador, o usuario pode fechar o app achando que dados estao salvos quando nao estao.\n\n## Features Filhas\n\n- {} F-03: Sync Status Indicator\n\n## Non-goals\n\n- Nao modificar logica de sincronizacao existente\n- Nao adicionar novas capacidades de sync"},
    {"title": "Epic 4: Clean Architecture Enforcement", "body": "## Resumo\n\nViolacoes de Clean Architecture (L11). AuthRepository importa AuthUiState do UI layer. DataMigrationService e BidirectionalSyncService importam DefaultTheme do UI. Essas violacoes bloqueiam KMP e dificultam testes.\n\n## Features Filhas\n\n- {} F-04: Fix Domain Layer Violations\n\n## Non-goals\n\n- Nao refatorar alem das violacoes registradas no ADR-006\n- Nao introduzir UseCase layer"},
    {"title": "Epic 5: Gestao de Funcionalidades", "body": "## Resumo\n\nO projeto nao possui feature flags (L05). Cada feature e ativada para 100% dos usuarios no release, sem kill switch para desativar remotamente.\n\n## Features Filhas\n\n- {} F-05: Remote Config Foundation\n\n## Non-goals\n\n- Nao implementar A/B testing framework\n- Nao integrar com ferramentas externas"},
    {"title": "Epic 6: Sincronizacao em Background", "body": "## Resumo\n\nA sincronizacao so ocorre quando o app esta aberto e ha mudanca de conectividade (L09). Nao ha worker periodico. Dados podem ficar dessincronizados por horas.\n\n## Features Filhas\n\n- {} F-06: Periodic WorkManager Sync\n\n## Non-goals\n\n- Nao modificar ConnectivityMonitorService\n- Nao implementar sync incremental (Epic 11)"},
    {"title": "Epic 7: Pipeline CI/CD", "body": "## Resumo\n\nO projeto nao tem pipeline CI/CD (L03). Build, lint e testes rodam apenas localmente. No central: sem CI, testes nao bloqueiam regression, release continua manual, E2E nao automatiza.\n\n## Features Filhas\n\n- {} F-07: PR Validation Pipeline\n\n## Non-goals\n\n- Nao configurar coverage gate\n- Nao implementar SonarCloud"},
    {"title": "Epic 8: Cobertura de Testes", "body": "## Resumo\n\nCobertura de testes proxima de zero (L01). 18 bugs arquiteturais do v1.1.0 nao detectados por testes. Regression silenciosa a cada mudanca.\n\n## Features Filhas\n\n- {} F-08: Core Repository Unit Tests\n- {} F-09: ViewModel Unit Tests\n\n## Non-goals\n\n- Nao exigir coverage minima em CI\n- Nao escrever testes de UI instrumentados"},
    {"title": "Epic 9: Automacao E2E em CI", "body": "## Resumo\n\n7 fluxos Maestro E2E existem mas rodam manualmente (L13). Regression de fluxo passa despercebida ate teste manual.\n\n## Features Filhas\n\n- {} F-10: Maestro CI Integration\n\n## Non-goals\n\n- Nao modificar flows Maestro existentes\n- Nao criar novos flows"},
    {"title": "Epic 10: Release Automatizado", "body": "## Resumo\n\nProcesso de release manual (L06). Build, assinatura, upload dependem de maquina local. Keystore e ponto unico de falha.\n\n## Features Filhas\n\n- {} F-11: Automated Release Pipeline\n\n## Non-goals\n\n- Nao configurar beta distribution\n- Nao integrar Fastlane"},
    {"title": "Epic 11: Eficiencia de Sincronizacao", "body": "## Resumo\n\nSync atual e full-sync (L07). Merge union sobre conjuntos inteiros. Nao escala com listas de culto e historico.\n\n## Features Filhas\n\n- {} F-12: Incremental Data Sync\n\n## Non-goals\n\n- Nao modificar merge de favoritos\n- Nao introduzir novo banco local"},
    {"title": "Epic 12: Mitigacao de Bus Factor", "body": "## Resumo\n\nBus factor = 1 (L02). Keystore, acesso Play Console, conhecimento concentrados em uma pessoa. Se o mantenedor ficar indisponivel, o projeto para.\n\n## Features Filhas\n\n- {} F-13: Keystore and Access Recovery\n- {} F-14: Contributor Onboarding\n\n## Non-goals\n\n- Nao contratar novo dev (fora de escopo)"},
    {"title": "Epic 13: Conteudo da DiscoverScreen", "body": "## Resumo\n\nDiscoverScreen e placeholder vazio (L10). Uma das 4 abas principais sem conteudo util. Oportunidade perdida de engajamento.\n\n## Features Filhas\n\n- {} F-15: DiscoverScreen Editorial Content\n\n## Non-goals\n\n- Nao implementar recomendacao\n- Nao integrar API externa"},
]

features_data = [
    {"title": "F-01: Crashlytics Integration", "body": "## Feature: Crashlytics Integration\n\n**Part of:** Epic 1\n\n### Descricao\nAdicionar Firebase Crashlytics SDK, configurar inicializacao em LouveApp, adicionar reporting de erros nao-fatais nos servicos de sync.\n\n### User Stories Filhas\n- {} US 1.1: Add Crashlytics SDK dependency\n- {} US 1.2: Configure Crashlytics initialization\n- {} US 1.3: Add error reporting to sync services\n\n### Criterios de aceite\n- SDK integrado, crashes reportados, erros de sync registrados"},
    {"title": "F-02: Update Stale Documentation", "body": "## Feature: Update Stale Documentation\n\n**Part of:** Epic 2\n\n### Descricao\nReescrever Favoritos.md para refletir implementacao real do sistema hibrido de favoritos.\n\n### User Stories Filhas\n- {} US 2.1: Rewrite Favoritos.md\n\n### Criterios de aceite\n- Documento descreve DefaultFavoritesRepository, DataMigrationService, BidirectionalSyncService"},
    {"title": "F-03: Sync Status Indicator", "body": "## Feature: Sync Status Indicator\n\n**Part of:** Epic 3\n\n### Descricao\nCriar StateFlow<SyncStatus> no ConnectivityMonitorService e indicador visual no TopAppBar.\n\n### User Stories Filhas\n- {} US 3.1: Create sync status StateFlow\n- {} US 3.2: Add visual sync indicator to TopAppBar\n\n### Criterios de aceite\n- SyncStatus: Synced, Syncing, Offline, Error. Indicador com tooltip."},
    {"title": "F-04: Fix Domain Layer Violations", "body": "## Feature: Fix Domain Layer Violations\n\n**Part of:** Epic 4\n\n### Descricao\nMover AuthUiState para domain/model. Substituir DefaultTheme por constantes em domain.\n\n### User Stories Filhas\n- {} US 4.1: Move AuthUiState to domain layer\n- {} US 4.2: Replace DefaultTheme in data services\n\n### Criterios de aceite\n- Projeto compila, KMP desbloqueado"},
    {"title": "F-05: Remote Config Foundation", "body": "## Feature: Remote Config Foundation\n\n**Part of:** Epic 5\n\n### Descricao\nConfigurar Firebase Remote Config e criar wrapper FeatureFlag injetavel via Hilt.\n\n### User Stories Filhas\n- {} US 5.1: Configure Firebase Remote Config\n- {} US 5.2: Create FeatureFlag wrapper\n\n### Criterios de aceite\n- SDK integrado, wrapper injetavel, fallback offline"},
    {"title": "F-06: Periodic WorkManager Sync", "body": "## Feature: Periodic WorkManager Sync\n\n**Part of:** Epic 6\n\n### Descricao\nCriar e agendar PeriodicWorkRequest para sync em background.\n\n### User Stories Filhas\n- {} US 6.1: Create PeriodicSyncWorker\n- {} US 6.2: Schedule worker on app init\n\n### Criterios de aceite\n- Worker sync bidirecional, agendamento idempotente"},
    {"title": "F-07: PR Validation Pipeline", "body": "## Feature: PR Validation Pipeline\n\n**Part of:** Epic 7\n\n### Descricao\nWorkflows GitHub Actions para build, lint e testes em cada PR.\n\n### User Stories Filhas\n- {} US 7.1: Build check workflow\n- {} US 7.2: Lint check workflow\n- {} US 7.3: Unit test workflow\n\n### Criterios de aceite\n- Build, lint e test checks bloqueantes em PR"},
    {"title": "F-08: Core Repository Unit Tests", "body": "## Feature: Core Repository Unit Tests\n\n**Part of:** Epic 8\n\n### Descricao\nTestes unitarios para repositories e servicos de sync.\n\n### User Stories Filhas\n- {} US 8.1: Test repositories and sync services\n\n### Criterios de aceite\n- HymnRepositoryImpl, DefaultFavoritesRepository, DataMigrationService, BidirectionalSyncService testados"},
    {"title": "F-09: ViewModel Unit Tests", "body": "## Feature: ViewModel Unit Tests\n\n**Part of:** Epic 8\n\n### Descricao\nTestes unitarios para ViewModels criticos com MockK.\n\n### User Stories Filhas\n- {} US 9.1: Test ViewModels\n\n### Criterios de aceite\n- HomeViewModel, HymnDetailViewModel, AuthViewModel testados"},
    {"title": "F-10: Maestro CI Integration", "body": "## Feature: Maestro CI Integration\n\n**Part of:** Epic 9\n\n### Descricao\nWorkflow GitHub Actions executando Maestro flows em emulador.\n\n### User Stories Filhas\n- {} US 10.1: Configure Maestro workflow in CI\n\n### Criterios de aceite\n- Workflow funcional nightly ou manual"},
    {"title": "F-11: Automated Release Pipeline", "body": "## Feature: Automated Release Pipeline\n\n**Part of:** Epic 10\n\n### Descricao\nPipeline para build AAB signed, upload Play Console, GitHub Release.\n\n### User Stories Filhas\n- {} US 11.1: Build signed AAB and upload\n\n### Criterios de aceite\n- Pipeline funcional em tag v*"},
    {"title": "F-12: Incremental Data Sync", "body": "## Feature: Incremental Data Sync\n\n**Part of:** Epic 11\n\n### Descricao\nSync incremental com diff tracking por timestamps.\n\n### User Stories Filhas\n- {} US 12.1: Implement incremental sync\n\n### Criterios de aceite\n- Sync incremental funcional, compativel WorkManager"},
    {"title": "F-13: Keystore and Access Recovery", "body": "## Feature: Keystore and Access Recovery\n\n**Part of:** Epic 12\n\n### Descricao\nDocumentar e proteger o keystore de release.\n\n### User Stories Filhas\n- {} US 13.1: Backup keystore and document recovery\n\n### Criterios de aceite\n- Processo documentado, backup valido"},
    {"title": "F-14: Contributor Onboarding", "body": "## Feature: Contributor Onboarding\n\n**Part of:** Epic 12\n\n### Descricao\nGuia completo de onboarding para novos contribuidores.\n\n### User Stories Filhas\n- {} US 14.1: Create contributor onboarding guide\n\n### Criterios de aceite\n- Guia cobre setup, testes, contribuicao, arquitetura"},
    {"title": "F-15: DiscoverScreen Editorial Content", "body": "## Feature: DiscoverScreen Editorial Content\n\n**Part of:** Epic 13\n\n### Descricao\nConteudo estatico curado na tela Descubra.\n\n### User Stories Filhas\n- {} US 15.1: Add curated content to DiscoverScreen\n\n### Criterios de aceite\n- Categorias, hinos em destaque, texto introdutorio"},
]

uss_data = [
    {"title": "US 1.1: Add Crashlytics SDK dependency", "body": "## US 1.1: Add Crashlytics SDK dependency\n\n**Parent:** F-01\n\n### Descricao\nAdicionar implementation(platform(libs.firebase.crashlytics)) e implementation(libs.firebase.crashlytics.ktx) ao app/build.gradle.kts.\n\n### Criterios de aceite\n- Dependencias adicionadas, projeto compila"},
    {"title": "US 1.2: Configure Crashlytics initialization", "body": "## US 1.2: Configure Crashlytics initialization\n\n**Parent:** F-01\n\n### Descricao\nInicializar FirebaseCrashlytics em LouveApp.onCreate(). setCrashlyticsCollectionEnabled(true). Testar com recordException.\n\n### Criterios de aceite\n- Crashlytics inicializado no startup"},
    {"title": "US 1.3: Add error reporting to sync services", "body": "## US 1.3: Add error reporting to sync services\n\n**Parent:** F-01\n\n### Descricao\nrecordException nos try-catch de DataMigrationService, BidirectionalSyncService, ConnectivityMonitorService.\n\n### Criterios de aceite\n- Erros de sync registrados como non-fatal"},
    {"title": "US 2.1: Rewrite Favoritos.md", "body": "## US 2.1: Rewrite Favoritos.md\n\n**Parent:** F-02\n\n### Descricao\nSubstituir docs/Favoritos.md com documento do sistema hibrido: DefaultFavoritesRepository, DataStoreLocalFavoritesRepository, FirestoreUserRepositoryImpl, DataMigrationService, BidirectionalSyncService, estrategias de merge.\n\n### Criterios de aceite\n- Documento reflete implementacao real"},
    {"title": "US 3.1: Create sync status StateFlow", "body": "## US 3.1: Create sync status StateFlow\n\n**Parent:** F-03\n\n### Descricao\nMutableStateFlow<SyncStatus> no ConnectivityMonitorService. SyncStatus: Synced, Syncing, Offline, Error.\n\n### Criterios de aceite\n- StateFlow exposto e atualizado"},
    {"title": "US 3.2: Add visual sync indicator to TopAppBar", "body": "## US 3.2: Add visual sync indicator to TopAppBar\n\n**Parent:** F-03\n\n### Descricao\nSyncIndicator no HomeTopAppBar com icone+cor. Tooltip: Sincronizado, Sincronizando..., Offline, Erro.\n\n### Criterios de aceite\n- Indicador visivel com tooltip"},
    {"title": "US 4.1: Move AuthUiState to domain layer", "body": "## US 4.1: Move AuthUiState to domain layer\n\n**Parent:** F-04\n\n### Descricao\nMover AuthUiState.kt de ui/common/state/ para domain/model/. Atualizar imports.\n\n### Criterios de aceite\n- Projeto compila"},
    {"title": "US 4.2: Replace DefaultTheme in data services", "body": "## US 4.2: Replace DefaultTheme in data services\n\n**Parent:** F-04\n\n### Descricao\nSubstituir DefaultTheme.name por ThemeDefaults.THEME_NAME em DataMigrationService e BidirectionalSyncService.\n\n### Criterios de aceite\n- Projeto compila, merge inalterado"},
    {"title": "US 5.1: Configure Firebase Remote Config", "body": "## US 5.1: Configure Firebase Remote Config\n\n**Parent:** F-05\n\n### Descricao\nimplementation(libs.firebase.config.ktx). @Provides FirebaseRemoteConfig. Fetch em LouveApp.onCreate.\n\n### Criterios de aceite\n- SDK integrado, Provider Hilt configurado"},
    {"title": "US 5.2: Create FeatureFlag wrapper", "body": "## US 5.2: Create FeatureFlag wrapper\n\n**Parent:** F-05\n\n### Descricao\nFeatureFlagKey enum no domain. FeatureFlagProvider @Singleton @Inject: isEnabled(key, default). Cache com TTL.\n\n### Criterios de aceite\n- Wrapper injetavel, fallback offline"},
    {"title": "US 6.1: Create PeriodicSyncWorker", "body": "## US 6.1: Create PeriodicSyncWorker\n\n**Parent:** F-06\n\n### Descricao\nPeriodicSyncWorker : CoroutineWorker(). Sync bidirecional. NetworkType.CONNECTED.\n\n### Criterios de aceite\n- Worker funcional"},
    {"title": "US 6.2: Schedule worker on app init", "body": "## US 6.2: Schedule worker on app init\n\n**Parent:** F-06\n\n### Descricao\nenqueueUniquePeriodicWork em LouveApp.onCreate try-catch.\n\n### Criterios de aceite\n- Agendamento idempotente"},
    {"title": "US 7.1: Build check workflow", "body": "## US 7.1: Build check workflow\n\n**Parent:** F-07\n\n### Descricao\n.github/workflows/pr-build.yaml: checkout, setup-java 17, ./gradlew assembleDebug.\n\n### Criterios de aceite\n- Workflow funcional"},
    {"title": "US 7.2: Lint check workflow", "body": "## US 7.2: Lint check workflow\n\n**Parent:** F-07\n\n### Descricao\nStep: ./gradlew detekt/ktlintCheck. Baseline para legado. Falha bloqueia.\n\n### Criterios de aceite\n- Lint executado e bloqueante"},
    {"title": "US 7.3: Unit test workflow", "body": "## US 7.3: Unit test workflow\n\n**Parent:** F-07\n\n### Descricao\nStep: ./gradlew test. Falha -> check fail. Upload artifact reports/.\n\n### Criterios de aceite\n- Testes executados, relatorio disponivel"},
    {"title": "US 8.1: Test repositories and sync services", "body": "## US 8.1: Test repositories and sync services\n\n**Parent:** F-08\n\n### Descricao\nTestes com MockK para HymnRepositoryImpl, DefaultFavoritesRepository, DefaultSettingsRepository, DataMigrationService, BidirectionalSyncService.\n\n### Criterios de aceite\n- Todos os repositorios e servicos cobertos"},
    {"title": "US 9.1: Test ViewModels", "body": "## US 9.1: Test ViewModels\n\n**Parent:** F-09\n\n### Descricao\nTestes com MockK: HomeViewModel (search, debounce), HymnDetailViewModel (favorite, font), AuthViewModel (signIn, signOut, rate limiting).\n\n### Criterios de aceite\n- ViewModels criticos testados"},
    {"title": "US 10.1: Configure Maestro workflow in CI", "body": "## US 10.1: Configure Maestro workflow in CI\n\n**Parent:** F-10\n\n### Descricao\nWorkflow com emulador Android, executa flows Maestro criticos. Nightly ou manual.\n\n### Criterios de aceite\n- Workflow funcional"},
    {"title": "US 11.1: Build signed AAB and upload", "body": "## US 11.1: Build signed AAB and upload\n\n**Parent:** F-11\n\n### Descricao\nWorkflow: build AAB signed, upload Play Console via Publisher API, GitHub Release. Tag v*.\n\n### Criterios de aceite\n- Pipeline funcional"},
    {"title": "US 12.1: Implement incremental sync", "body": "## US 12.1: Implement incremental sync\n\n**Parent:** F-12\n\n### Descricao\nDiff tracking com timestamps. Sync apenas records modificados. Migrar BidirectionalSyncService.\n\n### Criterios de aceite\n- Sync incremental funcional"},
    {"title": "US 13.1: Backup keystore and document recovery", "body": "## US 13.1: Backup keystore and document recovery\n\n**Parent:** F-13\n\n### Descricao\nDocumentar recuperacao. Backup em cofre seguro. Validar que assina build.\n\n### Criterios de aceite\n- Processo documentado, backup valido"},
    {"title": "US 14.1: Create contributor onboarding guide", "body": "## US 14.1: Create contributor onboarding guide\n\n**Parent:** F-14\n\n### Descricao\nGuia: setup JDK/Android SDK, compilar, testes, Maestro, contribuicao, arquitetura.\n\n### Criterios de aceite\n- Guia publicado"},
    {"title": "US 15.1: Add curated content to DiscoverScreen", "body": "## US 15.1: Add curated content to DiscoverScreen\n\n**Parent:** F-15\n\n### Descricao\nConteudo estatico: cards de categorias, hinos em destaque, texto introdutorio. Dados compilados em Kotlin.\n\n### Criterios de aceite\n- Conteudo visivel na tela Descubra"},
]

epic_feat_map = [[0],[1],[2],[3],[4],[5],[6],[7,8],[9],[10],[11],[12,13],[14]]
feat_us_map = [[0,1,2],[3],[4,5],[6,7],[8,9],[10,11],[12,13,14],[15],[16],[17],[18],[19],[20],[21],[22]]

url_pattern = re.compile(r'github\.com/[^/]+/[^/]+/issues/(\d+)')

def create_issue(title, body, label):
    r = subprocess.run(['gh', 'issue', 'create', '--title', title, '--body', body, '--label', label], capture_output=True, text=True)
    m = url_pattern.search(r.stdout)
    if m:
        return int(m.group(1))
    raise ValueError(f"Could not extract number from: {r.stdout} {r.stderr}")

print("=== Step 2: Create Epics ===")
epic_nums = []
for e in epics_data:
    num = create_issue(e['title'], e['body'], 'epic')
    epic_nums.append(num)
    print(f"  #{num}: {e['title']}")
    time.sleep(0.5)

print("=== Step 3: Create Features ===")
feat_nums = []
for f in features_data:
    num = create_issue(f['title'], f['body'], 'feature')
    feat_nums.append(num)
    print(f"  #{num}: {f['title']}")
    time.sleep(0.5)

print("=== Step 4: Create User Stories ===")
us_nums = []
for u in uss_data:
    num = create_issue(u['title'], u['body'], 'user-story')
    us_nums.append(num)
    print(f"  #{num}: {u['title']}")
    time.sleep(0.5)

print("=== Step 5: Update Epic bodies ===")
for ei, f_indices in enumerate(epic_feat_map):
    body = epics_data[ei]['body']
    for fi in f_indices:
        body = body.replace('{}', f'- [ ] #{feat_nums[fi]} - {features_data[fi]["title"]}', 1)
    body = re.sub(r'\{[^}]*\}', '', body)
    body = re.sub(r'\n{3,}', '\n\n', body)
    subprocess.run(['gh', 'issue', 'edit', str(epic_nums[ei]), '--body', body], capture_output=True)
    print(f"  Updated #{epic_nums[ei]}")

print("=== Step 6: Update Feature bodies ===")
for fi, us_indices in enumerate(feat_us_map):
    body = features_data[fi]['body']
    for ui in us_indices:
        body = body.replace('{}', f'- [ ] #{us_nums[ui]} - {uss_data[ui]["title"]}', 1)
    body = re.sub(r'\{[^}]*\}', '', body)
    body = re.sub(r'\n{3,}', '\n\n', body)
    subprocess.run(['gh', 'issue', 'edit', str(feat_nums[fi]), '--body', body], capture_output=True)
    print(f"  Updated #{feat_nums[fi]}")

print(f"\n=== DONE ===")
print(f"Epics: {epic_nums}")
print(f"Features: {feat_nums}")
print(f"US: {us_nums}")
print(f"Total: {len(epic_nums)} Epics + {len(feat_nums)} Features + {len(us_nums)} US = {len(epic_nums)+len(feat_nums)+len(us_nums)} issues")