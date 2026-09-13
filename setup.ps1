param(
    [string]$HarnessUrl = "https://github.com/lucas-de-lima/harness-agentic-louve.git",
    [string]$HarnessDir = $null
)

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
if (-not $HarnessDir) { $HarnessDir = Join-Path $Root "..\..\harness-agentic-louve" }
$HarnessDir = Resolve-Path $HarnessDir -ErrorAction SilentlyContinue

if (-not $HarnessDir) {
    $HarnessDir = Join-Path $Root "..\harness-agentic-louve"
    Write-Host "Cloning $HarnessUrl into $HarnessDir ..."
    git clone $HarnessUrl $HarnessDir
}

Write-Host "Syncing .kilo/ (skills, squads, commands)..."
robocopy (Join-Path $HarnessDir ".kilo") (Join-Path $Root ".kilo") /E /XD node_modules worktrees /NFL /NDL /NJH /NJS /R:2 /W:2

Write-Host "Syncing .harness/ (ADRs, HITL, policies, vault, workflows)..."
robocopy (Join-Path $HarnessDir ".harness") (Join-Path $Root ".harness") /E /NFL /NDL /NJH /NJS /R:2 /W:2

Write-Host "Syncing AGENTS.md + GUARDRAILS.md..."
Copy-Item (Join-Path $HarnessDir "AGENTS.md") (Join-Path $Root "AGENTS.md") -Force
Copy-Item (Join-Path $HarnessDir "GUARDRAILS.md") (Join-Path $Root "GUARDRAILS.md") -Force

Write-Host "Setup completo."
Write-Host "  Harness repo : $HarnessDir"
Write-Host "  In .gitignore: .kilo/ .harness/ AGENTS.md GUARDRAILS.md (not tracked)"