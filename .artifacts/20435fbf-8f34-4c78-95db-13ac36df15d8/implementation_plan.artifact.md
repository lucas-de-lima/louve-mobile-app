# Automação de Build e Coleta de Logs (Logcat)

Este plano visa automatizar as tarefas repetitivas de build, deploy e monitoramento de logs no projeto Louve Mobile App, utilizando scripts Bash conforme solicitado.

## User Review Required

> [!NOTE]
> Os scripts assumem que o `adb` e o `gradlew` estão configurados corretamente no ambiente. O script de execução (`run_app.sh`) tentará detectar um dispositivo conectado automaticamente.

## Proposed Changes

### Scripts de Automação

#### [NEW] [build_app.sh](file:///D:/Projetos/harness-agentic-sdlc-base/project/louve-mobile-app/scripts/build_app.sh)
Script para compilar a aplicação e salvar os logs de build em um arquivo para facilitar a depuração de erros de compilação.

#### [NEW] [run_app.sh](file:///D:/Projetos/harness-agentic-sdlc-base/project/louve-mobile-app/scripts/run_app.sh)
Script para instalar a aplicação, iniciá-la no dispositivo e capturar os logs do Logcat filtrados pelo pacote da aplicação (`com.lucasdelima.louveapp`).

---

### Documentação

#### [MODIFY] [README.md](file:///D:/Projetos/harness-agentic-sdlc-base/project/louve-mobile-app/README.md)
Adição de uma seção sobre como utilizar os novos scripts de automação.

## Verification Plan

### Manual Verification
1. Executar `./scripts/build_app.sh` e verificar se o arquivo `build.log` é gerado corretamente.
2. Executar `./scripts/run_app.sh` com um emulador ou dispositivo conectado e verificar se a aplicação inicia e os logs aparecem no terminal/arquivo `app.log`.
