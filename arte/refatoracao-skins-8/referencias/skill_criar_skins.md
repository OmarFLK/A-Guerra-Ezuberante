---
name: criar-skins-guerra-ezuberante
description: Crie e valide skins de entidades no estilo oficial do Guerra Ezuberante, em atlas PNG RGBA 64x64 Minecraft Java (Steve clássico), com pixel art detalhada, UV correto, sombras coerentes e prévias fiéis.
---

# SKILL — Criar skins Minecraft para o Guerra Ezuberante

> **Uso:** instruções permanentes para Codex, Claude Code ou outra IA que desenhe e integre skins do mod. **Entregável obrigatório:** PNG **real** de `64×64` px, pronto para Minecraft Java 1.20.1 / Forge / GeckoLib 4. A imagem de apresentação é um arquivo **separado**: ela não substitui o atlas jogável.

## 1. Objetivo e padrão de qualidade

Gerar skins reconhecíveis e expressivas, com o visual das referências anexadas pelo autor: Jair Bolsonaro, Flávio Bolsonaro, Lula, Arthur do Val, Renan Santos e combatente ucraniana. O padrão é **pixel art Minecraft bem trabalhada**, com formas nítidas, sombreamento nas laterais, roupas coerentes de todos os ângulos, rosto legível em **8×8 px**, costas realmente desenhadas e anatomia correta no modelo cúbico.

**As referências são a fonte visual prioritária.** Inspecione os PNGs se estiverem disponíveis na pasta `referencias/` ou no workspace. Copie a **qualidade visual e o método**, não a identidade de um personagem em outro. Evite recriar uma skin existente do zero quando o pedido for somente corrigir detalhes dela.

**Prioridades (nesta ordem):** UV funcional > reconhecimento do personagem > silhueta/roupa > luz/sombra > acabamento e prévia. Uma ilustração bonita, mas com UV incorreto, **não é uma skin válida**.

### Características verificadas nas referências

- Arquivos de jogo: **64×64 RGBA**, 100% pixels alinhados à grade, sem antialiasing.
- Regiões das seis faces de cada parte do corpo: pintadas de forma coerente, incluindo frente, costas, lados, topo e base.
- Pele: sombra no contorno/laterais, cores médias no centro e luz limitada em testa, nariz, bochecha e queixo.
- Cabelo: contorno escuro, volume em blocos de 1–3 px, variações discretas nas mechas, continuidade na nuca e laterais.
- Roupas: **3–6 níveis de valor por material** quando necessário, com costuras, golas, punhos, botões, sombras debaixo dos braços e pequenas dobras.
- Jeans: azul-escuro com variações azuladas; pernas separadas, barras e sapatos perceptíveis.
- Ternos: preto/grafite com sombras azuis ou cinzentas, ombros legíveis e pequenos highlights.
- Detalhes memoráveis: a faixa verde-amarela de Jair, a gola e botões da camisa social de Flávio, a barriga/camisa vermelha do Lula, detalhes da roupa e do cabelo dos demais.
- O número total de cores **não precisa ser rigidamente limitado**: nas referências há skins com poucas dezenas e outras com muitas tonalidades próximas; o importante é a coesão e **não parecer ruído aleatório**.

## 2. Contrato de entrega — nunca negociar

Entregar no mínimo:

1. `<personagem>_skin_64x64.png` — **64×64 pixels exatos**, modo RGBA, atlas Minecraft Java clássico/Steve (braços de **4 px**), transparente nas áreas não utilizadas.
2. `<personagem>_preview.png` — imagem ampliada com **frente, costas, rosto ampliado e atlas UV**; opcionalmente, vistas laterais. Separada do PNG de jogo.
3. Um resumo curto com caminho dos arquivos, dimensões verificadas, UV escolhido e o que foi modificado.

Se o personagem utilizar um modelo GeckoLib com barriga, cabelo volumoso, acessórios ou partes articuladas, **produzir também o modelo e/ou orientar a integração** sem falsificar a geometria na skin. A skin 2D **não cria sozinha** uma barriga tridimensional nem bone extras.

