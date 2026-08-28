$ADB = "C:\Users\Lucas\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$PACKAGE = "com.lucasdelima.louveapp"
while ($true) {
    $APP_PID = & $ADB shell pidof -s $PACKAGE 2>$null
    if ($APP_PID) {
        & $ADB logcat -v time --pid=$APP_PID 2>$null
    }
    Start-Sleep -Seconds 2
}