# Changelog

Todas as mudanças relevantes deste projeto de controle de estoque (JavaFX + Design System)
sao documentadas neste arquivo, por data e arquivo.

## [Em revisao] Correcoes visuais, aparência completa e alinhamentos (2026-09-22)

### Bug 1: barra colorida dos cards de estatistica (StatCard)
A barra lateral colorida era um `Region` sobreposto com raio proprio, deixando um vao
branco nas esquinas do card. Adotada a logica de "div pai colorida + div filha branca":
o card (pai) recebe a cor de status de fundo (raio 12) e o conteudo real fica numa
VBox branca (filha) recuada da esquerda via `background-insets`; o verde/ambar que sobra
a esquerda forma a barra, que nasce do raio do pai. Um `clip` arredondado no pai garante
que a filha nunca vaze para fora dos limites do card (equivalente a `overflow: hidden`).

- `src/br/com/distribuidora/view/components/StatCard.java`
  - Removido o `Region barra` (4px) que era sobreposto como filho do card.
  - Conteudo (`stat-card-content`) passa a crescer na horizontal (`HBox.setHgrow`) para
    preencher o card, deixando visivel apenas a barra esquerda da cor de status.
  - Novo `setClip(Rectangle)` com arco 24 (radius 12) amarrado ao tamanho do card,
    impedindo qualquer vazamento do conteudo para fora do raio (overflow hidden).
  - Mantida a classe `warning` para cards de atencao.
- `src/css/components.css`
  - `.stat-card` passa a ter a cor de status como `-fx-background-color`
    (`-bm-green-600`; `-bm-amber-600` em `.stat-card.warning`), raio 12 e o dropshadow.
  - `.stat-card-content` recebe `-fx-background-color: -bm-surface` (branco/superficie),
    `-fx-background-radius: 0 12 12 0` e `-fx-background-insets: 0 0 0 6` (recuo esquerdo
    de 6px que forma a barra colorida).
  - Removida a abordagem anterior de `border-left` (`-fx-border-*`) do card.

### Bug 2: alinhamento header x celula nas tabelas
Os cabecalhos ficavam a esquerda enquanto as celulas numericas estavam a direita (ou, em
Relatorios, ambas a esquerda). Colunas numericas agora recebem a classe
`numeric-column`, que alinha header e celula a direita de forma consistente.

- `src/css/components.css`
  - Novas regras `.table-view .column-header.numeric-column .label` e
    `.table-view .table-cell.numeric-column` com `-fx-alignment: CENTER_RIGHT`.
  - Mecanismo: a styleClass da `TableColumn` e refletida no no do cabecalho e nas
    celulas (comportamento interno do JavaFX; confirmado no fonte 21.0.5).
- `src/br/com/distribuidora/view/BebidasView.java`
  - Colunas `Preco` e `Estoque` ganharam a classe `numeric-column`.
- `src/br/com/distribuidora/view/DashboardView.java`
  - Mini-tabela de estoque baixo: colunas `Preco` e `Estoque` com `numeric-column`.
- `src/br/com/distribuidora/view/RelatorioView.java`
  - Colunas numericas marcadas em todas as tabelas:
    Vendas (`Qtd. itens`, `Valor total`), Estoque (`Estoque atual`, `Estoque minimo`,
    `Preco`), Produtos (`Preco de compra`, `Preco de venda`, `Estoque`), Financeiro
    (`Valor`) e Compras (`Qtd. produtos`, `Valor total`).

Colunas de texto (Codigo, Nome, Categoria, Marca, Fornecedor, Status, Validade) seguem
alinhadas a esquerda, sem alteracao. O `setAlignment(CENTER_RIGHT)` ja existente nas
celulas de `components/TabelaCelulas.java` foi mantido (reforco, sem conflito).

