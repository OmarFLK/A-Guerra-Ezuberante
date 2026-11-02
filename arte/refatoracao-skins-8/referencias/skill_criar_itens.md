---
name: criar-itens-guerra-ezuberante
description: Gere sprites de itens reais para Minecraft Java/Forge no estilo pixel art do Guerra Ezuberante: PNG RGBA 16x16 nítido, silhueta clara, paleta consistente, sombreamento pixel a pixel, integração opcional com JSON e validação automatizada.
---

# SKILL — Criar itens e sprites Minecraft para o Guerra Ezuberante

> **Uso:** instruções permanentes para Codex, Claude Code ou outra IA que desenhe itens e recursos do mod. **Entregável principal:** PNG RGBA **16×16 pixels** para ícone de item vanilla (padrão usado nas referências). A prévia ampliada é um arquivo separado.

## 1. Padrão visual do mod

Criar itens que pareçam parte de Minecraft: pixel art legítima, silhueta forte, arestas quadradas, cor e volume definidos em **pouquíssimos pixels** e pequenos highlights que deixam a forma perceptível no inventário.

A principal referência anexada é a **Espada/Bandeira da Ucrânia de 16×16**. Ela tem **16×16 RGBA**, fundo transparente, contorno escuro, lâmina em azul e amarelo, e diferenciação visual entre arma, cabo, guarda e iluminação. O PNG analisado utiliza cerca de **9 cores visíveis** e nenhuma borda suavizada: é um exemplo concreto de **paleta curta e intencional**, não um número obrigatório para todos os itens.

### Objetivo

Ao olhar o ícone com **16×16 pixels**, sem ampliar, o jogador deve reconhecer o tipo de item. Ao ampliar para 8×/16× com nearest-neighbor, o item deve revelar bom agrupamento de pixels, acabamento e sombras — nunca borrões ou uma imagem de IA reduzida.

## 2. Formatos e o que NÃO confundir

| Recurso | Tamanho padrão | Fundo | Observações |
|---|---|---|---|
| Ícone de item comum | **16×16 PNG RGBA** | Transparente | Padrão desta skill, inclusive espadas, ferramentas, projéteis, alimentos e materiais. |
| Item que exige detalhe extra | 32×32 PNG RGBA, **somente se pedido/justificado** | Transparente | Ainda pixel art, sem blur; conferir se o renderer/JSON é compatível. |
| Textura de bloco | 16×16 ou conforme especificação real do bloco | Geralmente opaca | Se precisar repetir em faces/tiles, documentar repetição e orientação. |
| Textura de projétil | Depende do renderer | Geralmente transparente | Não confundir sprite de inventário com textura/modelo de entidade do projétil. |
| Arma com modelo 3D | Texturas e geometria próprias | Dependente | Um ícone 16×16 **não** substitui um modelo 3D de arma TaCZ/GeckoLib. |
| Preview para apresentação | 256×256 ou maior | Livre | **Não é a textura de jogo.** Sempre gerar a partir do sprite final via NEAREST. |

**Nunca:** criar uma ilustração 1024×1024 e chamá-la de `16x16`; cortar um objeto de uma screenshot; incluir legenda, marca d'água, fundo opaco ou moldura no PNG do item; usar glow/bloom contínuo; deixar anti-alias nas bordas; misturar `item/generated` com um item cuja geometria foi criada em 3D sem adaptar o renderer.

## 3. Direção de arte — qualidade do sprite em 16×16

### 3.1 Composição e silhueta

1. Escolha uma orientação que mostre rapidamente o item: espada **diagonal**, ferramenta em 3/4 estilizado, semente/alimento centralizado, escudo frontal, munição com contorno claro.
2. Use normalmente uma borda de **0–2 pixels** entre o item e a tela, sem cortar pontas ou cabo. Se a silhueta exigir tocar a margem, justifique.
3. Deixe o fundo **alpha 0**; desenhe só o item.
4. Evite detalhes internos com menos de 1 pixel de leitura. Textos pequenos são ilegíveis: represente símbolos por cores e formas.
5. Primeiro valide a silhueta preta; depois pinte os materiais. Se só for reconhecível com a paleta, simplifique a forma.

### 3.2 Luz e sombras

Assuma uma luz dominante **superior esquerda** (ou siga o padrão de iluminação dos sprites existentes):

