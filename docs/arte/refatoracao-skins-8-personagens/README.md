# Refatoração visual dos oito personagens — 0.022.3

Pedido do autor: [guia de direção de arte](../../../arte/refatoracao-skins-8/referencias/skill_criar_skins.md), com acabamento baseado em Lula, Jair, Flávio, Renan, Arthur e Combatente Ucraniana. Esta entrega modifica aparência, nomes localizados confirmados e ferramentas de QA; **não modifica gameplay**.

## Auditoria dos ovos e renderizadores

Todos os IDs abaixo usam o namespace `guerra_ezuberante`. As texturas estão em `src/main/resources/assets/guerra_ezuberante/textures/entity/`.

| Ovo identificado pelo autor | ID da entidade / caminho após o primeiro tick | Textura efetiva | Modelo / renderer |
| --- | --- | --- | --- |
| Comunist / Comunista | `comunista` → `minecraft:villager` do tipo `comunista` | `comunista/comunista_0..3.png` | `PetistaRenderer.COMUNISTAS`, Steve 4 px; depois `VillagerOrPetistaRenderer` |
| Bolsonarista | `bolsonarista` → `minecraft:villager` do tipo `bolsonarista` | `bolsonarista/bolsonarista_0..4.png` | `PetistaRenderer.BOLSONARISTAS`, Steve 4 px; depois delegação vanilla |
| Direitista | `direitista` → `minecraft:villager` do tipo `direitista` | `direitista/direitista_0..4.png` | `PetistaRenderer.DIREITISTAS`, Steve 4 px; depois delegação vanilla |
| Ciro Gomes | `ciro_gomes` | `ciro_gomes.png` | `CharacterGeoRenderer`, `humanoid.geo.json` |
| Enéias / Enéas Carneiro | `eneias` | `eneias.png` | `EneiasRenderer`, mesmo humanoide; cores de chefe preservadas |
| Romeu Zema | `romeu_zema` | `romeu_zema.png` | `CharacterGeoRenderer`, mesmo humanoide |
| Petista | `petista` → `minecraft:villager` do tipo `petista` | `petista/petista_0..5.png` | `PetistaRenderer.PETISTAS`, Steve 4 px; depois delegação vanilla |
| Guilherma / Guilherme Boulos | `guilherme_boulos` | `guilherme_boulos.png` | `CharacterGeoRenderer`, mesmo humanoide |

São **oito famílias de ovos e 24 texturas efetivas**, pois quatro ovos escolhem profissões/variantes. Refazer só um PNG deixaria aparências antigas nas vilas e em parte dos usos dos ovos. Quantidades, sorteios, profissões, índices e conversões foram preservados. Guardas e Manifestantes que já reutilizavam esses PNGs recebem o novo acabamento pelos mesmos caminhos, sem alterar seu funcionamento.

O nome de Boulos já estava correto nas traduções: não havia `Guilherma` para corrigir. O nome exibido de Enéias foi corrigido para **Enéas Carneiro**, mantendo `eneias` em todos os IDs técnicos. A grafia do nome nos demais textos localizados foi ajustada sem alterar números ou efeitos.

## Direção artística aplicada

| Personagem | Problemas anteriores | Refatoração |
| --- | --- | --- |
| Comunista | Variantes com estilos incompatíveis, rostos pouco legíveis e grandes símbolos chapados | Quatro humanos originais, rostos e tons próprios; jaqueta/paleta vinho, calça oliva/escura, gola e pequenos detalhes. O pedido de rosto humano substitui a antiga aparência de cachorro/Venom/memes nessas variantes. |
| Bolsonarista | Mistura de caricaturas, fotografia reduzida e roupas amarelas chapadas | Cinco apoiadores genéricos distintos de Jair; amarelo/verde, gola, mangas, jeans e sombras consistentes. |
| Direitista | Rosto repetitivo, cabelo/roupas simples e pouca continuidade | Cinco rostos em azuis/neutros, social/polo/blazer, tecidos, punhos e sapatos. Aparência principal não repete Zema nem o Bolsonarista. |
| Ciro Gomes | Rosto rígido, branco/cinza chapado e pouca leitura da camisa | Rosto maduro individual, grisalho curto, camisa clara e gravata azul preservadas; colarinho, botões, costuras e calça social azul escura. |
| Enéas Carneiro | Barba como bloco preto, olhos sem aros e couro cabeludo sem construção | Calvície desenhada no topo/frente, cabelo lateral/nuca, óculos finos com olhos visíveis, barba e bigode em três tons; terno azul com lapelas e punhos. |
| Romeu Zema | Pele/cabelo quase planos e camisa com pouco volume | Grisalho castanho com penteado próprio, rosto e mandíbula distintos de Ciro; camisa azul-clara, manga dobrada, pequeno broche existente, calça escura. |
| Petista | Caricaturas/memes e estampas grandes; contraste irregular entre variantes | Seis humanos próprios, vermelho mais vivo que o Comunista, jeans e acabamentos de tecido; nenhum recebe automaticamente o rosto/barba branca/barriga do Lula. |
| Guilherme Boulos | Rosto sem barba/cabelo definidos e polo de uma cor | Cabelo curto escuro, barba e bigode próprios, rosto adulto; polo vermelha mantida, gola, mangas, dobras, jeans e calçados. |

