import subprocess, re, json, time

# ============================================================
# Nova hierarquia: 13 Epics (mantidos #113-#125)
# 29 Features, 57 US
# Cada Epic -> 2-3 Features, cada Feature -> 2-4 US
# ============================================================

# (feature_title, feature_body) por epic
# US: (us_title, us_body) por feature

structure = [
    # Epic 1: Observabilidade - Crashlytics (L04)
    {"epic_idx": 0, "features": [
        {"title": "F1.1: Crashlytics SDK Integration", "body": "## Feature: Crashlytics SDK Integration\n\n**Part of:** Epic 1\n\n### Descricao\nAdicionar Firebase Crashlytics SDK e configurar inicializacao no LouveApp.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- SDK integrado e compilando\n- Crashlytics inicializado no startup",
         "uss": [
            {"title": "US 1.1.1: Adicionar dependencias do Crashlytics", "body": "## US 1.1.1\n\n**Parent:** F1.1\n\nAdicionar implementation(platform(libs.firebase.crashlytics)) e libs.firebase.crashlytics.ktx no app/build.gradle.kts. Sincronizar e compilar."},
            {"title": "US 1.1.2: Configurar inicializacao no LouveApp", "body": "## US 1.1.2\n\n**Parent:** F1.1\n\nInicializar FirebaseCrashlytics em LouveApp.onCreate(). setCrashlyticsCollectionEnabled(true). Testar com crash forcado em debug."},
         ]},
        {"title": "F1.2: Error Reporting nos Sync Services", "body": "## Feature: Error Reporting nos Sync Services\n\n**Part of:** Epic 1\n\n### Descricao\nAdicionar reporting de erros nao-fatais nos servicos de sync para monitorar falhas silenciosas.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- Erros de sync categorizados por tipo\n- Nenhum erro silencioso escapa aos observadores",
         "uss": [
            {"title": "US 1.2.1: Logging no DataMigrationService", "body": "## US 1.2.1\n\n**Parent:** F1.2\n\nNos blocos try-catch do DataMigrationService, adicionar recordException(e) com categoria (network/migracao). Preservar logging existente."},
            {"title": "US 1.2.2: Logging no BidirectionalSyncService", "body": "## US 1.2.2\n\n**Parent:** F1.2\n\nAdicionar recordException(e) nos pontos de falha do BidirectionalSyncService, categorizando por tipo de sync."},
            {"title": "US 1.2.3: Logging no ConnectivityMonitorService", "body": "## US 1.2.3\n\n**Parent:** F1.2\n\nAdicionar recordException(e) nas falhas de connectivity e sync automatico do ConnectivityMonitorService."},
         ]},
    ]},
    # Epic 2: Higiene de Documentacao (L12)
    {"epic_idx": 1, "features": [
        {"title": "F2.1: Reescrever Favoritos.md", "body": "## Feature: Reescrever Favoritos.md\n\n**Part of:** Epic 2\n\n### Descricao\nSubstituir o documento obsoleto por um que reflita o sistema hibrido real.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- Documento descreve implementacao real",
         "uss": [
            {"title": "US 2.1.1: Escrever corpo do novo Favoritos.md", "body": "## US 2.1.1\n\n**Parent:** F2.1\n\nRedigir novo Favoritos.md: DefaultFavoritesRepository como mediador, DataStoreLocalFavoritesRepository, FirestoreUserRepositoryImpl, estrategias de merge (union favoritos, preferencia local tema)."},
            {"title": "US 2.1.2: Remover referencias a planejamento pre-implementacao", "body": "## US 2.1.2\n\n**Parent:** F2.1\n\nEliminar secoes que descrevem funcionalidades nao implementadas. Garantir que todos os fluxos citados existem no codigo atual."},
         ]},
        {"title": "F2.2: Alinhar referencias cruzadas", "body": "## Feature: Alinhar referencia cruzadas\n\n**Part of:** Epic 2\n\n### Descricao\nAtualizar demais documentos que descrevem favoritos/autenticacao para nao contradizer a implementacao.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- docs/10 e docs/11 consistentes com o codigo",
         "uss": [
            {"title": "US 2.2.1: Atualizar docs/10 autenticacao e persistencia", "body": "## US 2.2.1\n\n**Parent:** F2.2\n\nRevisar docs/10 (Autenticacao e Persistencia) verificando coerencia com FirestoreUserRepositoryImpl e mediadores atuais."},
            {"title": "US 2.2.2: Atualizar docs/11 migracao e sync", "body": "## US 2.2.2\n\n**Parent:** F2.2\n\nRevisar docs/11 (Migracao e Sync) para refletir o pipeline atual de 3 servicos e union merge."},
         ]},
    ]},
    # Epic 3: Feedback de Sincronizacao (L08)
    {"epic_idx": 2, "features": [
        {"title": "F3.1: Estado de Sincronizacao", "body": "## Feature: Estado de Sincronizacao\n\n**Part of:** Epic 3\n\n### Descricao\nExpor o estado de sync como StateFlow consumivel pela UI.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- StateFlow<SyncStatus> exposto",
         "uss": [
            {"title": "US 3.1.1: Criar SyncStatus enum e StateFlow", "body": "## US 3.1.1\n\n**Parent:** F3.1\n\nCriar enum SyncStatus {Synced, Syncing, Offline, Error}. Adicionar MutableStateFlow no ConnectivityMonitorService exposto via asStateFlow()."},
            {"title": "US 3.1.2: Atualizar estado nos callbacks de rede", "body": "## US 3.1.2\n\n**Parent:** F3.1\n\nAtualizar _syncStatus em onAvailable, onLost, onCapabilitiesChanged e inicio/fim de sync."},
         ]},
        {"title": "F3.2: Indicador Visual na UI", "body": "## Feature: Indicador Visual na UI\n\n**Part of:** Epic 3\n\n### Descricao\nRenderizar o status de sync no TopAppBar com feedback claro.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- Indicador visivel e responsivo",
         "uss": [
            {"title": "US 3.2.1: Criar componente SyncIndicator", "body": "## US 3.2.1\n\n**Parent:** F3.2\n\nComposable SyncIndicator: icone e cor por estado (check/relogio/cloud-off/alerta). Tooltip com texto descritivo."},
            {"title": "US 3.2.2: Integrar no HomeTopAppBar", "body": "## US 3.2.2\n\n**Parent:** F3.2\n\nInjetar SyncIndicator no HomeTopAppBar consumindo collectAsState do ConnectivityMonitorService."},
         ]},
    ]},
    # Epic 4: Clean Architecture Enforcement (L11) - 3 features por ADR-006 (5 violacoes)
    {"epic_idx": 3, "features": [
        {"title": "F4.1: AuthUiState/AuthError para domain", "body": "## Feature: AuthUiState/AuthError para domain\n\n**Part of:** Epic 4\n\n### Descricao\nResolver violacoes 1 e 2 do ADR-006: mover AuthUiState e AuthError para domain.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- domain sem dependencia de ui",
         "uss": [
            {"title": "US 4.1.1: Mover AuthUiState.kt para domain/model", "body": "## US 4.1.1\n\n**Parent:** F4.1\n\nMover AuthUiState.kt de ui para domain/model. Tipos sao Kotlin puro, sem mudanca de conteudo."},
            {"title": "US 4.1.2: Atualizar referencias em AuthRepository e FirebaseAuthRepositoryImpl", "body": "## US 4.1.2\n\n**Parent:** F4.1\n\nAtualizar imports em AuthRepository (domain), FirebaseAuthRepositoryImpl (data), AuthViewModel e telas. Verificar compilacao."},
         ]},
        {"title": "F4.2: DefaultTheme para constantes do domain", "body": "## Feature: DefaultTheme para constantes do domain\n\n**Part of:** Epic 4\n\n### Descricao\nResolver violacoes 3 e 4 do ADR-006: substituir importacoes de DefaultTheme nos servicos de dados.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- data sem import de ui/theme",
         "uss": [
            {"title": "US 4.2.1: Substituir DefaultTheme no DataMigrationService", "body": "## US 4.2.1\n\n**Parent:** F4.2\n\nTrocar DefaultTheme.name por ThemeDefaults.THEME_NAME (domain/model). Manter logica de merge de tema."},
            {"title": "US 4.2.2: Substituir DefaultTheme no BidirectionalSyncService", "body": "## US 4.2.2\n\n**Parent:** F4.2\n\nMesma substituicao no BidirectionalSyncService. Remover import de ui/theme."},
         ]},
        {"title": "F4.3: HomeViewModel com DI Hilt", "body": "## Feature: HomeViewModel com DI Hilt\n\n**Part of:** Epic 4\n\n### Descricao\nResolver violacao 5 do ADR-006: HomeViewModel criando HymnRepositoryImpl() diretamente.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- HomeViewModel injeta HymnRepository",
         "uss": [
            {"title": "US 4.3.1: Injetar HymnRepository via constructor", "body": "## US 4.3.1\n\n**Parent:** F4.3\n\nReceber HymnRepository no constructor do HomeViewModel (@Inject). HymnRepositoryImpl ja e provided no AppModule."},
            {"title": "US 4.3.2: Atualizar testes do HomeViewModel", "body": "## US 4.3.2\n\n**Parent:** F4.3\n\nAjustar HomeViewModelTest para passar mock/fake de HymnRepository no constructor."},
         ]},
    ]},
    # Epic 5: Gestao de Funcionalidades (L05)
    {"epic_idx": 4, "features": [
        {"title": "F5.1: Infraestrutura Remote Config", "body": "## Feature: Infraestrutura Remote Config\n\n**Part of:** Epic 5\n\n### Descricao\nIntegrar Firebase Remote Config e expor via Hilt.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- SDK integrado e provider Hilt",
         "uss": [
            {"title": "US 5.1.1: Adicionar SDK e provider", "body": "## US 5.1.1\n\n**Parent:** F5.1\n\nimplementation(libs.firebase.config.ktx). @Provides FirebaseRemoteConfig.getInstance() no AppModule."},
            {"title": "US 5.1.2: Configurar fetch no LouveApp", "body": "## US 5.1.2\n\n**Parent:** F5.1\n\nFetch no LouveApp.onCreate com minimumFetchInterval para dev. Timeout default para offline."},
         ]},
        {"title": "F5.2: Wrapper FeatureFlag", "body": "## Feature: Wrapper FeatureFlag\n\n**Part of:** Epic 5\n\n### Descricao\nWrapper injetavel para leitura de flags com fallback local.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- isEnabled(key, default) funcional",
         "uss": [
            {"title": "US 5.2.1: Criar FeatureFlagKey enum", "body": "## US 5.2.1\n\n**Parent:** F5.2\n\nEnum FeatureFlagKey { HABILITAR_CATEGORIZACAO, ... } com keys padronizadas no domain."},
            {"title": "US 5.2.2: Criar FeatureFlagProvider", "body": "## US 5.2.2\n\n**Parent:** F5.2\n\nFeatureFlagProvider @Singleton @Inject: isEnabled(key, default). Ler RemoteConfig, fallback para default, cache em memoria com TTL."},
         ]},
    ]},
    # Epic 6: Sincronizacao em Background (L09)
    {"epic_idx": 5, "features": [
        {"title": "F6.1: PeriodicSyncWorker", "body": "## Feature: PeriodicSyncWorker\n\n**Part of:** Epic 6\n\n### Descricao\nWorker periodico para sync bidirecional em background.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- Worker executa sync corretamente",
         "uss": [
            {"title": "US 6.1.1: Implementar PeriodicSyncWorker", "body": "## US 6.1.1\n\n**Parent:** F6.1\n\nCoroutineWorker com doWork(): BidirectionalSyncService.syncRemoteToLocal() e DataMigrationService.migrateLocalDataToCloud() se pendente."},
            {"title": "US 6.1.2: Configurar constraints e periodo", "body": "## US 6.1.2\n\n**Parent:** F6.1\n\nPeriodicWorkRequestBuilder com 6h e flex 2h. Constraints NetworkType.CONNECTED."},
         ]},
        {"title": "F6.2: Agendamento no aplicativo", "body": "## Feature: Agendamento no aplicativo\n\n**Part of:** Epic 6\n\n### Descricao\nAgendar e garantir idempotencia do worker.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- Agendamento unico e seguro",
         "uss": [
            {"title": "US 6.2.1: Agendar via enqueueUniquePeriodicWork", "body": "## US 6.2.1\n\n**Parent:** F6.2\n\nEm LouveApp.onCreate, enqueueUniquePeriodicWork(\"periodic_sync\", KEEP, request). Envolver em try-catch."},
            {"title": "US 6.2.2: Verificar agendamento", "body": "## US 6.2.2\n\n**Parent:** F6.2\n\nValidar com adb shell dumpsys jobscheduler que o worker esta registrado e nao duplica."},
         ]},
    ]},
    # Epic 7: Pipeline CI/CD (L03)
    {"epic_idx": 6, "features": [
        {"title": "F7.1: Pipeline de Build na CI", "body": "## Feature: Pipeline de Build na CI\n\n**Part of:** Epic 7\n\n### Descricao\nWorkflow base que compila o projeto em cada PR.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- assembleDebug rodando na CI",
         "uss": [
            {"title": "US 7.1.1: Workflow pr-build.yaml", "body": "## US 7.1.1\n\n**Parent:** F7.1\n\n.github/workflows/pr-build.yaml em pull_request para develop/main: checkout, setup-java 17, ./gradlew assembleDebug."},
            {"title": "US 7.1.2: Caching e otimizacao", "body": "## US 7.1.2\n\n**Parent:** F7.1\n\nConfigurar gradle-build-action com caching de dependencias e build artifacts."},
         ]},
        {"title": "F7.2: Quality Gates (lint + testes)", "body": "## Feature: Quality Gates (lint + testes)\n\n**Part of:** Epic 7\n\n### Descricao\nLint e testes unitarios bloqueantes em cada PR.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- Lint e testes bloqueiam merge",
         "uss": [
            {"title": "US 7.2.1: Lint check com detekt", "body": "## US 7.2.1\n\n**Parent:** F7.2\n\n./gradlew detekt com baseline para codigo legado. Falha bloqueia o PR."},
            {"title": "US 7.2.2: Unit test check", "body": "## US 7.2.2\n\n**Parent:** F7.2\n\n./gradlew test no CI. Falha em qualquer teste -> check fail. Upload de artifact app/build/reports/tests/."},
         ]},
    ]},
    # Epic 8: Cobertura de Testes (L01)
    {"epic_idx": 7, "features": [
        {"title": "F8.1: Testes de Repositorios", "body": "## Feature: Testes de Repositorios\n\n**Part of:** Epic 8\n\n### Descricao\nCobertura unitaria das implementacoes de repositorio.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- Repositorios principais testados",
         "uss": [
            {"title": "US 8.1.1: Testar HymnRepositoryImpl", "body": "## US 8.1.1\n\n**Parent:** F8.1\n\nTestes para getAllHymns() e getHymnById() com dataset real do HymnDataSource."},
            {"title": "US 8.1.2: Testar DefaultFavoritesRepository e DefaultSettingsRepository", "body": "## US 8.1.2\n\n**Parent:** F8.1\n\nMockK: logica de mediacao local/remoto em add/remove/getFavorites e theme/font."},
            {"title": "US 8.1.3: Testar DataMigrationService", "body": "## US 8.1.3\n\n**Parent:** F8.1\n\nCenarios: migracao local->cloud, skip quando vazio, merge union, erro."},
         ]},
        {"title": "F8.2: Testes de Servicos de Sync", "body": "## Feature: Testes de Servicos de Sync\n\n**Part of:** Epic 8\n\n### Descricao\nCobertura dos servicos de sincronizacao.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- Servicos de sync testados",
         "uss": [
            {"title": "US 8.2.1: Testar BidirectionalSyncService", "body": "## US 8.2.1\n\n**Parent:** F8.2\n\nCenarios: syncRemoteToLocal add/remove, sync de tema, deteccao e resolucao de conflitos."},
            {"title": "US 8.2.2: Testar ConnectivityMonitorService", "body": "## US 8.2.2\n\n**Parent:** F8.2\n\nTestar transicoes de estado e disparo de sync em onAvailable/onLost."},
         ]},
        {"title": "F8.3: Testes de ViewModels", "body": "## Feature: Testes de ViewModels\n\n**Part of:** Epic 8\n\n### Descricao\nCobertura dos ViewModels criticos.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- ViewModels criticos testados",
         "uss": [
            {"title": "US 8.3.1: Testar HomeViewModel", "body": "## US 8.3.1\n\n**Parent:** F8.3\n\nBusca: filtro por titulo/numero/coro, multi-palavra, acentos, debounce, clear."},
            {"title": "US 8.3.2: Testar HymnDetailViewModel", "body": "## US 8.3.2\n\n**Parent:** F8.3\n\nsetHymnId, toggleFavorite (otimista + rollback), ajuste de fonte 0.5-2.0."},
            {"title": "US 8.3.3: Testar AuthViewModel", "body": "## US 8.3.3\n\n**Parent:** F8.3\n\nEstado inicial idle, signIn/signOut, rate limiting de 2s, retry com backoff."},
         ]},
    ]},
    # Epic 9: Automacao E2E em CI (L13)
    {"epic_idx": 8, "features": [
        {"title": "F9.1: Runner E2E na CI", "body": "## Feature: Runner E2E na CI\n\n**Part of:** Epic 9\n\n### Descricao\nExecucao dos flows Maestro em emulador no GitHub Actions.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- Flows Maestro executam na CI",
         "uss": [
            {"title": "US 9.1.1: Subir emulador e instalar APK", "body": "## US 9.1.1\n\n**Parent:** F9.1\n\nreactivecircus/android-emulator-runner API 34, instalar APK debug, aguardar boot."},
            {"title": "US 9.1.2: Executar flows criticos", "body": "## US 9.1.2\n\n**Parent:** F9.1\n\nExecutar 00_splash_and_home, 01_bottom_nav_tabs, 06_search_flow como smoke minimo."},
         ]},
        {"title": "F9.2: Relatorios e Gatilho", "body": "## Feature: Relatorios e Gatilho\n\n**Part of:** Epic 9\n\n### Descricao\nRelatorios detalhados e politica de execucao.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- Resultados visiveis e acionaveis",
         "uss": [
            {"title": "US 9.2.1: Publicar relatorio JUnit", "body": "## US 9.2.1\n\n**Parent:** F9.2\n\nUpload de maestro_reports/ como artifact e comentario com resultado no PR."},
            {"title": "US 9.2.2: Politica de execucao", "body": "## US 9.2.2\n\n**Parent:** F9.2\n\nWorkflow nightly + manual dispatch. Nao-bloqueante em PR (custo do emulador), mas reportado."},
         ]},
    ]},
    # Epic 10: Release Automatizado (L06)
    {"epic_idx": 9, "features": [
        {"title": "F10.1: Build Assinado", "body": "## Feature: Build Assinado\n\n**Part of:** Epic 10\n\n### Descricao\nGerar AAB assinado na CI com keystore protegido.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- AAB assinado sem intervencao manual",
         "uss": [
            {"title": "US 10.1.1: Armazenar keystore em secrets", "body": "## US 10.1.1\n\n**Parent:** F10.1\n\nKEYSORE_BASE64, KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD como GitHub Secrets. Documentar no SECURITY."},
            {"title": "US 10.1.2: Workflow assembleRelease assinado", "body": "## US 10.1.2\n\n**Parent:** F10.1\n\nWorkflow release: decodificar keystore, gerar keystore.properties, ./gradlew bundleRelease."},
         ]},
        {"title": "F10.2: Upload Google Play Console", "body": "## Feature: Upload Google Play Console\n\n**Part of:** Epic 10\n\n### Descricao\nUpload automatico do AAB ao Play Console.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- Upload via Publisher API",
         "uss": [
            {"title": "US 10.2.1: Configurar service account", "body": "## US 10.2.1\n\n**Parent:** F10.2\n\nCriar service account no Google Cloud com papel Play Console, exportar JSON e armazenar como secret."},
            {"title": "US 10.2.2: Integrar upload no workflow", "body": "## US 10.2.2\n\n**Parent:** F10.2\n\nUsar action de Google Play upload (ou fastlane) para enviar AAB ao track interno."},
         ]},
        {"title": "F10.3: GitHub Release", "body": "## Feature: GitHub Release\n\n**Part of:** Epic 10\n\n### Descricao\nGerar release no GitHub com changelog.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- Release automatizado em tag v*",
         "uss": [
            {"title": "US 10.3.1: Criar release com changelog", "body": "## US 10.3.1\n\n**Parent:** F10.3\n\ngh release create a partir do CHANGELOG.md para a versao taggeada."},
            {"title": "US 10.3.2: Trigger em tag push", "body": "## US 10.3.2\n\n**Parent:** F10.3\n\nWorkflow dispara em push de tag v* e encadeia build->upload->release."},
         ]},
    ]},
    # Epic 11: Eficiencia de Sincronizacao (L07)
    {"epic_idx": 10, "features": [
        {"title": "F11.1: Change Tracking", "body": "## Feature: Change Tracking\n\n**Part of:** Epic 11\n\n### Descricao\nRastrear modificacoes para sincronizar apenas o que mudou.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- Tracking de timestamps funcional",
         "uss": [
            {"title": "US 11.1.1: Timestamps de ultima modificacao", "body": "## US 11.1.1\n\n**Parent:** F11.1\n\nManter updatedAt em favoritos/settings (Firestore + DataStore) para diff."},
            {"title": "US 11.1.2: Estado do ultimo sync", "body": "## US 11.1.2\n\n**Parent:** F11.1\n\nPersistir lastSyncAt local para calcular deltas desde o ultimo sync."},
         ]},
        {"title": "F11.2: Merge Incremental", "body": "## Feature: Merge Incremental\n\n**Part of:** Epic 11\n\n### Descricao\nMigrar servicos de full-sync para merge incremental.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- BidirectionalSyncService incremental",
         "uss": [
            {"title": "US 11.2.1: Migrar BidirectionalSyncService", "body": "## US 11.2.1\n\n**Parent:** F11.2\n\nSubstituir union full por diff: sincronizar apenas records com updatedAt > lastSyncAt, aplicando resolveConflicts."},
            {"title": "US 11.2.2: Compatibilidade com WorkManager", "body": "## US 11.2.2\n\n**Parent:** F11.2\n\nGarantir que o sync incremental funciona dentro do PeriodicSyncWorker sem corrupcao de estado."},
         ]},
    ]},
    # Epic 12: Mitigacao de Bus Factor (L02)
    {"epic_idx": 11, "features": [
        {"title": "F12.1: Keystore Recovery", "body": "## Feature: Keystore Recovery\n\n**Part of:** Epic 12\n\n### Descricao\nProteger o unico ativo que permite atualizar o app na Play Store.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- Processo documentado e backup valido",
         "uss": [
            {"title": "US 12.1.1: Documentar processo de recuperacao", "body": "## US 12.1.1\n\n**Parent:** F12.1\n\nGuia de recuperacao: localizacao do keystore, quem acessa, como re-registrar chave na Play Console, plano B (app signing exige Google)."},
            {"title": "US 12.1.2: Backup em cofre seguro", "body": "## US 12.1.2\n\n**Parent:** F12.1\n\nBackup do keystore + senhas em cofre (1Password ou GitHub encrypted). Validar que o backup assina um build de teste."},
         ]},
        {"title": "F12.2: Contributor Onboarding", "body": "## Feature: Contributor Onboarding\n\n**Part of:** Epic 12\n\n### Descricao\nPorta de entrada para novos contribuidores.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- Guia completo publicado",
         "uss": [
            {"title": "US 12.2.1: Guia de setup de ambiente", "body": "## US 12.2.1\n\n**Parent:** F12.2\n\nSetup JDK 17, Android SDK, local.properties, Google services, compilacao local."},
            {"title": "US 12.2.2: Guia de fluxo de contribuicao", "body": "## US 12.2.2\n\n**Parent:** F12.2\n\nBranching, conventional commits, execucao de testes unitarios e Maestro, processo de PR."},
         ]},
    ]},
    # Epic 13: Conteudo da DiscoverScreen (L10)
    {"epic_idx": 12, "features": [
        {"title": "F13.1: Estrutura de Conteudo", "body": "## Feature: Estrutura de Conteudo\n\n**Part of:** Epic 13\n\n### Descricao\nModelo de dados estatico para categorias e hinos em destaque.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- Dados compilados em Kotlin",
         "uss": [
            {"title": "US 13.1.1: Constantes de categorias e destaques", "body": "## US 13.1.1\n\n**Parent:** F13.1\n\nObjeto DiscoverContentDataSource com categorias (nomes, descricoes, hinos ids) e destaques selecionados, compilado em Kotlin."},
         ]},
        {"title": "F13.2: UI da DiscoverScreen", "body": "## Feature: UI da DiscoverScreen\n\n**Part of:** Epic 13\n\n### Descricao\nRenderizar conteudo curado na tela Descubra.\n\n### User Stories Filhas\n- {}\n\n### Criterios de aceite\n- Aba Descubra util",
         "uss": [
            {"title": "US 13.2.1: Renderizar cards de categorias e destaques", "body": "## US 13.2.1\n\n**Parent:** F13.2\n\nLazyColumn com cards de categoria e lista de hinos em destaque (reutilizando HymnCardItem)."},
            {"title": "US 13.2.2: Navegacao para hino", "body": "## US 13.2.2\n\n**Parent:** F13.2\n\nClicar em destaque abre HymnDetail; clicar em categoria abre lista filtrada (versao inicial)."},
         ]},
    ]},
]

