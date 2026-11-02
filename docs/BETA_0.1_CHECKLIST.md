# Caminho para a beta 0.1 — fotografia da 0.022.2

Referência: 02/11/2026, após integrar Gabriel até `14c0b35`. Fontes: [planejamento original](IDEIA_GERAL.md#8-roadmap-proposto), [diário técnico](../CONTEXTO.md), registros Java, configurações e traduções atuais.

O roadmap original chama a v0.1.0 de **alpha**. O autor agora usa **beta 0.1**; este documento adota esse nome para o próximo marco. Ainda não existe uma lista de corte de conteúdo aprovada especificamente para essa beta. Assim, **pendências do GDD completo** e **condições para distribuir uma beta do conteúdo atual** são coisas diferentes.

## Base já implementada

- Minecraft 1.20.1, Forge 47.4.28, Java 17 e GeckoLib 4.8.4; TaCZ opcional, pistola própria GE-P12.
- Seis facções com seleção/persistência, kits, passivos, alianças e poderes; cinco classes, XP, nível máximo padrão 100, perfil J e árvore de habilidades.
- Árvores ampliadas: Esquerda Independente com 10 poderes, Lulistas com 8, Direita Independente com 13. Missão, Bolsonaristas e Centro têm três poderes cada, com a árvore genérica.
- NPCs: Renan, Arthur e suas variantes, Combatente Ucraniana, Lula, Jair, Flávio, Haddad, Boulos, Dilma, Jones Manoel, Tabata, Marina, Ciro, Enéias, Cury, Zema e Caiado; tropas, moradores e guardas.
- Chefes, explosões de morte, armas, drones, projéteis, invocações, animações, áudio e música ambiente da vila da Missão.
- Vilas/castelos das seis facções, estandartes, guardas das torres, moradores de facção e comandos de localização/TP; dez quadros satíricos.
- Skins de Marina, Tabata, Haddad, Jones e Dilma refeitas na 0.022.1; originais e comparações preservados.

Ter uma facção registrada e jogável não significa que toda a fantasia do GDD esteja implementada ou que seu balanceamento esteja concluído.

## O que falta do planejamento original de conteúdo

| Grupo | Falta confirmada no código atual | O que já cobre parte da ideia |
| --- | --- | --- |
| Missão | Glenda/evento e transformação Modo Chad; nerds montados em onças; sequência Moggador Supremo; Renan convocando Arthur como habilidade própria do NPC; giro especial Me Respeita. | Renan já é chefe armado e toca gravações; Arthur Invocador e suas Ucranianas funcionam. Música/arma não equivalem ao giro especial planejado. |
| Lulistas | Janones; burros zumbificados; tropas de artistas; meteoritos/Chuva de Picanha do Lula e seus reforços políticos conforme GDD. | Lula tem feijões, Faz o L e Manifestação; Haddad, Boulos e Dilma já existem. Manifestantes não são burros nem artistas. Meteoros rosas da Missão não são picanha do Lula. |
| Bolsonaristas | Eduardo Bolsonaro, Nikolas Ferreira, Trump e Netanyahu; reforços correspondentes, bombardeios fictícios, Funk Temporal e combinação Caos Internacional. | Jair já tem pistola, invoca Flávio e é chefe; Rachadinha funciona. |
| Arthur | Buff das unidades e formação/Legião Ucraniana especificamente descritos no GDD, caso sejam mantidos no corte da beta. | Invocação, limites, duração e reposição das Ucranianas já existem; não reimplementar o que já funciona. |

Algumas ideias evoluíram por decisão do autor: a Rachadinha atual é uma fenda/impacto no chão, não os projéteis de dinheiro divididos da primeira concepção. Essa diferença não deve ser tratada automaticamente como bug ou obrigação de voltar ao desenho antigo.

Centro foi concebido com poderes básicos, sem um elenco obrigatório de personagens. Acrescentar líder, NPCs ou uma árvore grande ao Centro seria uma nova decisão de escopo. Esquerda Independente e Direita Independente ganharam conteúdo posterior que vai além do primeiro GDD; não estão vazias.

## O que falta para distribuir uma beta do conteúdo atual

1. **Fechar o corte da beta:** decidir se ela sai com o elenco/poderes atuais ou exige todos os conceitos da tabela acima. Essa decisão muda muito o tamanho do trabalho; não é necessário transformar cada ideia histórica em requisito de lançamento.
2. **Homologar multiplayer da integração:** dois ou mais clientes, três facções em combate, PvP ON/OFF, aliados, classe/árvore, tiros TaCZ e GE-P12, invocações, troca de dimensão, morte/respawn, relog e retomada de servidor. As validações antigas não equivalem a testar todas as mudanças 0.019–0.022.2 em rede real.
3. **Validar os cenários grandes:** bomba do Enéias no raio padrão (dano 300/cratera 80), vários drones, guardas, tropas e vilas carregadas simultaneamente. Medir tempo de tick/FPS e ajustar limites/configuração se necessário; os GameTests usuais usam bombas menores.
4. **Completar QA visual e de mundo:** novos poderes e árvore de cinco colunas da Direita; todos os NPCs/skins/poses; geração natural nova com guardas e moradores das seis facções. Testar `/place` não substitui todos os cenários de geração natural. Mundos existentes não recebem castelos/guardas por retrogeneração.
5. **Balancear com os amigos:** vida, dano, recarga, custos das árvores, economia de pontos/XP e quantidade de tropas. O limite de nível 100 dá 99 pontos; com cinco atributos até 10, parte deles sobra após maximizar atributos. Isso precisa de uma decisão de design, não de valores inventados neste documento.
6. **Preparar distribuição reproduzível:** JAR final identificado, dependências/versões claras, pacote cliente/servidor testado em instalações limpas, instruções de atualização/backup/configuração, changelog e release para download. O GitHub com código não substitui um release instalável.
7. **Atualizar documentação e wiki para a versão candidata:** registrar status real, comandos e requisitos; não divulgar conteúdo futuro como pronto. Manter uma lista curta de limitações conhecidas e nenhum defeito crítico sem tratamento antes da beta.

## Correções da integração 0.022.2

- Preservada a reconciliação tardia de moradores da vila, os quadros e a limitação da música vanilla.
- Paris bloqueia habilidade de classe, poder, pistola própria e dano atribuído ao jogador/tropas enquanto a viagem dura.
- Laço, Privatização e efeitos associados aos novos golpes respeitam o resultado efetivo do dano.
- Bomba do Enéias publica o cancelamento inicial e permite filtrar entidades e lotes de blocos pelos eventos Forge; não aplica empurrão/Náusea quando o dano não é efetivo.
- Folhas temporárias possuem registro persistente por dimensão: uma posição descarregada não é esquecida ao encerrar o servidor.

Resultados e limites dos testes dessa integração: [INTEGRACAO_0.022.2.md](INTEGRACAO_0.022.2.md).

## Ideias que não têm marco obrigatório para a beta

Arenas eleitorais, campanhas/missões, grandes eventos, invasões e expansões de recursos/minérios continuam sujeitos a especificação e aprovação. O GDD coloca vários desses modos como ideias futuras sem marco reservado. Não são pendências de implementação assumidas apenas porque a wiki possui uma categoria preparada.
