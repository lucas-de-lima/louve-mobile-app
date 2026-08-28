param()
$ErrorActionPreference = "Stop"
$PROJECT_DIR = Split-Path -Parent $PSScriptRoot
$LOG_FILE = Join-Path $PROJECT_DIR "build.log"
Write-Host "Iniciando build do Louve App..."
Set-Location $PROJECT_DIR
.\gradlew.bat assembleDebug 2>&1 | Tee-Object -FilePath $LOG_FILE
if ($LASTEXITCODE -eq 0) {
    Write-Host "Build concluído com sucesso! Logs disponíveis em build.log" -ForegroundColor Green
} else {
    Write-Host "Falha no build. Verifique build.log para detalhes." -ForegroundColor Red
    exit 1
}