### Bug 3: texto de CheckBox/RadioButton invisivel no tema claro
Os textos dos checkbox de "Formas de pagamento" e dos radios de "Aparencia" herdavam a
cor do lookup modena `-fx-text-background-color` (ladder), que em alguns contextos
resolvia para branco sobre fundo claro. Nao havia cor hardcoded, mas faltava uma regra
de token.

- `src/css/base.css`
  - Nova regra `.check-box, .radio-button { -fx-text-fill: -bm-text; }` ao lado do
    `.label`, usando o mesmo token de texto do tema (`-bm-text`). Vale para os unicos
    pontos que usam esses controles: `ConfiguracaoView` (abre Vendas - Formas de
    pagamento - e Aparência).

### Bug 4a: switches fora de coluna (Configuracoes)
Os toggles de `criarSwitch` ficavam logo apos o texto do label, em X diferentes por
linha, porque a linha nao ocupava a largura do container.

- `src/br/com/distribuidora/view/ConfiguracaoView.java`
  - `criarSwitch`: a linha (`HBox`) agora usa `setMaxWidth(Double.MAX_VALUE)` e recebe a
    classe `setting-row`. Com o `HBox.setHgrow(label, ALWAYS)` ja existente, o label
    consome o espaco restante e o switch fica preso na mesma posicao X (borda direita)
    em todas as secoes (Estoque, Descontos, Fluxo de venda e Config. Gerais).

### Bug 4b: botoes "Filtrar"/"Limpar filtros" desalinhados dos campos de data (Relatorios)
A linha do card de filtros centralizava verticalmente os filhos; os grupos de data tem
label acima, entao os botoes ficavam fora da base dos inputs.

- `src/br/com/distribuidora/view/RelatorioView.java`
  - `criarCardFiltros`: `linha.setAlignment(Pos.CENTER_LEFT)` passou a
    `Pos.BOTTOM_LEFT`, alinhando os botoes pela base das caixas de data
    (equivalente a `align-items: flex-end`).

### Tema escuro funcionando (aba Aparência)
A troca de tema deixou de ser apenas um aviso e agora aplica e persiste a escolha.

- `src/css/tokens-escuro.css` (novo)
  - Paleta escura via os mesmos nomes `-bm-*` (neutros `#111815`/`#1A231F`/`#E6F0EA`,
    estados e fundos de badge ajustados para dark); marca (verdes/Âmbar) mantida.
- `src/br/com/distribuidora/ThemeService.java` (novo)
  - Guarda a `Scene` e troca `tokens.css` <-> `tokens-escuro.css` em
    `scene.getStylesheets()`. Lookups `-bm-*` resolvem para o último stylesheet que
    as define, entao a troca se reflete em toda a UI na hora.
- `src/br/com/distribuidora/repository/ConfiguracaoStore.java`
  - Campo `tema` (padrao `claro`) carregado/salvo em `config.properties`
    (`~/.bebmais/config.properties`).
- `src/br/com/distribuidora/controller/ConfiguracaoController.java`
  - Novos `tema()` e `salvarTema(String)`.
- `src/br/com/distribuidora/Main.java`
  - `ThemeService.registrar(scene)` na inicializacao: aplica o tema persistido.
- `src/br/com/distribuidora/view/ConfiguracaoView.java`
  - Radios de tema iniciam refletindo o salvo; "Aplicar" salva e aplica o tema
    (Claro/Escuro/"Sistema"); "Restaurar padrao" volta ao claro e persiste.

### Tema "Sistema" corrigido
O radio "Sistema" antes caia sempre em Claro. Agora le o tema real do SO.

- `src/br/com/distribuidora/SistemaTema.java` (novo)
  - Deteccao via registro do Windows (`AppsUseLightTheme` em
    `HKCU\...\Themes\Personalize`): `0x1` = claro, `0x0` = escuro.
    Fora do Windows cai em claro.
- `src/br/com/distribuidora/ThemeService.java`
  - `aplicarTema("sistema")` resolve para o tema efetivo detectado; feedback
    mostra o resultado, ex.: `sistema (escuro)`.

