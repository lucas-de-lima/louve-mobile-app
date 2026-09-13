## Contexto
Durante execucao dos testes E2E Maestro (flow 11_support_ticket_form), identificou-se que o botao "Enviar Ticket" no formulario de suporte nao esta visivel apos preenchimento dos campos.

## Evidencia
- Flow 11_support_ticket_form: Assert that "Enviar Ticket" is visible retorna WARNED (assertion false)
- O elemento existe no layout mas nao esta visivel na tela

## Causa Provavel
O layout do SupportScreen (provavelmente um Scaffold ou ScrollableColumn) nao rola automaticamente para exibir o botao de envio apos o preenchimento dos campos. O teclado e os campos ocupam espaco vertical que empurra o botao para fora da viewport.

## Passos para Reproduzir
1. Abrir o app
2. Navegar: Mais > Ajuda e Suporte
3. Preencher os tres campos (Nome, E-mail, Descricao)
4. Observar que o botao "Enviar Ticket" permanece fora da area visivel

## Impacto
Usuario nao consegue visualizar o botao de acao principal apos preencher o formulario, causando UX frustrante e potencial abandono do fluxo de suporte.

## Ambiente
- Dispositivo: Emulador Medium_Phone (1080x2400, API 37)
- Branch: feature/epic8-f81-testes-repositorios