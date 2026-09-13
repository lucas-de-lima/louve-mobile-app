import subprocess, json, re

# Rebuild the structure metadata (same as restructure.py)
structure = [
    {"features": [{"title": "F1.1: Crashlytics SDK Integration", "uss": []},
                  {"title": "F1.2: Error Reporting nos Sync Services", "uss": []}]},
    {"features": [{"title": "F2.1: Reescrever Favoritos.md", "uss": []},
                  {"title": "F2.2: Alinhar referencias cruzadas", "uss": []}]},
    {"features": [{"title": "F3.1: Estado de Sincronizacao", "uss": []},
                  {"title": "F3.2: Indicador Visual na UI", "uss": []}]},
    {"features": [{"title": "F4.1: AuthUiState/AuthError para domain", "uss": []},
                  {"title": "F4.2: DefaultTheme para constantes do domain", "uss": []},
                  {"title": "F4.3: HomeViewModel com DI Hilt", "uss": []}]},
    {"features": [{"title": "F5.1: Infraestrutura Remote Config", "uss": []},
                  {"title": "F5.2: Wrapper FeatureFlag", "uss": []}]},
    {"features": [{"title": "F6.1: PeriodicSyncWorker", "uss": []},
                  {"title": "F6.2: Agendamento no aplicativo", "uss": []}]},
    {"features": [{"title": "F7.1: Pipeline de Build na CI", "uss": []},
                  {"title": "F7.2: Quality Gates (lint + testes)", "uss": []}]},
    {"features": [{"title": "F8.1: Testes de Repositorios", "uss": []},
                  {"title": "F8.2: Testes de Servicos de Sync", "uss": []},
                  {"title": "F8.3: Testes de ViewModels", "uss": []}]},
    {"features": [{"title": "F9.1: Runner E2E na CI", "uss": []},
                  {"title": "F9.2: Relatorios e Gatilho", "uss": []}]},
    {"features": [{"title": "F10.1: Build Assinado", "uss": []},
                  {"title": "F10.2: Upload Google Play Console", "uss": []},
                  {"title": "F10.3: GitHub Release", "uss": []}]},
    {"features": [{"title": "F11.1: Change Tracking", "uss": []},
                  {"title": "F11.2: Merge Incremental", "uss": []}]},
    {"features": [{"title": "F12.1: Keystore Recovery", "uss": []},
                  {"title": "F12.2: Contributor Onboarding", "uss": []}]},
    {"features": [{"title": "F13.1: Estrutura de Conteudo", "uss": []},
                  {"title": "F13.2: UI da DiscoverScreen", "uss": []}]},
]

# Expected US titles per feature in order (flattened, same as restructure.py order)
us_titles = [
    # Epic 1
    "US 1.1.1: Adicionar dependencias do Crashlytics", "US 1.1.2: Configurar inicializacao no LouveApp",
    "US 1.2.1: Logging no DataMigrationService", "US 1.2.2: Logging no BidirectionalSyncService", "US 1.2.3: Logging no ConnectivityMonitorService",
    # Epic 2
    "US 2.1.1: Escrever corpo do novo Favoritos.md", "US 2.1.2: Remover referencias a planejamento pre-implementacao",
    "US 2.2.1: Atualizar docs/10 autenticacao e persistencia", "US 2.2.2: Atualizar docs/11 migracao e sync",
    # Epic 3
    "US 3.1.1: Criar SyncStatus enum e StateFlow", "US 3.1.2: Atualizar estado nos callbacks de rede",
    "US 3.2.1: Criar componente SyncIndicator", "US 3.2.2: Integrar no HomeTopAppBar",
    # Epic 4
    "US 4.1.1: Mover AuthUiState.kt para domain/model", "US 4.1.2: Atualizar referencias em AuthRepository e FirebaseAuthRepositoryImpl",
    "US 4.2.1: Substituir DefaultTheme no DataMigrationService", "US 4.2.2: Substituir DefaultTheme no BidirectionalSyncService",
    "US 4.3.1: Injetar HymnRepository via constructor", "US 4.3.2: Atualizar testes do HomeViewModel",
    # Epic 5
    "US 5.1.1: Adicionar SDK e provider", "US 5.1.2: Configurar fetch no LouveApp",
    "US 5.2.1: Criar FeatureFlagKey enum", "US 5.2.2: Criar FeatureFlagProvider",
    # Epic 6
    "US 6.1.1: Implementar PeriodicSyncWorker", "US 6.1.2: Configurar constraints e periodo",
    "US 6.2.1: Agendar via enqueueUniquePeriodicWork", "US 6.2.2: Verificar agendamento",
    # Epic 7
    "US 7.1.1: Workflow pr-build.yaml", "US 7.1.2: Caching e otimizacao",
    "US 7.2.1: Lint check com detekt", "US 7.2.2: Unit test check",
    # Epic 8
    "US 8.1.1: Testar HymnRepositoryImpl", "US 8.1.2: Testar DefaultFavoritesRepository e DefaultSettingsRepository", "US 8.1.3: Testar DataMigrationService",
    "US 8.2.1: Testar BidirectionalSyncService", "US 8.2.2: Testar ConnectivityMonitorService",
    "US 8.3.1: Testar HomeViewModel", "US 8.3.2: Testar HymnDetailViewModel", "US 8.3.3: Testar AuthViewModel",
    # Epic 9
    "US 9.1.1: Subir emulador e instalar APK", "US 9.1.2: Executar flows criticos",
    "US 9.2.1: Publicar relatorio JUnit", "US 9.2.2: Politica de execucao",
    # Epic 10
    "US 10.1.1: Armazenar keystore em secrets", "US 10.1.2: Workflow assembleRelease assinado",
    "US 10.2.1: Configurar service account", "US 10.2.2: Integrar upload no workflow",
    "US 10.3.1: Criar release com changelog", "US 10.3.2: Trigger em tag push",
    # Epic 11
    "US 11.1.1: Timestamps de ultima modificacao", "US 11.1.2: Estado do ultimo sync",
    "US 11.2.1: Migrar BidirectionalSyncService", "US 11.2.2: Compatibilidade com WorkManager",
    # Epic 12
    "US 12.1.1: Documentar processo de recuperacao", "US 12.1.2: Backup em cofre seguro",
    "US 12.2.1: Guia de setup de ambiente", "US 12.2.2: Guia de fluxo de contribuicao",
    # Epic 13
    "US 13.1.1: Constantes de categorias e destaques",
    "US 13.2.1: Renderizar cards de categorias e destaques", "US 13.2.2: Navegacao para hino",
]