### Tamanho da fonte funcionando (Pequena / Média / Grande)
- `src/css/base.css` e `src/css/components.css`
  - Todos os `-fx-font-size` absolutos viraram `em` relativos ao `.root`
    (ancora 13px em `tokens*.css`). Identico na Média, escalona nas demais -
    titulos, tabelas, badges, botoes e campos acompanham.
- `src/css/fontes-pequena.css` (novo): `.root { -fx-font-size: 11.5px; }`
- `src/css/fontes-grande.css` (novo): `.root { -fx-font-size: 15px; }`
- `src/br/com/distribuidora/ThemeService.java`
  - `aplicarFonte(fonte)` adiciona/remove o override correspondente (nada em Média).

### Densidade funcionando (Confortável / Compacta)
- `src/css/densidade-compacta.css` (novo)
  - Spacing global de `VBox`/`HBox` reduzido (10/8), padding do conteudo e
    paineis enxutos, botoes/abas/linhas da sidebar e cabecalho de tabela menores.
    Classes que definem spacing proprio (ex.: `.sidebar`, `.setting-row`) mantem
    o valor delas por especificidade.
- `src/br/com/distribuidora/ThemeService.java`: `aplicarDensidade(...)`.

### Persistencia e handler Aplicar/Restaurar
- `src/br/com/distribuidora/repository/ConfiguracaoStore.java`
  - Campos `tema`, `fonte` e `densidade` salvos em `config.properties`.
- `src/br/com/distribuidora/controller/ConfiguracaoController.java`
  - Getters/setters de `tema`, `fonte`, `densidade`.
- `src/br/com/distribuidora/view/ConfiguracaoView.java`
  - Combos de fonte/densidade e radios iniciam refletindo o salvo; "Aplicar"
    aplica e persiste os tres; "Restaurar padrao" volta a claro/Média/Confortável.

### Verificacao
- `mvn compile`: OK (apenas warnings do JDK 25).
- Smoke `javafx:run`: aplicacao iniciou e permaneceu ativa 90s sem excecoes.

## [Em revisao] Separador de milhar e categoria tipada (2026-09-22)

### Separador de milhar no preco
- `src/br/com/distribuidora/util/Formatadores.java`
  - `moeda(...)` trocou `String.format` por `DecimalFormat` (`pt_BR`, 2 casas,
    agrupamento ativo): `"R$ 1.234,56"`. Vale para tabelas, dashboard e relatorios.

### Categoria vira enum (fonte unica da lista)
- `src/br/com/distribuidora/model/Categoria.java` (novo)
  - Enum com as 8 categorias do cadastro: Refrigerante, Água, Suco, Cerveja,
    Energético, Vinho, Destilado, Outro. Helpers `getLabel()`, `porLabel()` e
    `labels()`.
- `src/br/com/distribuidora/model/Bebida.java`
  - `categoria` passa de `String` para `Categoria`; `descricao()` usa o label.
- `src/br/com/distribuidora/repository/EstoqueRepository.java`
  - Sementes usam os valores do enum.
- `src/br/com/distribuidora/controller/EstoqueController.java`
  - `categoriasOrdenadas()` agora retorna a lista completa do enum (ordena por
    label), nao apenas as que existem no estoque.
- `src/br/com/distribuidora/view/CadastroBebidaView.java`
  - Combo de categoria usa `Categoria.labels()`; salvar converte via `porLabel`.
- `src/br/com/distribuidora/view/BebidasView.java` e `DashboardView.java`
  - Colunas e filtro mostram/comparam pelo label.
- `src/br/com/distribuidora/controller/RelatorioController.java`
  - Mocks dos relatorios recebem o label da categoria.

### Verificacao
- `mvn compile`: OK (apenas warnings do JDK 25).

## [Em revisao] Sistema de loading (UX) (2026-09-22)

