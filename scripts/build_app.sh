#!/bin/bash

# Script para build da aplicação Louve App
# Gera logs detalhados em build.log

echo "Iniciando build do Louve App..."
./gradlew assembleDebug --stacktrace > build.log 2>&1

if [ $? -eq 0 ]; then
    echo "Build concluído com sucesso! Logs disponíveis em build.log"
else
    echo "Falha no build. Verifique build.log para detalhes."
    exit 1
fi