epic_nums = [113,114,115,116,117,118,119,120,121,122,123,124,125]
epic_titles = [
    "Epic 1: Observabilidade - Crashlytics",
    "Epic 2: Higiene de Documentacao",
    "Epic 3: Feedback de Sincronizacao",
    "Epic 4: Clean Architecture Enforcement",
    "Epic 5: Gestao de Funcionalidades",
    "Epic 6: Sincronizacao em Background",
    "Epic 7: Pipeline CI/CD",
    "Epic 8: Cobertura de Testes",
    "Epic 9: Automacao E2E em CI",
    "Epic 10: Release Automatizado",
    "Epic 11: Eficiencia de Sincronizacao",
    "Epic 12: Mitigacao de Bus Factor",
    "Epic 13: Conteudo da DiscoverScreen",
]

url_pattern = re.compile(r'github\.com/[^/]+/[^/]+/issues/(\d+)')

def create_issue(title, body, label):
    r = subprocess.run(['gh', 'issue', 'create', '--title', title, '--body', body, '--label', label], capture_output=True, text=True)
    m = url_pattern.search(r.stdout)
    if m:
        return int(m.group(1))
    raise ValueError(f"Failed: {r.stdout} {r.stderr}")

created_features = {}  # epic_idx -> [(feat_title, feat_num)]
created_uss = {}       # (epic_idx, feat_db_idx) -> [us_title, us_num]