Feedback visual quando uma acao troca de tela ou processa dados, para o cliente
perceber que o app esta respondendo. Dois modos na mesma camada, com tempo minimo
de 400ms para nunca "piscar" nas operacoes in-memory.

- `src/br/com/distribuidora/view/components/LoadingOverlay.java` (novo)
  - Camada com dois modos: barra fina no topo (ProgressBar indeterminada, ambar)
    e overlay central (spinner verde + rotulo) que bloqueia cliques na area de
    conteudo.
- `src/br/com/distribuidora/view/LoadingService.java` (novo)
  - Singleton (padrao do ThemeService): `barra()`, `central(rotulo)`, `parar()`
    com contador de chamadas aninhadas e tempo minimo de exibicao de 400ms.
    Arquitetura pronta para operacoes async (Task/cenario real).
- `src/br/com/distribuidora/view/MainLayout.java`
  - Camada registrada no `StackPane` central; `navegar(...)` dispara a barra ao
    trocar de pagina.
- `src/br/com/distribuidora/view/RelatorioView.java` e `ConfiguracaoView.java`
  - Troca de abas dispara a barra; "Filtrar"/"Limpar filtros" tambem.
- `src/br/com/distribuidora/view/CadastroBebidaView.java` e `BebidasView.java`
  - Salvar/Excluir usam o overlay central ("Salvando bebida...",
    "Excluindo bebida...").
- `src/css/components.css`
  - Estilos `.loading-bar*`, `.loading-fundo`, `.loading-card`, `.loading-spinner`,
    `.loading-label` (funciona nos temas claro e escuro).

### Verificacao
- `mvn compile`: OK (apenas warnings do JDK 25).
- Smoke `javafx:run`: aplicacao iniciou e permaneceu ativa sem excecoes.

## [Em revisao] Registro de venda no balcao (PLANO_NOVA_VENDA) (2026-09-22)

Opcao 1 do `PLANO_NOVA_VENDA.md`: tela "Nova Venda" com carrinho de varios itens,
selecao da forma de pagamento (respeitando as configuracoes), desconto com limite e
baixa automatica no estoque ao finalizar. Implementado em 5 fases (0 a 4).

- `src/br/com/distribuidora/repository/ConfiguracaoStore.java`
  - Novos campos persistidos em `config.properties`: `pagamentos` (separados por
    virgula), `desconto` (boolean), `limite_desconto` e `imprimir_comprovante`.
  - Constante `FORMAS_PAGAMENTO_PADRAO` (Dinheiro, Cartao de credito, Cartao de
    debito, Pix, Transferencia bancaria) usada como padrao se o arquivo nao tiver
    o valor salvo.
- `src/br/com/distribuidora/controller/ConfiguracaoController.java`
  - Exposicoes/salvadores: `formasPagamentoPadrao()`, `formasPagamentoHabilitadas()`,
    `salvarFormasPagamento()`, `permitirDesconto()`, `salvarPermitirDesconto()`,
    `limiteDescontoPercentual()`, `salvarLimiteDescontoPercentual()`,
    `imprimirComprovante()`, `salvarImprimirComprovante()`.
