#!/bin/bash

# Script para rodar a aplicação e capturar logs do Logcat
# Uso: ./run_app.sh

PACKAGE_NAME="com.lucasdelima.louveapp"
LOG_FILE="app.log"

echo "Limpando logs antigos do dispositivo..."
adb logcat -c

echo "Instalando aplicação..."
./gradlew installDebug

if [ $? -ne 0 ]; then
    echo "Falha na instalação. Verifique os logs acima."
    exit 1
fi

echo "Iniciando $PACKAGE_NAME..."
adb shell am start -n "$PACKAGE_NAME/.MainActivity"

echo "Aguardando inicialização do processo..."
sleep 2
PID=$(adb shell pidof -s $PACKAGE_NAME)

if [ -z "$PID" ]; then
    echo "Não foi possível encontrar o processo da aplicação. Verifique se ela iniciou corretamente no dispositivo."
    exit 1
fi

echo "Capturando logs (Logcat) para PID: $PID"
echo "Salvando em $LOG_FILE e exibindo no terminal (Ctrl+C para parar)..."
adb logcat --pid=$PID *:V | tee $LOG_FILE
