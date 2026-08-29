$ADB = "C:\Users\Lucas\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$PROJECT_DIR = Split-Path -Parent $PSScriptRoot
$MAESTRO_DIR = Join-Path $PROJECT_DIR ".maestro"
$REPORTS_DIR = Join-Path $PROJECT_DIR "maestro_reports"
$TIMESTAMP = Get-Date -Format "yyyyMMdd_HHmmss"
$LOG_FILE = Join-Path $PROJECT_DIR "maestro_run_$TIMESTAMP.log"

Set-Location $PROJECT_DIR

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  Louve App - Maestro E2E Test Suite" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Verificar se Maestro está instalado
try {
    $version = & maestro --version 2>&1 | Select-Object -Last 1
    Write-Host "Maestro CLI: $version" -ForegroundColor Green
} catch {
    Write-Host "Maestro não encontrado! Adicione C:\maestro\bin ao PATH." -ForegroundColor Red
    exit 1
}

# Verificar se o emulador está rodando
$devices = & $ADB devices | Select-String -Pattern "device$"
if (-not $devices) {
    Write-Host "Nenhum dispositivo Android encontrado. Inicie o emulador primeiro." -ForegroundColor Red
    exit 1
}

Write-Host "Dispositivo: $(($devices -split '\s+')[0])" -ForegroundColor Green
Write-Host ""

# Limpar logs do dispositivo
Write-Host "Limpando logs do dispositivo..." -ForegroundColor Yellow
& $ADB logcat -c 2>$null

# Criar diretório de relatórios
New-Item -ItemType Directory -Path $REPORTS_DIR -Force | Out-Null

# Coletar todos os flows
$flows = Get-ChildItem -Path $MAESTRO_DIR -Filter "*.yaml" | Where-Object { $_.Name -ne "config.yaml" } | Sort-Object Name
$totalFlows = $flows.Count
$passed = 0
$failed = 0

Write-Host "Flows encontrados: $totalFlows" -ForegroundColor Cyan
Write-Host ""

$results = @()

foreach ($flow in $flows) {
    $flowName = $flow.BaseName
    Write-Host "▶ Executando: $flowName..." -ForegroundColor Yellow
    
    # Executar o flow com Maestro
    $output = & maestro test $flow.FullName --format junit --output "$REPORTS_DIR\$flowName.xml" 2>&1
    
    # Verificar se crashou
    $exitCode = $LASTEXITCODE
    
    if ($exitCode -eq 0) {
        Write-Host "  ✅ PASSED: $flowName" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "  ❌ FAILED: $flowName" -ForegroundColor Red
        $failed++
    }
    
    $results += [PSCustomObject]@{
        Flow = $flowName
        Status = if ($exitCode -eq 0) { "✅ PASSED" } else { "❌ FAILED" }
    }
}

# Coletar logs do dispositivo
Write-Host ""
Write-Host "Coletando logs do dispositivo..." -ForegroundColor Yellow
$APP_PID = & $ADB shell pidof -s com.lucasdelima.louveapp 2>$null
if ($APP_PID) {
    & $ADB logcat -d --pid=$APP_PID -v time | Out-File -FilePath $LOG_FILE -Encoding utf8
    Write-Host "Logs salvos em: $LOG_FILE" -ForegroundColor Green
}

# Resumo
Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  RESULTADOS" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "Total  : $totalFlows" -ForegroundColor White
Write-Host "Passed : $passed" -ForegroundColor Green
Write-Host "Failed : $failed" -ForegroundColor Red
Write-Host ""

$results | Format-Table -AutoSize

Write-Host ""
Write-Host "Relatórios: $REPORTS_DIR" -ForegroundColor Cyan
if ($APP_PID) {
    Write-Host "Logs      : $LOG_FILE" -ForegroundColor Cyan
}

if ($failed -gt 0) {
    Write-Host ""
    Write-Host "⚠️  Verifique os relatórios e logs para detalhes dos crashes." -ForegroundColor Yellow
    exit 1
}

exit 0