- Borda externa: cor de contorno mais escura, não obrigatoriamente preto puro.
- Face iluminada: tom claro em alguns pixels da parte superior/esquerda.
- Centro do material: tom médio.
- Parte inferior/direita ou reentrâncias: tom mais escuro.
- Ponto especular: **1–3 pixels** bem escolhidos para metal e vidro; não espalhar brilhos por todo o item.
- Para metal: distinguir aresta brilhante, centro e contorno escuro.
- Para madeira: 2–3 cores terrosas e um ou dois clusters de textura.
- Para tecido/bandeira: formas simples em cores sólidas, com sombreamento só onde reforçar volume.
- Para gema/minério: núcleo mais claro, facetas em 2–4 cores e contorno que o separa do fundo.

Preferir **3–5 tons por material**, ajustados à importância do item. Variações de cor não devem virar ruído aleatório.

### 3.3 Clusters e pixel art

- Desenhar com lápis de 1 px; cada pixel deve ter função.
- Criar pequenas áreas/`clusters` conectadas, em vez de espalhar pontos avulsos.
- Evitar contornos em escadinha irregular ou espessura acidental.
- Não utilizar antialiasing, blur, gradiente subpixel ou dithering excessivo.
- Usar pixels quadrados, alinhados à grade.
- Trabalhar em resolução nativa; ampliar com `Image.Resampling.NEAREST`.
- Testar o sprite também em tamanho real, em fundo claro, escuro e em slot do inventário.

## 4. Receita visual por tipo de item

### Espadas

- Diagonal legível do canto inferior esquerdo ao superior direito (ou inversa, conforme referência).
- Ponta clara, corpo em 2–3 tons, fio/brilho bem posicionado.
- Guarda distinguível da lâmina e do cabo.
- Empunhadura 2–4 px; pomo contrastante.
- Na espada com bandeira, **não deixar as cores nacionais destruírem a leitura da lâmina**.
- A espada ucraniana de referência usa azul, amarelo, detalhes escuros e cabo quente: manter essa filosofia de contraste.

### Machados e picaretas

- Cabeça identificável mesmo sem cor.
- Espessura visual de 2–4 px para a peça funcional.
- Cabo consistente e conectado, sem pixels soltos.
- Volume por contraste entre parte metálica e madeira.

### Arcos, armas, munição e acessórios

- Silhueta reconhecível; usar ângulo que evidencie o formato.
- Projetar para 16×16, não tentar desenhar todas as peças reais de uma arma.
- Se for arma 3D de mod externo, **não copiar asset nem fingir que o sprite substitui a arma**.
- Para munição, diferenciar ponta, corpo e base em 2–4 grupos de cores.

### Itens políticos/satíricos fictícios

- Converter piada/tema em **ícone legível**, evitando texto minúsculo.
- Ex.: feijão como semente marrom em forma de rim, com 2–3 tons e uma marca de luz; estandarte com padrões simples; livro amarelo com lombada escura e brilho pontual.
- Não fingir que o item está registrado no jogo: listar status `planejado` se for arte conceitual.

### Minérios, cristais, alimentos e recursos

- Minério de inventário: forma irregular, centro claro e arestas escuras.
- Bloco de minério: textura de pedra que mantenha padrão Minecraft e inclusões minerais com alto contraste; preservar capacidade de tile se relevante.
- Comida: contorno orgânico **pixelado**, cores de casca/interior e luz simples; não parecer emoji redimensionado.
- Material mágico: paleta própria, 1–2 pixels de highlight; efeitos luminosos externos ficam para partículas/renderer, não blur dentro do PNG.

## 5. Paleta por material

Monte primeiro uma paleta intencional. Exemplo orientativo, **não obrigatório**:

| Material | Sombra | Médio | Luz |
|---|---|---|---|
| Aço escuro | `#28303A` | `#68798B` | `#D9E5EF` |
| Ouro/amarelo | `#9F6528` | `#E2B52F` | `#FFE36C` |
| Azul bandeira | `#123A82` | `#2872D8` | `#5CA9FF` |
| Madeira | `#583623` | `#94603A` | `#C18C55` |
| Feijão marrom | `#4E2B22` | `#94513C` | `#C8845E` |
| Couro/grafite | `#181B22` | `#3B404A` | `#6B727C` |

Evite usar essas cores mecanicamente em tudo. Para novos itens, **priorize a paleta do universo já existente** e ajuste as cores no contexto das texturas reais do mod.

## 6. Processo obrigatório da IA

1. **Inspecionar a referência existente** (`espada_bandeira_ucrania_16x16.png`) e os sprites atuais do mod.
2. Ler o pedido e identificar: nome, papel, raridade, materiais, simbolismo, uso e tamanho final.
3. Fazer conceito de silhueta em **grade nativa de 16×16**.
4. Definir orientação e contorno.
5. Criar a paleta de materiais.
6. Construir o sprite via Pillow, geração pixel a pixel, SVG com rasterização **sem antialiasing** comprovada, ou ferramenta de pixel art programável.
7. Ajustar luz lateral/superior, sombras, highlights e clusters.
8. Verificar o sprite em 16×16, 64×64 e 256×256 por **nearest-neighbor**.
9. Testar em fundo claro/escuro e em um slot de inventário semelhante ao Minecraft.
10. Validar dimensões, alpha e silhueta.
11. Salvar PNG do jogo separado da preview.
12. Se solicitado, gerar o JSON de item e integrar no registro existente **sem duplicar IDs**.

