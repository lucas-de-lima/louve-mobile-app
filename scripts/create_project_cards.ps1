$projectId = "PVT_kwHOBn6SPs4BhJgi"

$epics = @(
    @{title="Epic 1: Observabilidade — Crashlytics"; body="Limitation L04 resolvida. Firebase Crashlytics para visibilidade de erros em producao."}
    @{title="Epic 2: Higiene de Documentacao"; body="Limitation L12 resolvida. Documentacao obsoleta revisada e alinhada com a implementacao real."}
    @{title="Epic 3: Feedback de Sincronizacao"; body="Limitation L08 resolvida. Indicador visual de status de sync na UI."}
    @{title="Epic 4: Enforcement de Clean Architecture"; body="Limitation L11 resolvida. Violacoes de Clean Architecture corrigidas."}
    @{title="Epic 5: Gestao de Funcionalidades"; body="Limitation L05 resolvida. Firebase Remote Config para rollout seguro."}
    @{title="Epic 6: Sincronizacao em Background"; body="Limitation L09 resolvida. WorkManager para sync periodico em background."}
    @{title="Epic 7: Pipeline CI/CD"; body="Limitation L03 resolvida. GitHub Actions para build + lint + testes em PR. No central."}
    @{title="Epic 8: Cobertura de Testes"; body="Limitation L01 resolvida. Testes unitarios para repositories, services e ViewModels."}
    @{title="Epic 9: Automacao E2E em CI"; body="Limitation L13 resolvida. Maestro flows em pipeline CI."}
    @{title="Epic 10: Release Automatizado"; body="Limitation L06 resolvida. Pipeline de release com Google Play Publisher API."}
    @{title="Epic 11: Eficiencia de Sincronizacao"; body="Limitation L07 resolvida. Sync incremental com diff tracking."}
    @{title="Epic 12: Mitigacao de Bus Factor"; body="Limitation L02 mitigada. Keystore backup, documentacao, onboarding."}
    @{title="Epic 13: Conteudo da DiscoverScreen"; body="Limitation L10 resolvida. Conteudo editorial curado na tela Descubra."}
)

$features = @(
    @{title="F-01: Crashlytics Integration"; body="Epic 1. Firebase Crashlytics SDK integrado."; parentEpic="Epic 1:"}
    @{title="F-02: Update Stale Documentation"; body="Epic 2. Favoritos.md reescrito."; parentEpic="Epic 2:"}
    @{title="F-03: Sync Status Indicator"; body="Epic 3. Indicador visual de sync na UI."; parentEpic="Epic 3:"}
    @{title="F-04: Fix Domain Layer Violations"; body="Epic 4. AuthUiState movido para domain. DefaultTheme substituido."; parentEpic="Epic 4:"}
    @{title="F-05: Remote Config Foundation"; body="Epic 5. Firebase Remote Config + wrapper FeatureFlag."; parentEpic="Epic 5:"}
    @{title="F-06: Periodic WorkManager Sync"; body="Epic 6. PeriodicWorkRequest para sync em background."; parentEpic="Epic 6:"}
    @{title="F-07: PR Validation Pipeline"; body="Epic 7. GitHub Actions workflow build+lint+test em PR."; parentEpic="Epic 7:"}
    @{title="F-08: Core Repository Unit Tests"; body="Epic 8. Testes para repositories e sync services."; parentEpic="Epic 8:"}
    @{title="F-09: ViewModel Unit Tests"; body="Epic 8. Testes para ViewModels criticos."; parentEpic="Epic 8:"}
    @{title="F-10: Maestro CI Integration"; body="Epic 9. Maestro flows executados em CI (nightly)."; parentEpic="Epic 9:"}
    @{title="F-11: Automated Release Pipeline"; body="Epic 10. Pipeline de release automatizada."; parentEpic="Epic 10:"}
    @{title="F-12: Incremental Data Sync"; body="Epic 11. Sync incremental com diff tracking."; parentEpic="Epic 11:"}
    @{title="F-13: Keystore and Access Recovery"; body="Epic 12. Backup e processo de recuperacao do keystore."; parentEpic="Epic 12:"}
    @{title="F-14: Contributor Onboarding"; body="Epic 12. Guia completo de contribuicao."; parentEpic="Epic 12:"}
    @{title="F-15: DiscoverScreen Editorial Content"; body="Epic 13. Conteudo editorial curado."; parentEpic="Epic 13:"}
)

$userStories = @(
    @{title="US 1.1: Add Crashlytics SDK dependency"; body="Feature F-01."}
    @{title="US 1.2: Configure Crashlytics initialization"; body="Feature F-01."}
    @{title="US 1.3: Add error reporting to sync services"; body="Feature F-01."}
    @{title="US 2.1: Rewrite Favoritos.md"; body="Feature F-02."}
    @{title="US 3.1: Create sync status StateFlow"; body="Feature F-03."}
    @{title="US 3.2: Add visual sync indicator to TopAppBar"; body="Feature F-03."}
    @{title="US 4.1: Move AuthUiState to domain layer"; body="Feature F-04."}
    @{title="US 4.2: Replace DefaultTheme in data services"; body="Feature F-04."}
    @{title="US 5.1: Configure Firebase Remote Config"; body="Feature F-05."}
    @{title="US 5.2: Create FeatureFlag wrapper"; body="Feature F-05."}
    @{title="US 6.1: Create PeriodicSyncWorker"; body="Feature F-06."}
    @{title="US 6.2: Schedule worker on app init"; body="Feature F-06."}
    @{title="US 7.1: Build check workflow"; body="Feature F-07."}
    @{title="US 7.2: Lint check workflow"; body="Feature F-07."}
    @{title="US 7.3: Unit test workflow"; body="Feature F-07."}
)

$mutationCreate = @'
mutation($project: ID!, $title: String!, $body: String) {
  addProjectV2DraftIssue(input: {projectId: $project, title: $title, body: $body}) {
    projectItem { id }
  }
}
'@

Write-Host "=== Criando Epics ==="
$epicIds = @()
foreach ($epic in $epics) {
    $result = gh api graphql -f query="$mutationCreate" -F project="$projectId" -F title="$($epic.title)" -F body="$($epic.body)" --jq ".data.addProjectV2DraftIssue.projectItem.id"
    Write-Host "  $($epic.title) -> $result"
    $epicIds += @{title=$epic.title; id=$result}
}

Write-Host "`n=== Criando Features ==="
$featureIds = @()
foreach ($feat in $features) {
    $result = gh api graphql -f query="$mutationCreate" -F project="$projectId" -F title="$($feat.title)" -F body="$($feat.body)" --jq ".data.addProjectV2DraftIssue.projectItem.id"
    Write-Host "  $($feat.title) -> $result"
    $featureIds += @{title=$feat.title; id=$result; parent=$feat.parentEpic}
}

Write-Host "`n=== Criando User Stories ==="
$usIds = @()
foreach ($us in $userStories) {
    $result = gh api graphql -f query="$mutationCreate" -F project="$projectId" -F title="$($us.title)" -F body="$($us.body)" --jq ".data.addProjectV2DraftIssue.projectItem.id"
    Write-Host "  $($us.title) -> $result"
    $usIds += @{title=$us.title; id=$result}
}

Write-Host "`n=== Criacao concluida ==="
Write-Host "Epics: $($epicIds.Count)"
Write-Host "Features: $($featureIds.Count)"
Write-Host "User Stories: $($usIds.Count)"
Write-Host "Total: $(($epicIds.Count + $featureIds.Count + $usIds.Count))"