**Nunca:** salvar uma arte gerativa 1024×1024 reduzida para 64×64 esperando que vire um atlas válido; redimensionar uma foto; embutir textos, legendas ou fundos no PNG jogável; enviar apenas o mockup; pintar mãos nas posições do braço errado; deixar o verso igual à frente; confundir Steve com Alex (3 px de braço).

## 3. Mapa UV canônico — Minecraft Java moderno, modelo Steve

Trabalhe com coordenadas **`(x0, y0, x1, y1)` exclusivas no extremo final** (convenção Pillow `crop`/`paste`). Oriente e espelhe cada face adequadamente ao montar um cubo: a tabela indica a região na folha, **não** autoriza copiar a mesma imagem para todas as faces sem considerar o sentido de leitura.

| Parte / camada base | Topo | Base | Lateral direita do modelo | Frente | Lateral esquerda do modelo | Costas |
|---|---|---|---|---|---|---|
| Cabeça, 8×8×8 | `(8,0,16,8)` | `(16,0,24,8)` | `(0,8,8,16)` | `(8,8,16,16)` | `(16,8,24,16)` | `(24,8,32,16)` |
| Tronco, 8×12×4 | `(20,16,28,20)` | `(28,16,36,20)` | `(16,20,20,32)` | `(20,20,28,32)` | `(28,20,32,32)` | `(32,20,40,32)` |
| Braço direito, 4×12×4 | `(44,16,48,20)` | `(48,16,52,20)` | `(40,20,44,32)` | `(44,20,48,32)` | `(48,20,52,32)` | `(52,20,56,32)` |
| Perna direita, 4×12×4 | `(4,16,8,20)` | `(8,16,12,20)` | `(0,20,4,32)` | `(4,20,8,32)` | `(8,20,12,32)` | `(12,20,16,32)` |
| Perna esquerda, 4×12×4 | `(20,48,24,52)` | `(24,48,28,52)` | `(16,52,20,64)` | `(20,52,24,64)` | `(24,52,28,64)` | `(28,52,32,64)` |
| Braço esquerdo, 4×12×4 | `(36,48,40,52)` | `(40,48,44,52)` | `(32,52,36,64)` | `(36,52,40,64)` | `(40,52,44,64)` | `(44,52,48,64)` |

### Segunda camada (hat/jacket/sleeves/pants)

As áreas de overlay são independentes, para detalhes **sobre** o corpo base; o que não for usado fica com `alpha=0`.

| Camada extra | Topo | Base | Lateral direita | Frente | Lateral esquerda | Costas |
|---|---|---|---|---|---|---|
| Cabeça (chapéu/cabelo) | `(40,0,48,8)` | `(48,0,56,8)` | `(32,8,40,16)` | `(40,8,48,16)` | `(48,8,56,16)` | `(56,8,64,16)` |
| Tronco (jaqueta) | `(20,32,28,36)` | `(28,32,36,36)` | `(16,36,20,48)` | `(20,36,28,48)` | `(28,36,32,48)` | `(32,36,40,48)` |
| Braço direito (manga) | `(44,32,48,36)` | `(48,32,52,36)` | `(40,36,44,48)` | `(44,36,48,48)` | `(48,36,52,48)` | `(52,36,56,48)` |
| Perna direita (calça) | `(4,32,8,36)` | `(8,32,12,36)` | `(0,36,4,48)` | `(4,36,8,48)` | `(8,36,12,48)` | `(12,36,16,48)` |
| Perna esquerda (calça) | `(4,48,8,52)` | `(8,48,12,52)` | `(0,52,4,64)` | `(4,52,8,64)` | `(8,52,12,64)` | `(12,52,16,64)` |
| Braço esquerdo (manga) | `(52,48,56,52)` | `(56,48,60,52)` | `(48,52,52,64)` | `(52,52,56,64)` | `(56,52,60,64)` | `(60,52,64,64)` |