## 7. Entregáveis

1. `<item>_16x16.png` — PNG jogável RGBA **16×16** com fundo transparente.
2. `<item>_preview.png` — imagem de apresentação ampliada com nearest-neighbor, de preferência com fundo claro e escuro e uma simulação de slot de inventário.
3. Quando **explicitamente solicitado integrar ao mod**: model JSON, registro, lang keys e instruções/resultado dos testes. Não modificar registros se a tarefa for só desenhar o sprite.

### Exemplo de integração Forge 1.20.1

Textura (adaptar ao padrão real do projeto):

`src/main/resources/assets/guerra_ezuberante/textures/item/espada_exemplo.png`

Modelo JSON padrão para espada/item segurado:

```json
{
  "parent": "minecraft:item/handheld",
  "textures": {
    "layer0": "guerra_ezuberante:item/espada_exemplo"
  }
}
```

Para um item plano comum, trocar `handheld` por `generated`. Esses modelos vanilla de item **não criam automaticamente mecânica, dano ou item registrável**. O código Java deve registrar o item e definir propriedades quando solicitado.

**Atenção:** adaptar ao namespace/ID existente; não inserir um arquivo 3D de TaCZ onde o projeto espera um ícone vanilla plano.

## 8. Validação automática mínima (Pillow)

```python
from PIL import Image
from pathlib import Path

arquivo = Path('item_16x16.png')
im = Image.open(arquivo)
assert im.size == (16, 16), f'Tamanho errado: {im.size}'
assert im.mode == 'RGBA', f'Modo errado: {im.mode}'
alpha = list(im.getchannel('A').getdata())
assert any(a == 0 for a in alpha), 'Fundo opaco indevidamente'
assert any(a == 255 for a in alpha), 'Sprite vazio'
assert set(alpha).issubset({0, 255}), 'Pixels semitransparentes/antialias inesperados'
assert im.getchannel('A').getbbox() is not None, 'Item sem desenho'

preview = im.resize((256, 256), Image.Resampling.NEAREST)
preview.save('item_preview_16x.png')
print('Sprite PNG RGBA 16x16: validação básica OK')
```

Essa validação é **necessária mas não suficiente**: um quadrado opaco também passaria. Faça revisão visual da silhueta, leitura em escala 1× e coerência do sombreamento.

Se o item for intencionalmente translúcido (poção/vidro etc.), adapte a checagem do alfa **somente para esse caso**, registrando a razão. Não aplique antialiasing por padrão.

## 9. Critérios de reprovação

Recriar/corrigir caso ocorra qualquer um:

- Arquivo entregue tem 1024 px ou dimensões diferentes sem solicitação.
- Fundo do sprite é preto, branco, cinza ou possui UI/legenda embutida.
- Antialiasing, bordas borradas ou glow fotográfico.
- O item se torna ilegível na escala nativa de 16×16.
- Silhueta desconectada (cabo sem lâmina, pontas quebradas etc.).
- Cores chapadas sem volume em item que pede acabamento detalhado.
- Dithering/ruído sem função visual.
- O item copia arma/textura de terceiros sem permissão.
- `models/item/*.json` aponta para ID de textura inexistente.
- A preview é apresentada como se fosse o PNG de jogo.
- A integração altera atributos ou mecânicas já existentes sem ser solicitada.

## 10. Exemplo de solicitação para usar esta skill

> **Crie o item `[NOME]` para Guerra Ezuberante seguindo `skill_criar_itens.md`.** Categoria: `[espada/ferramenta/consumível/recurso/projétil]`. Aparência: `[DESCRIÇÃO]`. Material e cores: `[DESCRIÇÃO]`. Use o acabamento e a nitidez da Espada/Bandeira da Ucrânia anexada. Quero PNG RGBA **16×16 verdadeiro**, pixel art sem blur, fundo transparente, luz superior/esquerda, sombras nas bordas, silhueta reconhecível em inventário, preview separada e validação automática. Se eu pedir integração, utilize o namespace, os registros e o padrão JSON existentes; não invente mecânicas não especificadas.

**Critério final:** o item deve parecer nativo de um mod Minecraft bem acabado e continuar reconhecível mesmo com **16 pixels de largura**.
