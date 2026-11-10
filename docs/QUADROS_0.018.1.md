# Quadros satíricos — 0.018.1

> Histórico da introdução das artes. Desde a [0.022.4](QUADROS_0.022.4.md), a seleção normal contém somente estas dez artes e os quadros vanilla carregados são migrados.

Dez imagens fornecidas pelo autor foram adicionadas como variantes do item vanilla **Quadro**. Elas entram na tag `minecraft:placeable`, portanto não exigem item próprio, comando novo ou mod adicional: coloque um quadro vanilla em uma parede com espaço compatível até aparecer a variante desejada.

| ID | Tamanho no mundo | Fonte |
| --- | ---: | --- |
| `feliz_datas` | 4 × 2 blocos | `01-feliz-datas.png` |
| `happy_nation` | 4 × 2 blocos | `02-happy-nation.png` |
| `me_respeita` | 4 × 4 blocos | `03-me-respeita.png` |
| `pergunta` | 2 × 2 blocos | `04-pergunta.png` |
| `rachadinha_tres_porcento` | 2 × 2 blocos | `05-rachadinha-tres-porcento.png` |
| `sacos_lula_flavio` | 4 × 4 blocos | `06-sacos-lula-flavio.png` |
| `mestre_dos_bagos` | 2 × 3 blocos | `07-mestre-dos-bagos.png` |
| `bolsonaro_joinha` | 2 × 2 blocos | `08-bolsonaro-joinha.png` |
| `um_pau` | 4 × 2 blocos | `09-um-pau.png` |
| `tweet_autodeclaracao` | 4 × 3 blocos | `10-tweet-autodeclaracao.png` |

## Preparação e preservação

As imagens originais ficam em `assets-entrada/quadros/`. `tools/prepare_paintings.ps1` produz PNGs de alta resolução em `textures/painting/`, mantendo proporção e acrescentando somente margens pretas quando necessário. Não há recorte, reconstrução por IA ou alteração editorial do texto e dos rostos.

`tools/validate_assets.ps1` exige uma fonte para cada prefixo, dimensões exatas das dez texturas e presença dos dez IDs na tag colocável. `PaintingGameTests` confirma no servidor que todas as variantes foram registradas com dimensões válidas.

Validação final em 31/10/2026: build/check/reobf aprovado, **179/179 GameTests com TaCZ** e **115/115 sem TaCZ**. O JAR `guerra_ezuberante-0.018.1.jar` possui SHA-256 `4a6073ebd4bfdc5588288a9a735ff7fbf0437b5cb6bea992537dc0e418adbac7`.

As imagens são conteúdo satírico fornecido para o projeto. O repositório não reivindica autoria de fotografias, capturas ou memes de terceiros.