**Atenção:** skins modernas não usam simplesmente o mesmo braço/perna espelhado automaticamente. Preencha **ambos os membros explicitamente**, inclusive lados, costas, topo e base. Para acessórios sobressalentes, preferir a segunda camada ou um bone do GeckoLib.

## 4. Direção de arte: como conseguir a qualidade das referências

### 4.1 Iluminação coerente (mais importante que quantidade de detalhes)

Adote uma luz principal levemente vinda de **cima/frente/esquerda do personagem**, e mantenha sua lógica em todas as partes:

- Topos e pontos expostos: tom mais claro.
- Frente: valor médio com highlights pontuais.
- Laterais: uma lateral mais sombreada; a outra recebe uma faixa discreta de luz.
- Costas: valor um pouco mais escuro que a frente, mas com textura própria.
- Debaixo do queixo, do cabelo e da gola, sob a manga e entre as pernas: sombra de oclusão.
- Nos cantos de cubos e bainhas: escurecimento seletivo, não uma borda preta contínua em tudo.

Use diferenças graduais de **aprox. 8%–20%** entre faces em uma primeira tentativa, ajustando material e cena. Nunca transformar todas as faces em uma cor chapada.

### 4.2 Rosto Minecraft 8×8 — retrato precisa funcionar em poucos pixels

- Defina linha de cabelo/testa nos pixels superiores; respeite também topo, nuca e têmporas.
- Dois olhos pequenos e bem posicionados; sobrancelhas de 1 px de espessura ou agrupamento que mantenha expressão legível.
- Nariz: variação sutil de luz/sombra; evite uma listra enorme no meio da face.
- Boca: 1–3 px de leitura clara, não uma mancha preta larga.
- Mandíbula, bochechas, barba e idade por paleta e agrupamento de pixels, não degradês suaves.
- Para retratos de pessoas reais, buscar **semelhança estilizada**, sem pretender reproduzir fisionomia fotográfica com 8×8.
- Conferir a consistência entre frente, laterais e costas da cabeça: cabelo não pode acabar abruptamente no meio da orelha.

### 4.3 Roupa — acabamento que diferencia uma skin boa

- **Camisa social:** gola, abertura central, 2–4 botões minúsculos, tons distintos no peito, punho/manga dobrada que continue pela lateral e verso.
- **Terno:** lapelas e gravata/faixa com diagonais de 1 px; ombros mais escuros nas dobras; sombras sob mangas.
- **Camisa simples:** peito com luz, bordas laterais escuras, barra inferior; emblema pequeno, de alta legibilidade, quando pedido.
- **Jeans:** azul escuro com manchas controladas de 1–2 px sugerindo tecido, leves costuras, joelhos e barras; sapatos separados da calça.
- **Pele:** mão termina onde a manga termina em **todas as faces do braço**; não deixar punhos assimétricos involuntários.
- **Faixas/brasões:** seguir a direção da peça sobre frente e ombro; nunca atravessar o bone com o padrão em lugar arbitrário.
- Desenhe as **costas de verdade**: nuca, caimento da roupa, cintura, bolso/costura, cabelo traseiro.

### 4.4 Pixel art limpa

- Um pixel da arte = um pixel do PNG final, sem interpolação.
- Não usar `GaussianBlur`, bilinear/bicubic, antialiasing, degradê contínuo nem efeitos de brilho fotográfico.
- Textura rica não significa ruído: cada cluster de pixels deve sugerir volume, material, cabelo, costura ou desgaste.
- Evitar padrões repetidos tipo xadrez em pele/roupa.
- Manter silhueta reconhecível **também a 1×, 4× e no modelo in-game**.
- Utilizar alfa 0 para regiões não usadas; para base do corpo, preferência por alfa 255 em todas as seis faces. Overlay pode ter transparência.

## 5. Processo obrigatório para a IA

