param()
$ErrorActionPreference = "Stop"
$PROJECT_DIR = Split-Path -Parent $PSScriptRoot
$ADB = "C:\Users\Lucas\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$PACKAGE = "com.lucasdelima.louveapp"
$LOG_FILE = Join-Path $PROJECT_DIR "app.log"
Set-Location $PROJECT_DIR
Write-Host "Limpando logs antigos..." -ForegroundColor Cyan
& $ADB logcat -c
Write-Host "Instalando aplicação..." -ForegroundColor Cyan
.\gradlew.bat installDebug
if ($LASTEXITCODE -ne 0) { exit 1 }
Write-Host "Iniciando $PACKAGE..." -ForegroundColor Cyan
& $ADB shell am start -n "$PACKAGE/.MainActivity"
Start-Sleep -Seconds 3
$APP_PID = & $ADB shell pidof -s $PACKAGE 2>&1
if (-not $APP_PID) {
    Write-Host "Processo não encontrado." -ForegroundColor Red
    exit 1
}
Write-Host "Capturando logs para PID: $APP_PID (salvando em $LOG_FILE, Ctrl+C para parar)" -ForegroundColor Cyan
& $ADB logcat --pid=$APP_PID *:V | Tee-Object -FilePath $LOG_FILE