As texturas são desenhadas na grade **nativa 64×64 RGBA**, com seis faces por parte do corpo, 36 faces base, alfa 0/255 e transparência fora do atlas utilizado. Não foram usadas fotos reduzidas, desfoque, ruído aleatório ou um gerador de mockup como textura. Membros esquerdo e direito são preenchidos explicitamente, com orientação própria e relógio somente no punho esquerdo quando presente. A luz favorece cima/frente/esquerda; laterais e bases recebem sombras coerentes.

A revisão dos primeiros renders de topo identificou repetição excessiva no cabelo grisalho. As mechas foram redesenhadas em agrupamentos contínuos, e Ciro/Zema receberam desenhos e tonalidades diferentes antes da validação final.

## Arquivos da entrega

- [Oito personagens lado a lado](oito-personagens.png).
- [Todas as 24 variantes, antes](antes-24-texturas.png) e [depois](depois-24-texturas.png).
- [Referências aprovadas preservadas](referencias-aprovadas.png).
- [Topos, bases e mandíbulas](topos-e-bases.png).
- [Originais](originais/): 24 cópias dos recursos efetivamente usados antes desta sprint.
- [PNG 64×64 novos](novas/): 24 arquivos `<id>_skin_64x64.png`.
- [Prévias](previews/): frente, costas, esquerda, direita, mapa UV e rosto, separadas das texturas jogáveis.
- [Comparações](comparacoes/): antes/depois de cada variante, rosto ampliado e vista em perspectiva.
- [Manifesto de arquivos e hashes originais](manifesto.json).
- [Validação automática de pixels/UV/hash](validacao.json).

Os PNGs em uso continuam nos mesmos caminhos de recursos do jogo. As fontes novas estão também em `assets-entrada/skins/refatoracao-8/`; as fontes anteriores do projeto não foram sobrescritas. Para Zema/Direitistas, que eram gerados por Java e não tinham uma fonte externa equivalente, o PNG anterior do recurso foi preservado em `originais/`.

## Skills e método reproduzível

Foram lidos integralmente `Downloads/skill_criar_skins.md` e `Downloads/skill_criar_itens.md`; o ZIP `skills_guerra_ezuberante_com_referencias.zip` foi extraído apenas em `.tools/referencias-skins-8/`, preservando o arquivo original. As [skills copiadas para referência](../../../arte/refatoracao-skins-8/referencias/) acompanham as ferramentas. A skill de itens foi consultada para preservar o contrato de recursos; os ícones e cores dos ovos não precisaram mudar.

Ferramentas em [arte/refatoracao-skins-8](../../../arte/refatoracao-skins-8/), com Python 3.12 e Pillow 11.3.0:

```powershell
python arte/refatoracao-skins-8/auditar.py
python arte/refatoracao-skins-8/gerar.py
python arte/refatoracao-skins-8/validar.py
python arte/refatoracao-skins-8/previas.py
```

`gerar.py` escreve somente os 24 PNGs previstos, suas cópias-fonte e a documentação visual; `auditar.py` preserva originais já existentes. `validar.py` exige PNG/RGBA/64×64, alfa binário, ocupação exata do atlas, 36 faces completas, hashes de integração iguais, rostos distintos e ausência de alteração nos diretórios de entidades, registros, geração, configurações, geometria e animações. As prévias usam os UVs do humanoide do mod, equivalentes ao Steve base utilizado nos moradores; o renderer vanilla real é conferido também no Minecraft.

## Teste real dos ovos e integração

`EightSkinVisualChecks` e [tools/skins_eight_visual_test.ps1](../../../tools/skins_eight_visual_test.ps1) utilizam o caminho real `SpawnEggItem.useOn` em um mundo de QA isolado. Depois da conversão para villager, a fixture seleciona cada profissão somente nesse mundo para cobrir todos os índices. O cliente verifica a textura escolhida pelo renderer, lê seus bytes pelo ResourceManager, compara SHA-256 com a fonte e verifica 64×64. São quatro vistas por textura e uma foto dos oito: **97 capturas**.