- `src/br/com/distribuidora/view/ConfiguracaoView.java`
  - Aba "Vendas" agora constroi os 5 CheckBox de pagamento a partir do estado salvo
    (referencias em `checksPagamento`) e guarda os toggles de desconto e comprovante
    para persistencia real no Salvar (o switch "Exigir confirmacao antes de cancelar
    venda" segue decorativo, fora de escopo). Validacao do limite de desconto
    mantida. Helpers `novoSwitch`/`linhaSwitch` extraidos de `criarSwitch`.
- `src/br/com/distribuidora/model/ItemVenda.java` (novo)
  - Item de carrinho: `bebidaId`, `nomeBebida` (snapshot), `quantidade`,
    `precoUnitario` e `subtotal()` (HALF_UP, 2 casas).
- `src/br/com/distribuidora/model/Venda.java` (novo)
  - Venda: `id` (int, embora o proximoId use base 1), `dataHora`, itens,
    `formaPagamento`, `descontoPercentual`, `status` ("Concluida"),
    `subtotalBruto()`, `valorDesconto()` e `valorTotal()`.
- `src/br/com/distribuidora/repository/VendaRepository.java` (novo)
  - Singleton no padrao `EstoqueRepository`: `adicionar`, `listar`, `proximoId`.
- `src/br/com/distribuidora/controller/VendaController.java` (novo)
  - Singleton com `formasPagamentoDisponiveis()`, `permitirDesconto()`,
    `limiteDescontoPercentual()`, `imprimirComprovante()`, `listarVendas()` e
    `registrarVenda(itens, formaPagamento, desconto)` validando: itens vazios,
    quantidade > 0, estoque suficiente por bebida, forma de pagamento habilitada,
    desconto nao negativo, desconto permitido e dentro do limite. Ao finalizar,
    gera a `Venda` com status/valor e da baixa no estoque via
    `EstoqueRepository.atualizar` (so depois de todas as validacoes passarem).
- `src/br/com/distribuidora/view/NovaVendaView.java` (novo)
  - Dois paineis (`panel`): a esquerda seletor de bebida (nome, marca e estoque
    disponivel) + quantidade (TextFormatter de digitos) + "Adicionar" (junta
    quantidades da mesma bebida, verificando estoque) e a tabela do carrinho com
    remocao por linha e subtotais; a direita forma de pagamento, campo de
    desconto (%) que so aparece se `permitirDesconto` (com ajuda "Desconto maximo
    permitido: X%"), resumo (subtotal/desconto/total) e botoes "Finalizar venda"
    (desabilitado com carrinho vazio; usa `LoadingService.central`) e "Cancelar".
    Feedback de erro amigavel reutilizando a mensagem da `IllegalArgumentException`.
    Toasts de confirmacao (inclusive o comprovante simulado, quando a config
    "Imprimir comprovante automaticamente" estiver ativa).
- `src/br/com/distribuidora/view/MainLayout.java`
  - Caso `VENDAS` roteia para `novaVenda()` (antes ficava vazio).
- `src/br/com/distribuidora/util/Formatadores.java`
  - Novo `dataHora(LocalDateTime)` em "dd/MM/yyyy HH:mm" para os toasts de venda.

Decisao de produto (secao 3.1 do plano): carrinho com multiplos itens. Ficou fora do
escopo a operacao de cancelar venda e a integracao real com os cronometros de
relatorios (usam `VendaMock` ate existirem vendas reais em lote).

### Ajustes de UX apos primeira passada
- `src/br/com/distribuidora/view/NovaVendaView.java`
  - Tabela do carrinho ganha clip arredondado (raio 12) amarrado ao tamanho,
    eliminando qualquer pixel da linha que extravase as bordas do painel; a sombra
    propria da tabela e desligada (`.table-view.cart-table`) ja que o painel que a
    envolve ja projeta a sombra. Coluna de remocao flexivel (44 -> 40..64) para a
    politica CONSTRAINED nunca forcar overflow; botao "x" contido em 28x28 para
    caber na altura fixa de 44px da linha.
  - Desconto: novo botao "Aplicar desconto" ao lado do campo (e Enter no campo
    dispara o mesmo). Valida valor (numero, 0..100, dentro do limite configurado)
    marcando o campo com `input-error` quando invalido, recalcula o resumo na hora
    e mostra feedback verde "Desconto de X% aplicado ao total" (classe
    `field-success`, mesmo padrao da ConfiguracaoView). O resumo nao depende mais
    de finalizar a venda. `finalizarVenda` reutiliza o mesmo parse/validacao e
    remove o hack de `setStyle` inline. Tambem corrigido um NPE descoberto no
    smoke: a label local "Desconto (%)" sombreava o campo `labelDesconto` do
    resumo, que ficava `null` ao atualizar o resumo (renomeada para
    `rotuloDesconto`).
- `src/css/components.css`
  - Nova regra `.table-view.cart-table { -fx-effect: null; }` (sombra fica so no
    painel).

### Verificacao
- `mvn compile`: OK (apenas warnings do JDK 25).
- Smoke `javafx:run`: aplicacao iniciou e permaneceu ativa sem excecoes.

## [Em revisao] Armazenamento local offline (estoque.json + vendas.json) (2026-09-22)
- Decisao de produto (via questionario): substituir os mocks de estoque e vendas por
  persistencia local offline em JSON, seguindo o padrao do `ConfiguracaoStore`
  (`~/.bebmais/config.properties`). Os mocks de Financeiro e Compras permanecem
  (nao ha telas que originem esses dados).
- `pom.xml`
  - Nova dependencia `com.google.code.gson:gson:2.11.0`.
- `src/br/com/distribuidora/repository/Persistencia.java` (novo)
  - Gson com pretty-print e adapters de `LocalDate`/`LocalDateTime` (string ISO),
    ja que datas JSON nativas virariam objetos. `lerLista(Path, Type)` devolve
    `null` se o arquivo nao existir, estiver vazio/corrompido ou falhar a leitura
    (mensagem no stderr); `gravar(Path, Object)` imprime no stderr em falha de
    escrita. Aplicacao segue em memoria em qualquer cenario de excecao (graceful).
- `src/br/com/distribuidora/repository/EstoqueRepository.java`
  - Carrega `~/.bebmais/estoque.json` no construtor; primeira execucao gera os 9
    seeds e grava o arquivo. `adicionar`/`atualizar`/`remover` chamam `salvar()`.
  - Removido codigo morto `totalVendas = 25` (nao existe mais esse dado em estoque).
- `src/br/com/distribuidora/model/ItemVenda.java` e `model/Venda.java`
  - Convertidos para POJOs mutaveis (construtor sem argumentos + setters), mesmo
    padrao do `Bebida`, para o Gson desserializar (`LocalDateTime` de `dataHora`
    via adapter).
- `src/br/com/distribuidora/repository/VendaRepository.java`
  - Carrega `~/.bebmais/vendas.json`; `adicionar` chama `salvar()`.
- `src/br/com/distribuidora/controller/RelatorioController.java`
  - Vendas agora vem do `VendaRepository` (dados reais); `totalVendas`,
    `valorTotalVendas`, `produtosVendidos` e `ticketMedio` sao calculados das
    vendas persistidas. Classe `VendaMock` removida (8 registros de mentira) e
    campo correspondente eliminado. `FinanceiroMock`/`CompraMock` mantidos.
- `src/br/com/distribuidora/view/RelatorioView.java`
  - Aba Vendas migrada para `Venda` real: colunas Data (`Formatadores.dataHora`),
    Numero da venda ("V-nnn" via `Formatadores.codigo`), Qtd. itens (soma dos
    itens), Forma de pagamento, Valor total e Status; filtros derivam as opcoes
    dos dados reais (pagamento e status apenas). Filtros e colunas de Cliente/
    Vendedor removidos (venda e de balcao). Cards da pagina seguem usando
    `relatorio.*`.

### Verificacao
- `mvn compile`: OK (apenas warnings do JDK 25).
- Smoke `javafx:run`: aplicacao iniciou e permaneceu ativa sem excecoes; primeira
  execucao criou `~/.bebmais/estoque.json` com os 9 seeds.
- Primeiro smoke detectou e corrigiu um NPE de ordem de inicializacao estatica:
  `INSTANCIA` era criada antes de `ARQUIVO`/`TIPO_*` (null no construtor) em
  `EstoqueRepository` e `VendaRepository`; campos de parsing movidos para antes do
  singleton.