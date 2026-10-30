# Música da vila da Missão — 0.014.3

A contribuição de GabrielKraemer2805 adiciona uma trilha ambiente à vila da Missão. O servidor detecta a presença do jogador na estrutura e envia apenas mudanças de estado; o cliente reproduz a faixa em loop.

## Comportamento

- A música começa ao entrar na caixa horizontal da vila, incluindo casas, ruas e castelo.
- Ao sair, permanece por até oito blocos para não piscar na borda.
- Morte, espectador, troca de dimensão, saída da área e desconexão encerram a faixa.
- A trilha é não posicional e usa a categoria Música.
- A música vanilla é interrompida enquanto a faixa da vila toca.
- Músicas adicionadas por outros mods não são canceladas.
- Recarregar recursos com `F3+T` reinicia o loop se o jogador continuar na vila.
- O modo `-PsilentClient` mantém a sessão completamente silenciosa.

Nesta versão somente a facção Missão possui uma faixa. Entrar em vilas das outras cinco facções não inicia música.

## Rede e instalação

O canal `guerra_ezuberante:faction` usa o protocolo **4** e inclui o pacote S2C `VillageMusic`, ID 8. Cliente e servidor precisam executar a mesma versão `0.014.3`; versões anteriores são recusadas no handshake em vez de interpretar o pacote incorretamente.

O arquivo empacotado é `assets/guerra_ezuberante/sounds/batalha/renan_santos.ogg`: Vorbis, estéreo, 44.100 Hz e streaming. O original de edição permanece fora do Git.

## Validação

As regressões permanentes verificam:

- detecção por metadados reais de `StructureStart`;
- silêncio nas vilas sem faixa;
- margem de oito blocos sem ampliar a área inicial;
- limpeza em morte, espectador e outra dimensão;
- estado independente por jogador e remoção no logout.

Build, validadores de assets, GameTests com e sem TaCZ e cliente real silencioso devem ser executados na integração. A validação automatizada confirma eventos e estado, não qualidade sonora percebida.
