# Demo v0.1 — pacote para jogar

**Demo v0.1** é o nome dado a este pacote de demonstração. O JAR do mod se identifica internamente como **Guerra Ezuberante 0.022.4**; o projeto não foi renumerado artificialmente. Esta demonstração não declara concluídas as metas de conteúdo e homologação da [beta 0.1](BETA_0.1_CHECKLIST.md).

O pacote contém três arquivos independentes na pasta `mods/`: Guerra Ezuberante 0.022.4, GeckoLib 4.8.4 para Forge 1.20.1 e TaCZ 1.1.8-hotfix2 para Forge 1.20.1. TaCZ é opcional para o funcionamento básico, mas está incluído na demo para que o arsenal integrado apareça. As dependências mantêm seus arquivos e créditos originais; veja [dependências externas](DEPENDENCIAS_TERCEROS.md).

Para instalar, use **Minecraft Java 1.20.1**, **Java 17** e **Forge 47.4.28**. Execute esse perfil uma vez e feche o jogo. Extraia o ZIP, copie somente os três JARs de `mods/` para a pasta `mods/` do perfil Forge e retire versões antigas do Guerra Ezuberante, GeckoLib ou TaCZ. Inicie o perfil. Para ver as vilas completas, crie um mundo novo: chunks já explorados não recebem as estruturas novas. Faça cópia dos mundos existentes antes de atualizar qualquer mod.

O ZIP é um conjunto de mods para um perfil Forge existente; não contém o Minecraft, Java ou o instalador Forge. Ele também não deve ser confundido com uma GitHub Release, que exigiria publicação própria. O JAR e o ZIP são gerados localmente a partir do mesmo commit da `main`.

A versão 0.022.4 traz dez quadros próprios como seleção exclusiva do item Quadro; também contém as 24 texturas refeitas na 0.022.3 e a integração anterior do Gabriel. Confira o [README](../README.md) e a [validação](VALIDACAO.md) para o histórico e os limites dos testes. Recursos ainda em planejamento não devem ser anunciados como jogáveis.

Na preparação do pacote em 10/11/2026, o build/check/reobf e **223/223 GameTests com TaCZ** passaram em servidor de teste isolado. A rodada anterior sem TaCZ aprovou **159/159**. Isso valida o código e as dependências usadas, mas não substitui os testes de multiplayer real, geração natural completa e balanceamento necessários antes de chamar o projeto de beta concluída.
