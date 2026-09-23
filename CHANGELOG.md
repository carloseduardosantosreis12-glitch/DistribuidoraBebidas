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