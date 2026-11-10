# Quadros exclusivos do autor — 0.022.4

O autor solicitou manter somente os quadros que enviou, retirando as artes originais do Minecraft. O item continua sendo `minecraft:painting`; nenhuma imagem fornecida pelo autor foi alterada.

## Colocação

`data/minecraft/tags/painting_variant/placeable.json` usa `replace: true` e contém exatamente os dez IDs registrados em `ModPaintings`. A seleção normal deixa de somar os quadros vanilla aos do mod. Os tamanhos e fontes das dez artes permanecem no [catálogo original](QUADROS_0.018.1.md).

A menor arte tem **2 × 2 blocos**. Uma parede de 1 × 1 ou com apenas uma fileira de altura não comporta nenhum desses quadros; a colocação não usa uma arte vanilla como alternativa nem estica a imagem. O Minecraft continua escolhendo entre as variantes de maior área que cabem no local. Para ver formatos menores, limite o espaço disponível ao pendurar o quadro.

## Quadros vanilla em mundos existentes

`decoration/AuthorPaintings.java` trata quadros do namespace `minecraft` quando entram no servidor, inclusive ao carregar entidades salvas. A operação é agendada para a thread do servidor, depois da entrada da entidade; não varre mundos no disco nem força o carregamento de chunks.

1. Tenta artes do autor com largura e altura menores ou iguais às originais, priorizando a maior área e variando entre opções equivalentes pelo UUID.
2. Exige apoio e ausência de colisão com outros quadros pelo teste vanilla `Painting.survives()`.
3. Mantém a mesma entidade, UUID, posição de fixação, orientação e nome personalizado quando a troca cabe.
4. Se não houver arte compatível — por exemplo, num quadro vanilla de 1 × 1 — remove a decoração e devolve um item **Quadro**, seguindo `doEntityDrops`. Com essa regra desativada, não há drop, como na quebra vanilla.

As paredes, os blocos e os quadros do próprio mod ficam intactos. Artes vanilla continuam registradas internamente para permitir ler os saves antigos; isso não as devolve à seleção de colocação. Variantes de outros mods não são migradas pelo evento. Datapacks com prioridade superior ainda podem alterar a tag, como ocorre normalmente no Minecraft.

A migração ocorre por entidade carregada. Decorações em chunks ainda não visitados são tratadas quando esses chunks forem carregados. Não é necessário criar um mundo novo.

## Verificação

O validador de assets exige `replace: true`, exatamente dez entradas únicas e as dez texturas/fontes originais. `PaintingGameTests` cobre registro, tag efetivamente carregada, seleção real em parede ampla, recusa de uma parede de 1 × 1, carregamento de NBT vanilla, manutenção de identidade e limites, devolução única do item e preservação de uma arte já pertencente ao autor.

Os testes usam mundos isolados. Os mundos pessoais não são usados como fixtures.

Resultado de 02/11/2026: **build/check/reobf e 159/159 GameTests sem TaCZ aprovados**, incluindo os cinco testes de quadros. Na preparação da demo v0.1 em 10/11/2026, **build/check/reobf e 223/223 GameTests com TaCZ aprovados** em outro mundo isolado. [Resumo](validacao/quadros-0.022.4/resultado.json). Logs locais: `.tools/paintings-0224-build.log`, `.tools/paintings-0224-tests.log` e `.tools/demo-v01-validation.log`.

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File tools/gradle.ps1 build check compilePistolTestJava --console=plain
# Use uma pasta de servidor isolada com eula.txt aceito pelo operador.
powershell -NoProfile -ExecutionPolicy Bypass -File tools/gradle.ps1 runGameTestServer -PwithoutTacz '-PgameTestWorkDir=.tools/paintings-0224-test' --console=plain
```

JAR local `build/libs/guerra_ezuberante-0.022.4.jar`: SHA-256 `c16533bc74354573257f85edc6858f8deb3351cf735dbda36353ac95cce8f16f`.
