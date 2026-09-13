## Contexto

O Job de SonarQube Cloud Analysis no PR Validation falha com exit code 3 (EXECUTION FAILURE) em todo PR aberto. O problema nao e de codigo — e de configuracao entre a plataforma SonarCloud e o CI.

## Evidencia

Log do CI na PR #253 (workflow `pr-validation.yml`, job `validate`):

```
00:09:49.641 ERROR You are running CI analysis while Automatic Analysis
             is enabled. Please consider disabling one or the other.
```

## Causa Raiz

O projeto `lucas-de-lima_louve-mobile-app` no SonarCloud tem **Automatic Analysis** habilitada (que escaneia automaticamente pushes para default branch e PRs). Ao mesmo tempo, o workflow `pr-validation.yml` executa `SonarSource/sonarcloud-github-action@v5` com `sonar-scanner` via CI.

A documentacao oficial do SonarCloud (https://docs.sonarsource.com/sonarqube-cloud/analyzing-source-code/automatic-analysis#conflict-with-ci-based-analysis) afirma:

> "Automatic analysis is not intended to be used in conjunction with CI-based analysis. If you enable automatic analysis, you must ensure that you do not have any CI-based analyses configured. If you do then these CI-based analyses will fail and cause a failure in your build process."

## Impacto

- SonarCloud Code Analysis e marcado como CANCELLED em todo PR
- PR fica com status check bloqueado (merge state: BLOCKED)
- Novos problemas de qualidade e seguranca nao sao detectados automaticamente
- Coverage (JaCoCo) nao e reportado
- PR #253, PR #254 e futuros PRs serao afetados

## Opcoes de Correcao

### Opcao A (recomendada): Desabilitar Automatic Analysis

No SonarCloud UI:
1. Ir em Project Settings > Administration > Analysis Method
2. Desligar "Automatic Analysis"
3. Manter CI-based analysis no `pr-validation.yml`

**Beneficios:** Coverage (JaCoCo) passa a funcionar, analise completa de seguranca Java/Kotlin, logs disponiveis.

### Opcao B: Remover CI-based analysis

Remover o step `SonarSource/sonarcloud-github-action@v5` do `pr-validation.yml` e deixar so a Automatic Analysis.

**Riscos:** Automatic Analysis nao suporta coverage, nao tem logs, tem menos rules de seguranca e limite de 10MB de codigo Java.

### Opcao C: Manter ambos

Nao e possivel — conflito documentado, causa falha no build.

## Como reproduzir

1. Abrir qualquer PR no repositorio
2. Aguardar o job `validate` completar
3. Observar o step "SonarQube Cloud Analysis" com status CANCELLED
4. Verificar logs: `ERROR You are running CI analysis while Automatic Analysis is enabled`

## Ambiente

- Projeto SonarCloud: `lucas-de-lima_louve-mobile-app`
- Workflow: `.github/workflows/pr-validation.yml`
- Acao: `SonarSource/sonarcloud-github-action@v5`
- Scanner: sonar-scanner-cli 7.0.2.4839
- CI: GitHub Actions (ubuntu-latest)
- Branch: qualquer PR para develop/main