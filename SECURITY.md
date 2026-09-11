# Política de Segurança

Se você encontrar uma vulnerabilidade de segurança no Louve App, por favor, **NÃO abra uma issue pública**.

Em vez disso, reporte de forma responsável enviando um e-mail para o mantenedor principal (ver contato no README) ou usando o canal privado de segurança do projeto.

Todos os relatórios serão tratados com prioridade e confidencialidade. Após a análise, uma correção será implementada e comunicada à comunidade.

Práticas de segurança do projeto: segredos nunca são versionados (usa-se `keystore.properties` local) e as regras do Firestore são versionadas e auditáveis ([firestore.rules](firestore.rules)). 