1. **Inspecionar referências** e o personagem solicitado: cores, rosto, roupa, acessórios, idade, silhueta.
2. Definir **paleta por material**: pele, cabelo, roupa, calça, calçado, acessórios; 3–6 valores por material conforme necessidade.
3. Criar um `Image.new('RGBA', (64,64), (0,0,0,0))` por script (Pillow ou equivalente).
4. Gerar **cada face de cada parte do corpo** obedecendo à tabela UV. Preferir funções `paint_face`, `shade_face`, `apply_detail`, `mirror_or_rotate_face` com orientação comprovada.
5. Pintar rosto antes de detalhes menores e conferir leitura em 8×8.
6. Pintar torso + braços + pernas, respeitando continuidade das roupas.
7. Pintar topo, laterais, costas e base; não deixar áreas de pele/tecido invisíveis.
8. Adicionar segunda camada **somente quando melhorar volume**, cuidando de alpha e compatibilidade com o renderer.
9. Salvar a skin definitiva em PNG RGBA 64×64; **não** salvar no lugar da skin uma preview ou prompt gráfico.
10. Renderizar um personagem a partir da textura **usando UV verdadeiro** para pelo menos frente e costas; ideal: perspectiva 3D em navegador/renderer local. Se não houver renderer disponível, fazer um preview 2D fiel a partir dos retângulos do atlas e deixar explícita a limitação.
11. Revisar visualmente: costuras, lados da cabeça, cabelo, punhos, mãos, sapatos, duplicações, reflexos, manchas, emblemas.
12. Repetir a geração até corrigir defeitos visíveis. Entregar somente depois de validar.

## 6. Validação automática mínima (Python/Pillow)

Execute um script equivalente a este em todo PNG final:

```python
from PIL import Image
from pathlib import Path

skin_path = Path('personagem_skin_64x64.png')
im = Image.open(skin_path)
assert im.size == (64, 64), f'Dimensões incorretas: {im.size}'
assert im.mode == 'RGBA', f'Modo incorreto: {im.mode}'

# Regiões frontais BASE que devem estar completamente desenhadas.
frentes = {
    'rosto': (8, 8, 16, 16),
    'peito': (20, 20, 28, 32),
    'braco_direito': (44, 20, 48, 32),
    'braco_esquerdo': (36, 52, 40, 64),
    'perna_direita': (4, 20, 8, 32),
    'perna_esquerda': (20, 52, 24, 64),
}
for nome, rect in frentes.items():
    alfa = im.getchannel('A').crop(rect)
    mn, mx = alfa.getextrema()
    assert mn == 255 and mx == 255, f'{nome}: base com buracos/alpha incorreto'

# PNG não pode conter antialiasing inadvertido nas bordas transparentes.
assert set(im.getchannel('A').getdata()).issubset({0, 255}), 'Alfa parcial inesperado'

# Deve haver transparência no canvas (áreas UV não usadas).
assert im.getchannel('A').getbbox() is not None
assert min(im.getchannel('A').getdata()) == 0, 'Atlas inteiro opaco; possível fundo indevido'
print('Skin 64x64 RGBA: verificações automáticas básicas OK')
```

**Isso é só uma checagem sintática:** os testes não garantem que a orientação das faces esteja correta. O agente **também deve renderizar e inspecionar visualmente** o modelo com o atlas. Conferir todas as faces, overlays e orientação dos braços/pernas.

Se a skin usa antialiasing intencional na segunda camada (não recomendado neste estilo), documente e ajuste a validação; **não ignore** resultados de teste de forma silenciosa.

## 7. Integração com o mod