print("=== Criando 29 Features ===")
for epic_entry in structure:
    ei = epic_entry["epic_idx"]
    created_features[ei] = []
    for fi, feat in enumerate(epic_entry["features"]):
        num = create_issue(feat["title"], feat["body"], 'feature')
        created_features[ei].append((feat["title"], num))
        print(f"  Epic{ei+1} -> #{num}: {feat['title']}")
        time.sleep(0.3)

print("=== Criando 57 User Stories ===")
for epic_entry in structure:
    ei = epic_entry["epic_idx"]
    created_uss[ei] = []
    for fi, feat in enumerate(epic_entry["features"]):
        for ui, us in enumerate(feat["uss"]):
            num = create_issue(us["title"], us["body"], 'user-story')
            created_uss[ei].append((us["title"], num, fi))
            time.sleep(0.3)
print("US criadas")

print("=== Atualizando Epics com Features ===")
for epic_entry in structure:
    ei = epic_entry["epic_idx"]
    feats = created_features[ei]
    lines = []
    for title, num in feats:
        lines.append(f"- [ ] #{num} - {title}")
    body = f"## Features Filhas\n\n" + "\n".join(lines) + "\n"
    # Manter o resumo original - buscar body atual
    r = subprocess.run(['gh', 'issue', 'view', str(epic_nums[ei]), '--json', 'body'], capture_output=True, text=True)
    cur = json.loads(r.stdout)['body']
    # Substituir a secao Features Filhas inteira
    import re as re2
    new_body = re2.sub(r'## Features Filhas\n\n.*?(?=\n## Non-goals|\Z)', body, cur, flags=re2.DOTALL)
    subprocess.run(['gh', 'issue', 'edit', str(epic_nums[ei]), '--body', new_body], capture_output=True)
    print(f"  Epic #{epic_nums[ei]} OK")

print("=== Atualizando Features com US ===")
for epic_entry in structure:
    ei = epic_entry["epic_idx"]
    for fi, feat in enumerate(epic_entry["features"]):
        # feature number
        feat_num = created_features[ei][fi][1]
        us_for_feat = [u for u in created_uss[ei] if u[2] == fi]
        lines = []
        for title, num, _ in us_for_feat:
            lines.append(f"- [ ] #{num} - {title}")
        body = f"## User Stories Filhas\n\n" + "\n".join(lines) + "\n"
        r = subprocess.run(['gh', 'issue', 'view', str(feat_num), '--json', 'body'], capture_output=True, text=True)
        cur = json.loads(r.stdout)['body']
        import re as re2
        new_body = re2.sub(r'## User Stories Filhas\n\n.*?(?=### Criterios|\Z)', body, cur, flags=re2.DOTALL)
        subprocess.run(['gh', 'issue', 'edit', str(feat_num), '--body', new_body], capture_output=True)
        print(f"  Feature #{feat_num} OK")

print(f"\n=== DONE ===")
total_f = sum(len(v) for v in created_features.values())
total_u = sum(len(v) for v in created_uss.values())
print(f"Epics: 13 | Features: {total_f} | US: {total_u}")