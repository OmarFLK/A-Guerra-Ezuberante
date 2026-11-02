# Integração Gabriel — 0.022.2

Data: 02/11/2026. A pedido do autor, integrar a contribuição de Gabriel, revisar as novidades e depois comparar o resultado com o planejamento da beta 0.1.

## Contribuições preservadas

- `90bc4f2`: versões 0.019–0.022, poderes/árvores de facções, Renan chefe, Enéias, Cury, Zema, Caiado, drones, guardas e moradores.
- `14c0b35`: versão 0.022.1, novas skins de Marina, Tabata, Haddad, Jones e Dilma; fontes antigas preservadas, comparações e ferramenta de captura.
- Trabalho local até `b0a3ce0`: dez quadros, reconciliação tardia dos moradores, testes estabilizados e correção da música da vila que preserva músicas de outros mods.

Os commits de Gabriel mantêm a autoria original. A integração tem versão própria **0.022.2**. O cache Python compilado enviado no fork foi retirado; scripts/fontes de arte e comparações permanecem.

## Correções necessárias à aprovação

- Paris: habilidade de classe, dano atribuído ao jogador ou tropas, B e GE-P12 ficam bloqueados durante a viagem. A Investida já iniciada também é encerrada; a proteção de dano atua no servidor e cobre projéteis com atribuição de jogador.
- Dano cancelado/sem perda de vida ou absorção não aplica os efeitos associados de Laço, Privatização, Tapas, Diploma, Bombinha, drone bomba e Boiada; movimento indevido é restaurado. Privatização só retira efeitos benéficos depois de um golpe efetivo.
- Bomba atômica publica `ExplosionEvent.Start`; cancelamento não gera dano/cratera/efeitos. `ExplosionEvent.Detonate` filtra vítimas e cada lote limitado de blocos antes da escavação. Revalida `mobGriefing`, borda, chunk carregado e blocos inquebráveis. Os callbacks de blocos são incrementais, não uma lista gigante de toda a cratera. Empurrão/Náusea exigem dano efetivo.
- Folhas temporárias: `SavedData` por dimensão guarda posição, estado e prazo. Ao encerrar, posições descarregadas permanecem pendentes e vencidas; no próximo carregamento, são limpas sem forçar geração de chunks e sem substituir blocos que alguém alterou.
- Vilas: resolução dos conflitos manteve a conversão imediata e a reconciliação no tick seguinte, incluindo os tipos novos. Os quadros continuam registrados.
- QA: `gameTestWorkDir` permite mundos isolados por execução; ferramenta de skins aceita origem do mundo fechado e usa `silentClient` além de master 0.

## Validação

- **155/155 GameTests sem TaCZ aprovados**, incluindo cinco regressões novas de integração e os registros dos quadros. Mundo isolado em `.tools/integration-0222-no-tacz`; log `.tools/integration-0222-no-tacz.log`.
- **21 capturas gráficas de skins aprovadas** no cliente real (20 vistas individuais + grupo), perfil isolado, master 0 gravado antes de iniciar e `silentClient` confirmado no carregamento do áudio. [Relatório](validacao/integracao-0.022.2/skins.json), [grupo](preview/integracao-0.022.2/skin-grupo.png), [Marina](preview/integracao-0.022.2/skin-marina_silva-frente.png), [Tabata](preview/integracao-0.022.2/skin-tabata_amaral-frente.png).
- **219/219 GameTests com TaCZ aprovados** em outro mundo limpo, com build/check/reobf aprovado. Log `.tools/integration-0222-approved.log` (4 min 32 s). [Resumo verificável](validacao/integracao-0.022.2/testes.json).
- JAR inspecionado sem classes de testes/QA ou TaCZ embutidas; `git diff --check` e links da documentação nova aprovados.
- Cliente normal `run/`, PID 10884, menu carregado e processo respondendo. `silentClient` confirmado no log `.tools/integration-0222-normal-client.log`; sem modo automático de encerramento e sem alterar o volume do Windows ou o volume salvo do perfil.

Na primeira rodada, duas fixtures antigas falharam porque exigiam efeito secundário depois de um golpe recusado pela imunidade vanilla de um dano anterior no mesmo tick. Elas agora reiniciam essa janela antes de testar o segundo golpe válido; não foi removida a exigência de dano/cancelamento da produção. As regressões novas exigem que o golpe cancelado preserve o alvo.

Uma rodada seguinte encontrou interferência no teste antigo de feijão/parede, que dividia lote e altura com outros combates. A fixture passou a lote próprio em arena no céu, mantendo a exigência de dano único, bloqueio pela parede e tempo de vida; a mensagem de falha agora informa estado da parede, remoção do projétil e vida do alvo.

## Limites e beta

Esta integração não declara uma beta pronta. Multiplayer real envolvendo todas as mudanças novas, geração natural ampla, balanceamento com amigos e desempenho da bomba no raio padrão continuam como homologação de lançamento. Os testes de bomba usam raios reduzidos em mundos descartáveis; não comprovam o desempenho da cratera 80 em uma sessão normal. Áudio automatizado é conferido em silêncio, sem avaliação por audição.

[O que falta do planejamento e para a beta 0.1](BETA_0.1_CHECKLIST.md).