- Minecraft Java 1.20.1 / Forge / GeckoLib 4.
- Respeitar o **mod id e estrutura reais** encontrados no workspace (`guerra_ezuberante` no projeto conhecido).
- Caminho usual: `src/main/resources/assets/guerra_ezuberante/textures/entity/<personagem>.png`.
- Se o renderer ou o arquivo `.geo.json` já esperam outro nome, preservar e adaptar cuidadosamente.
- Para GeckoLib, verificar dimensões de textura (`texture_width=64`, `texture_height=64` ou equivalentes) e UV dos cubos.
- Se forem adicionados bones para dedos, barriga, cabelo ou acessórios, modelar de modo que o atlas continue coerente; pode ser necessário planejar UV extra/overlays. **Não criar novos bones com UV por suposição e sem preview.**
- Não alterar skins de personagens existentes sem necessidade ou autorização.
- Se solicitado apenas **criar PNG**, não implementar entidade, IA, modelo ou alterar o código do mod além do necessário para salvar a textura.

## 8. Referências anexadas e o que aprender com cada uma

Consulte os exemplos, quando disponíveis:

| Arquivo da referência | Foco técnico/estético |
|---|---|
| `flavio_bolsonaro_skin_64x64.png` | Camisa social azul: gola, botões, mangas dobradas, jeans escuro, sombra lateral, relógio, cabelo e rosto em 8×8. |
| `jair_bolsonaro_skin_64x64.png` | Terno escuro com relevo tonal; faixa verde-amarela diagonal legível no torso; cabelo grisalho/laterais. |
| `lula_skin_64x64.png` | Camisa vermelha, rosto mais idoso, cabelo/barba cinzentos, jeans; leitura de volume do tronco. |
| `arthur_do_val_skin_64x64_corrigida.png` | Roupa clara e contraste de detalhes; atenção ao contorno do rosto e continuidade das laterais. |
| `renan_santos_skin_64x64.png` | Roupa preta sem virar uma massa uniforme; contraste de pele/cabelo e detalhes de traje. |
| `ucraniana_loira_skin_64x64.png` | Cabelo loiro, roupa clara, equilíbrio de tons e silhueta com cores de contraste. |
| `*_preview.png` | **Somente padrão de apresentação**. Nunca confundir preview ampliada com atlas Minecraft de 64×64. |

**Observação importante:** uma das referências de Flávio/Jair foi corrigida historicamente nas laterais da cabeça; se houver versão `*_corrigida.png` no workspace, dê preferência a ela para detalhes de cabelo/orelha.

## 9. Critérios de reprovação (corrigir antes de entregar)

- A imagem de jogo não mede 64×64, não é RGBA, ou mistura arte ilustrativa com atlas.
- Cabeça de frente não cai no retângulo `(8,8,16,16)`.
- Os braços ou as pernas estão trocados, espelhados erroneamente ou incompletos.
- Laterais de cabeça têm grandes manchas vazias, orelhas desproporcionais ou cabelo cortado.
- Costas são cópia literal da frente com olhos, botões e acessórios no lugar errado.
- O emblema atravessa superfícies incorretas.
- Pele ou roupa apresentam buracos em faces básicas.
- A skin usa blur ou pixels suavizados.
- Braços de 3 px foram usados sem solicitação; o padrão é Steve com braço de 4 px.
- Preview é bonita, mas o render real mostra falhas.
- A IA diz “está pronta” sem validar tamanho, modo e render de frente/costas.

## 10. Modelo de solicitação para usar esta skill

> **Crie a skin Minecraft do personagem [NOME] para Guerra Ezuberante, seguindo integralmente `skill_criar_skins.md`.** Roupa: [DETALHES]. Idade/cabelo/rosto: [DETALHES]. Acessórios: [DETALHES]. Preserve o estilo das referências. Entregue PNG RGBA jogável 64×64, UV Steve completo, frente/costas/laterais realmente detalhadas, iluminação coerente entre faces, preview ampliada separada e testes automáticos + renderização visual. Não entregue imagem conceitual no lugar da skin. Se o workspace contém uma skin anterior, reutilize-a quando apropriado e corrija apenas o necessário.

**Critério final:** quem colocar o PNG no renderer correto deve ver um personagem limpo, reconhecível, bonito e consistente de todos os lados, **igual à qualidade das referências anexadas**.
