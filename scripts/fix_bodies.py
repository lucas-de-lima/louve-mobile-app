import subprocess, re, json

epic_nums = [113,114,115,116,117,118,119,120,121,122,123,124,125]
feat_nums = [126,127,128,129,130,131,132,133,134,135,136,137,138,139,140]
epic_feat_map = [[0],[1],[2],[3],[4],[5],[6],[7,8],[9],[10],[11],[12,13],[14]]
feat_us_map = [[0,1,2],[3],[4,5],[6,7],[8,9],[10,11],[12,13,14],[15],[16],[17],[18],[19],[20],[21],[22]]

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

feat_titles = [
    "F-01: Crashlytics Integration",
    "F-02: Update Stale Documentation",
    "F-03: Sync Status Indicator",
    "F-04: Fix Domain Layer Violations",
    "F-05: Remote Config Foundation",
    "F-06: Periodic WorkManager Sync",
    "F-07: PR Validation Pipeline",
    "F-08: Core Repository Unit Tests",
    "F-09: ViewModel Unit Tests",
    "F-10: Maestro CI Integration",
    "F-11: Automated Release Pipeline",
    "F-12: Incremental Data Sync",
    "F-13: Keystore and Access Recovery",
    "F-14: Contributor Onboarding",
    "F-15: DiscoverScreen Editorial Content",
]

us_titles = [
    "US 1.1: Add Crashlytics SDK dependency",
    "US 1.2: Configure Crashlytics initialization",
    "US 1.3: Add error reporting to sync services",
    "US 2.1: Rewrite Favoritos.md",
    "US 3.1: Create sync status StateFlow",
    "US 3.2: Add visual sync indicator to TopAppBar",
    "US 4.1: Move AuthUiState to domain layer",
    "US 4.2: Replace DefaultTheme in data services",
    "US 5.1: Configure Firebase Remote Config",
    "US 5.2: Create FeatureFlag wrapper",
    "US 6.1: Create PeriodicSyncWorker",
    "US 6.2: Schedule worker on app init",
    "US 7.1: Build check workflow",
    "US 7.2: Lint check workflow",
    "US 7.3: Unit test workflow",
    "US 8.1: Test repositories and sync services",
    "US 9.1: Test ViewModels",
    "US 10.1: Configure Maestro workflow in CI",
    "US 11.1: Build signed AAB and upload",
    "US 12.1: Implement incremental sync",
    "US 13.1: Backup keystore and document recovery",
    "US 14.1: Create contributor onboarding guide",
    "US 15.1: Add curated content to DiscoverScreen",
]

# Fix Epic bodies - remove duplicated feature titles
for i, en in enumerate(epic_nums):
    r = subprocess.run(['gh', 'issue', 'view', str(en), '--json', 'body'], capture_output=True, text=True)
    body = json.loads(r.stdout)['body']
    for fi in epic_feat_map[i]:
        ftitle = feat_titles[fi]
        fnum = feat_nums[fi]
        # Fix the duplicated pattern: "- - [ ] #XXX - Title Title" -> "- [ ] #XXX - Title"
        body = body.replace(f'- - [ ] #{fnum} - {ftitle} {ftitle}', f'- [ ] #{fnum} - {ftitle}')
        # Also fix if it's just "- - [ ]" 
        body = body.replace('- - [ ]', '- [ ]')
    subprocess.run(['gh', 'issue', 'edit', str(en), '--body', body], capture_output=True)
    print(f"Fixed Epic #{en}")

# Fix Feature bodies - remove duplicated US titles
for i, fn in enumerate(feat_nums):
    r = subprocess.run(['gh', 'issue', 'view', str(fn), '--json', 'body'], capture_output=True, text=True)
    body = json.loads(r.stdout)['body']
    for ui in feat_us_map[i]:
        utitle = us_titles[ui]
        unum = us_nums = [141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,162,163][ui]
        body = body.replace(f'- - [ ] #{unum} - {utitle} {utitle}', f'- [ ] #{unum} - {utitle}')
        body = body.replace('- - [ ]', '- [ ]')
    subprocess.run(['gh', 'issue', 'edit', str(fn), '--body', body], capture_output=True)
    print(f"Fixed Feature #{fn}")

print("All bodies fixed.")