# Fetch open user-story issues >= 164 sorted by number
d = subprocess.run(['gh','issue','list','--label','user-story','--state','open','--limit','200','--json','number,title'], capture_output=True, text=True)
us_issues = [u for u in json.loads(d.stdout) if u['number'] >= 164]
us_issues.sort(key=lambda x: x['number'])
print(f"US issues encontradas: {len(us_issues)}")

# Map US titles to issue numbers in order
us_num_by_title = {}
count = 0
for expected in us_titles:
    # Find matching issue
    for iss in us_issues:
        if iss['title'] == expected:
            us_num_by_title[expected] = iss['number']
            count += 1
            break
print(f"US mapeadas: {count}")

# Build feature -> [us...] mapping in order
idx = 0
feat_us_map = []  # flat list of [feature_number, [us_numbers...]]
feature_numbers = list(range(164, 193))
fi = 0
for epic_entry in structure:
    for feat in epic_entry["features"]:
        # Each feature has 2 us except F8.1 (3), F1.2 (3), F8.2 (2), etc - determined by title match
        pass

# Actually: directly count features and their us counts from the structure in restructure.py
# Reconstruct using the feature bodies from restructure: us count per feature
us_counts = [2, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 4, 2, 2, 2, 2, 2]
# Hmm this is fragile. Instead: use us_titles with known per-feature segmentation:
feat_segments = [
    ["US 1.1.1", "US 1.1.2"],
    ["US 1.2.1", "US 1.2.2", "US 1.2.3"],
    ["US 2.1.1", "US 2.1.2"],
    ["US 2.2.1", "US 2.2.2"],
    ["US 3.1.1", "US 3.1.2"],
    ["US 3.2.1", "US 3.2.2"],
    ["US 4.1.1", "US 4.1.2"],
    ["US 4.2.1", "US 4.2.2"],
    ["US 4.3.1", "US 4.3.2"],
    ["US 5.1.1", "US 5.1.2"],
    ["US 5.2.1", "US 5.2.2"],
    ["US 6.1.1", "US 6.1.2"],
    ["US 6.2.1", "US 6.2.2"],
    ["US 7.1.1", "US 7.1.2"],
    ["US 7.2.1", "US 7.2.2"],
    ["US 8.1.1", "US 8.1.2", "US 8.1.3"],
    ["US 8.2.1", "US 8.2.2"],
    ["US 8.3.1", "US 8.3.2", "US 8.3.3"],
    ["US 9.1.1", "US 9.1.2"],
    ["US 9.2.1", "US 9.2.2"],
    ["US 10.1.1", "US 10.1.2"],
    ["US 10.2.1", "US 10.2.2"],
    ["US 10.3.1", "US 10.3.2"],
    ["US 11.1.1", "US 11.1.2"],
    ["US 11.2.1", "US 11.2.2"],
    ["US 12.1.1", "US 12.1.2"],
    ["US 12.2.1", "US 12.2.2"],
    ["US 13.1.1"],
    ["US 13.2.1", "US 13.2.2"],
]

print(f"Feature count: {len(feature_numbers)}, segments: {len(feat_segments)}")
assert len(feature_numbers) == len(feat_segments), "Mismatch!"

# Update each feature body
for fi, feat_num in enumerate(feature_numbers):
    segment = feat_segments[fi]
    us_links = []
    for prefix in segment:
        # Find full title by prefix
        for title in us_num_by_title:
            if title.startswith(prefix):
                us_links.append(f"- [ ] #{us_num_by_title[title]} - {title}")
                break
    r = subprocess.run(['gh', 'issue', 'view', str(feat_num), '--json', 'body'], capture_output=True, text=True)
    body = json.loads(r.stdout)['body']
    body = body.replace('- {}', "\n".join(us_links))
    # Clean up leftover {}
    body = re.sub(r'\{[^}]*\}', '', body)
    body = re.sub(r'\n{3,}', '\n\n', body)
    subprocess.run(['gh', 'issue', 'edit', str(feat_num), '--body', body], capture_output=True)
    print(f"Feature #{feat_num} OK ({len(us_links)} US)")

print("Done")