O perfil `.tools/run-skins-eight/` inicia com master 0 escrito antes do launch e `-PsilentClient`; não muda o volume salvo do perfil normal nem o mixer do Windows. O cenário encerra apenas seu próprio cliente após salvar o resultado. O mundo pessoal do autor não é fixture.

O primeiro ensaio gráfico encontrou um erro no caminho da própria fixture por causa de `gameDirectory/.`; foi corrigido com normalização do caminho, sem mudança de produção. A rodada final é registrada depois da última revisão dos cabelos.

## Resultado final

| Personagem | Skin refatorada | UV validado | Preview criada | Integração verificada |
| --- | --- | --- | --- | --- |
| Comunista | Sim, 4 variantes | 36 faces por PNG | Sim | Ovos, conversão, renderer e hashes no cliente |
| Bolsonarista | Sim, 5 variantes | 36 faces por PNG | Sim | Ovos, conversão, renderer e hashes no cliente |
| Direitista | Sim, 5 variantes | 36 faces por PNG | Sim | Ovos, conversão, renderer e hashes no cliente |
| Ciro Gomes | Sim | 36 faces | Sim | Ovo e renderer GeckoLib real |
| Enéas Carneiro | Sim | 36 faces | Sim | Ovo, renderer real, óculos/calvície/barba |
| Romeu Zema | Sim | 36 faces | Sim | Ovo e renderer GeckoLib real |
| Petista | Sim, 6 variantes | 36 faces por PNG | Sim | Ovos, conversão, renderer e hashes no cliente |
| Guilherme Boulos | Sim | 36 faces | Sim | Ovo e renderer GeckoLib real |

- **Build Gradle, check, reobf e validadores aprovados.** Log `.tools/skins-8-package-final.log`.
- **219/219 GameTests com TaCZ** aprovados (`.tools/skins-8-build-tests.log`). O último refinamento de cabelo alterou somente PNGs; depois dele passaram novamente os validadores, a rodada sem TaCZ, o cliente gráfico completo e o build final.
- **155/155 sem TaCZ** aprovados no segundo mundo limpo (`.tools/skins-8-no-tacz-final.log`). A primeira rodada teve falhas de moradores nas vilas Bolsonarista e da Esquerda; não foi alterada lógica de geração, e não se declara que a arte corrigiu essa intermitência. O código de geração é idêntico ao da base.
- **97 capturas finais, 24 vínculos de renderer e oito IDs de ovos aprovados**, sem erros: [resultado do cliente](jogo/resultado.json) e [galeria de jogo](jogo/). Exemplos: [grupo](jogo/skin8-grupo-oito.png), [Enéas de frente](jogo/skin8-eneias-frente.png), [Zema de costas](jogo/skin8-romeu_zema-costas.png). O caminho real do ovo foi usado pela fixture, não uma entidade substituta desenhada para a foto.
- JAR `build/libs/guerra_ezuberante-0.022.3.jar`: **24 hashes finais de PNG conferidos dentro do pacote**, sem testes, prévias, documentação, fontes de arte ou classes TaCZ embutidas. SHA-256 `8acb4c01cda0b169828abc4f41b0e37b6fedfc4d8e444ea39d9fe3e99f6cf553`.
- O cliente normal anterior encerrou por fechamento normal, com **todas as dimensões salvas** em log. Cliente atualizado **PID 19004**, menu pronto, respondendo, aberto em `run/` com `-PsilentClient`, sem modo automático de encerramento. Volume salvo do perfil normal e mixer do Windows preservados.

As capturas de QA, guias e originais ficam fora do JAR. A conferência artística do autor pode ser feita na mesma instalação; não é necessário gerar um mundo novo para trocar uma textura.

## Escopo preservado e pendências

Não foram alterados vida, dano, IA, facções, invocações, equipamentos, drops, recargas, relações, profissões, spawn natural, registros ou arquivos de animação/modelo. Alterações Java de produção se limitam a comentários descritivos no renderer; os nomes corrigidos estão nos arquivos de tradução.

A entrega foi preparada na branch visual `art/skins-oito-0.022.3`. Em 02/11/2026, após conferir o estado local e remoto, o autor autorizou commit, integração na `main` e push de toda a versão 0.022.3. Essa autorização substitui a restrição inicial de publicação. O acabamento representa semelhança estilizada no limite de um rosto 8×8; a aprovação artística pessoal continua sendo do autor.
