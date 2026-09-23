package br.com.distribuidora.view;

import br.com.distribuidora.model.Bebida;
import br.com.distribuidora.repository.ConfiguracaoStore;
import br.com.distribuidora.repository.EstoqueRepository;
import br.com.distribuidora.util.Formatadores;
import br.com.distribuidora.view.components.PageHeader;
import br.com.distribuidora.view.components.StatCard;
import br.com.distribuidora.view.components.StatusBadge;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class RelatorioView {

    private final EstoqueRepository estoque = EstoqueRepository.getInstance();
    private final ConfiguracaoStore config = ConfiguracaoStore.getInstance();

    private Label feedbackFiltros;
    private Label feedbackAcoes;

    // =========================
    // DADOS MOCKADOS
    // =========================

    private final List<VendaMock> vendasMock = new ArrayList<>(List.of(
            new VendaMock("02/01/2026", "V-1001", "Bar do Zé", "Carlos", 5, "Pix", "187,50", "Concluída"),
            new VendaMock("03/01/2026", "V-1002", "Mercado Central", "Ana", 12, "Cartão de crédito", "540,00", "Concluída"),
            new VendaMock("05/01/2026", "V-1003", "Churrascaria Gaúcha", "Carlos", 3, "Dinheiro", "89,90", "Pendente"),
            new VendaMock("08/01/2026", "V-1004", "Conveniência Estrela", "Ana", 8, "Pix", "320,40", "Concluída"),
            new VendaMock("10/01/2026", "V-1005", "Bar do Zé", "Paulo", 15, "Transferência", "950,00", "Cancelada"),
            new VendaMock("12/01/2026", "V-1006", "Restaurante Sabor", "Paulo", 20, "Cartão de débito", "1230,75", "Concluída"),
            new VendaMock("15/01/2026", "V-1007", "Mercado Central", "Ana", 6, "Pix", "275,30", "Pendente"),
            new VendaMock("18/01/2026", "V-1008", "Depósito do Nando", "Carlos", 30, "Boleto", "2800,00", "Concluída")
    ));

    private final List<FinanceiroMock> financeiroMock = new ArrayList<>(List.of(
            new FinanceiroMock("02/01/2026", "Venda à vista", "Receita", "Vendas", "2350,00"),
            new FinanceiroMock("03/01/2026", "Vendas cartão", "Receita", "Vendas", "4860,00"),
            new FinanceiroMock("04/01/2026", "Vendas pix", "Receita", "Vendas", "5270,00"),
            new FinanceiroMock("05/01/2026", "Compra de bebidas", "Despesa", "Compras", "3850,00"),
            new FinanceiroMock("06/01/2026", "Folha de pagamento", "Despesa", "Pessoal", "3120,00"),
            new FinanceiroMock("07/01/2026", "Energia e água", "Despesa", "Operacional", "890,00"),
            new FinanceiroMock("08/01/2026", "Combustível", "Despesa", "Logística", "850,00")
    ));

    private final List<CompraMock> comprasMock = new ArrayList<>(List.of(
            new CompraMock("02/01/2026", "C-2001", "Distribuidora Ambev", 12, "1850,00", "Concluída"),
            new CompraMock("05/01/2026", "C-2002", "Coca-Cola Femsa", 9, "2380,00", "Concluída"),
            new CompraMock("09/01/2026", "C-2003", "Casa Valduga", 4, "1420,00", "Pendente"),
            new CompraMock("12/01/2026", "C-2004", "Red Bull Brasil", 3, "990,00", "Concluída"),
            new CompraMock("16/01/2026", "C-2005", "Dell Vale", 6, "610,00", "Cancelada")
    ));

    public VBox getRoot() {
        PageHeader cabecalho = new PageHeader(
                "Relatórios",
                "Consulte e acompanhe os dados da distribuidora"
        );

        VBox cardFiltros = criarCardFiltros();

        HBox cardsResumo = new HBox(16,
                new StatCard("Total de Vendas", "1.247"),
                new StatCard("Valor Total Vendido", formatarValor(new BigDecimal("84532.90"))),
                new StatCard("Produtos Vendidos", "9.856"),
                new StatCard("Ticket Médio", formatarValor(new BigDecimal("67.80"))));
        cardsResumo.getChildren().forEach(no -> {
            HBox.setHgrow(no, Priority.ALWAYS);
            ((Region) no).setMaxWidth(Double.MAX_VALUE);
        });

        Label tituloRelatorios = new Label("Relatórios disponíveis");
        tituloRelatorios.getStyleClass().add("section-title");

        TabPane abas = new TabPane();
        abas.getStyleClass().add("tabs");

        Tab abaVendas = new Tab("Vendas", criarAbaVendas());
        Tab abaEstoque = new Tab("Estoque", criarAbaEstoque());
        Tab abaProdutos = new Tab("Produtos", criarAbaProdutos());
        Tab abaFinanceiro = new Tab("Financeiro", criarAbaFinanceiro());
        Tab abaCompras = new Tab("Compras", criarAbaCompras());

        abas.getTabs().addAll(abaVendas, abaEstoque, abaProdutos, abaFinanceiro, abaCompras);

        Label tituloGraficos = new Label("Gráficos");
        tituloGraficos.getStyleClass().add("section-title");

        VBox secaoGraficos = criarSecaoGraficos();

        VBox cardAcoes = criarCardAcoes();

        VBox raiz = new VBox(24,
                cabecalho,
                cardFiltros,
                cardsResumo,
                tituloRelatorios,
                abas,
                tituloGraficos,
                secaoGraficos,
                cardAcoes);
        raiz.setMaxWidth(Double.MAX_VALUE);
        return raiz;
    }

    // =========================
    // CARD DE FILTRO POR PERÍODO
    // =========================

    private VBox criarCardFiltros() {
        VBox card = new VBox(16);
        card.getStyleClass().add("panel");

        Label titulo = new Label("Período do relatório");
        titulo.getStyleClass().add("section-title");

        DatePicker dataInicial = new DatePicker();
        dataInicial.setPrefWidth(180);

        DatePicker dataFinal = new DatePicker();
        dataFinal.setPrefWidth(180);

        Button filtrar = new Button("Filtrar");
        filtrar.getStyleClass().add("btn-primary");

        Button limpar = new Button("Limpar filtros");
        limpar.getStyleClass().add("btn-secondary");

        feedbackFiltros = new Label();
        feedbackFiltros.getStyleClass().add("field-help");
        feedbackFiltros.setVisible(false);

        HBox linha = new HBox(16,
                criarGrupoCampo("Data inicial", dataInicial),
                criarGrupoCampo("Data final", dataFinal),
                filtrar,
                limpar);
        linha.setAlignment(Pos.CENTER_LEFT);

        filtrar.setOnAction(event -> {
            feedbackFiltros.setText("Relatório gerado para o período selecionado.");
            feedbackFiltros.setVisible(true);
        });

        limpar.setOnAction(event -> {
            dataInicial.setValue(null);
            dataFinal.setValue(null);
            feedbackFiltros.setText("Filtros de período limpos.");
            feedbackFiltros.setVisible(true);
        });

        card.getChildren().addAll(titulo, linha, feedbackFiltros);
        return card;
    }

    private VBox criarCardAcoes() {
        VBox card = new VBox(16);
        card.getStyleClass().add("panel");

        Label titulo = new Label("Ações");
        titulo.getStyleClass().add("section-title");

        HBox linha = new HBox(12,
                criarBotaoAcao("Visualizar", "btn-primary"),
                criarBotaoAcao("Imprimir", "btn-secondary"),
                criarBotaoAcao("Exportar PDF", "btn-secondary"),
                criarBotaoAcao("Exportar Excel", "btn-secondary"));

        feedbackAcoes = new Label();
        feedbackAcoes.getStyleClass().add("field-help");
        feedbackAcoes.setVisible(false);

        card.getChildren().addAll(titulo, linha, feedbackAcoes);
        return card;
    }

    // =========================
    // ABA: VENDAS
    // =========================

    private Node criarAbaVendas() {
        VBox conteudo = new VBox(18);

        ObservableList<VendaMock> dados = FXCollections.observableArrayList(vendasMock);

        ComboBox<String> cliente = new ComboBox<>();
        cliente.getItems().addAll("Todos", "Bar do Zé", "Mercado Central",
                "Churrascaria Gaúcha", "Conveniência Estrela", "Restaurante Sabor", "Depósito do Nando");
        cliente.setValue("Todos");
        cliente.setPrefWidth(170);

        ComboBox<String> vendedor = new ComboBox<>();
        vendedor.getItems().addAll("Todos", "Carlos", "Ana", "Paulo");
        vendedor.setValue("Todos");
        vendedor.setPrefWidth(140);

        ComboBox<String> pagamento = new ComboBox<>();
        pagamento.getItems().addAll("Todos", "Pix", "Cartão de crédito", "Cartão de débito",
                "Dinheiro", "Transferência", "Boleto");
        pagamento.setValue("Todos");
        pagamento.setPrefWidth(170);

        ComboBox<String> status = new ComboBox<>();
        status.getItems().addAll("Todos", "Concluída", "Pendente", "Cancelada");
        status.setValue("Todos");
        status.setPrefWidth(140);

        HBox filtros = new HBox(14,
                criarGrupoCampo("Cliente", cliente),
                criarGrupoCampo("Vendedor", vendedor),
                criarGrupoCampo("Forma de pagamento", pagamento),
                criarGrupoCampo("Status", status));
        filtros.setAlignment(Pos.CENTER_LEFT);

        FilteredList<VendaMock> filtrada = new FilteredList<>(dados);
        filtrada.setPredicate(venda -> filtrarVenda(venda, cliente.getValue(),
                vendedor.getValue(), pagamento.getValue(), status.getValue()));

        cliente.valueProperty().addListener((obs, o, n) -> filtrada.setPredicate(
                venda -> filtrarVenda(venda, n, vendedor.getValue(),
                        pagamento.getValue(), status.getValue())));
        vendedor.valueProperty().addListener((obs, o, n) -> filtrada.setPredicate(
                venda -> filtrarVenda(venda, cliente.getValue(), n,
                        pagamento.getValue(), status.getValue())));
        pagamento.valueProperty().addListener((obs, o, n) -> filtrada.setPredicate(
                venda -> filtrarVenda(venda, cliente.getValue(), vendedor.getValue(),
                        n, status.getValue())));
        status.valueProperty().addListener((obs, o, n) -> filtrada.setPredicate(
                venda -> filtrarVenda(venda, cliente.getValue(), vendedor.getValue(),
                        pagamento.getValue(), n)));

        TableView<VendaMock> tabela = new TableView<>(filtrada);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setFixedCellSize(44);
        tabela.setMaxHeight(300);

        TableColumn<VendaMock, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));

        TableColumn<VendaMock, String> colNumero = new TableColumn<>("Número da venda");
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));

        TableColumn<VendaMock, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));

        TableColumn<VendaMock, String> colVendedor = new TableColumn<>("Vendedor");
        colVendedor.setCellValueFactory(new PropertyValueFactory<>("vendedor"));

        TableColumn<VendaMock, Integer> colItens = new TableColumn<>("Qtd. itens");
        colItens.setCellValueFactory(new PropertyValueFactory<>("quantidadeItens"));

        TableColumn<VendaMock, String> colPagamento = new TableColumn<>("Forma de pagamento");
        colPagamento.setCellValueFactory(new PropertyValueFactory<>("pagamento"));

        TableColumn<VendaMock, BigDecimal> colValor = new TableColumn<>("Valor total");
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colValor.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal valor, boolean vazio) {
                setText(vazio || valor == null ? "" : formatarValor(valor));
            }
        });

        TableColumn<VendaMock, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> criarCelulaStatus());

        tabela.getColumns().addAll(colData, colNumero, colCliente, colVendedor,
                colItens, colPagamento, colValor, colStatus);

        conteudo.getChildren().addAll(filtros, tabela);
        return conteudo;
    }

    private boolean filtrarVenda(VendaMock venda, String cliente, String vendedor,
                                 String pagamento, String status) {
        boolean okCliente = "Todos".equals(cliente) || cliente == null || cliente.equals(venda.getCliente());
        boolean okVendedor = "Todos".equals(vendedor) || vendedor == null || vendedor.equals(venda.getVendedor());
        boolean okPagamento = "Todos".equals(pagamento) || pagamento == null || pagamento.equals(venda.getPagamento());
        boolean okStatus = "Todos".equals(status) || status == null || status.equals(venda.getStatus());
        return okCliente && okVendedor && okPagamento && okStatus;
    }

    // =========================
    // ABA: ESTOQUE
    // =========================

    private Node criarAbaEstoque() {
        VBox conteudo = new VBox(18);

        HBox legenda = new HBox(18,
                criarItemLegenda("dot-success", "Estoque normal"),
                criarItemLegenda("dot-warning", "Estoque baixo"),
                criarItemLegenda("dot-danger", "Sem estoque"));
        legenda.setAlignment(Pos.CENTER_LEFT);

        List<Bebida> bebidas = estoque.listar();
        int limite = config.getLimiteEstoqueBaixo();

        List<EstoqueMock> registros = new ArrayList<>();
        for (Bebida b : bebidas) {
            registros.add(new EstoqueMock(
                    String.valueOf(b.getId() + 1),
                    b.getNome(),
                    b.getCategoria(),
                    b.getEstoque(),
                    limite,
                    b.getPreco(),
                    statusEstoque(b.getEstoque())
            ));
        }

        TableView<EstoqueMock> tabela = new TableView<>(
                FXCollections.observableArrayList(registros)
        );
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setFixedCellSize(44);
        tabela.setMaxHeight(300);

        TableColumn<EstoqueMock, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        TableColumn<EstoqueMock, String> colProduto = new TableColumn<>("Produto");
        colProduto.setCellValueFactory(new PropertyValueFactory<>("produto"));

        TableColumn<EstoqueMock, String> colCategoria = new TableColumn<>("Categoria");
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        TableColumn<EstoqueMock, Integer> colAtual = new TableColumn<>("Estoque atual");
        colAtual.setCellValueFactory(new PropertyValueFactory<>("estoqueAtual"));

        TableColumn<EstoqueMock, Integer> colMinimo = new TableColumn<>("Estoque mínimo");
        colMinimo.setCellValueFactory(new PropertyValueFactory<>("estoqueMinimo"));

        TableColumn<EstoqueMock, BigDecimal> colPreco = new TableColumn<>("Preço");
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colPreco.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal preco, boolean vazio) {
                setText(vazio || preco == null ? "" : formatarValor(preco));
            }
        });

        TableColumn<EstoqueMock, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> criarCelulaStatus());

        tabela.getColumns().addAll(colCodigo, colProduto, colCategoria,
                colAtual, colMinimo, colPreco, colStatus);

        conteudo.getChildren().addAll(legenda, tabela);
        return conteudo;
    }

    private String statusEstoque(int quantidade) {
        if (quantidade <= 0) {
            return "Sem estoque";
        }
        return quantidade <= config.getLimiteEstoqueBaixo() ? "Estoque baixo" : "Normal";
    }

    // =========================
    // ABA: PRODUTOS
    // =========================

    private Node criarAbaProdutos() {
        VBox conteudo = new VBox(18);

        List<Bebida> bebidas = estoque.listar();

        List<ProdutoMock> registros = new ArrayList<>();
        for (Bebida b : bebidas) {
            registros.add(new ProdutoMock(
                    String.valueOf(b.getId() + 1),
                    b.getNome(),
                    b.getCategoria(),
                    b.getMarca(),
                    b.getPreco().multiply(new BigDecimal("0.60")),
                    b.getPreco(),
                    b.getEstoque(),
                    statusEstoque(b.getEstoque())
            ));
        }

        TableView<ProdutoMock> tabela = new TableView<>(
                FXCollections.observableArrayList(registros)
        );
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setFixedCellSize(44);
        tabela.setMaxHeight(300);

        TableColumn<ProdutoMock, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        TableColumn<ProdutoMock, String> colProduto = new TableColumn<>("Produto");
        colProduto.setCellValueFactory(new PropertyValueFactory<>("produto"));

        TableColumn<ProdutoMock, String> colCategoria = new TableColumn<>("Categoria");
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        TableColumn<ProdutoMock, String> colMarca = new TableColumn<>("Marca");
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));

        TableColumn<ProdutoMock, BigDecimal> colCompra = new TableColumn<>("Preço de compra");
        colCompra.setCellValueFactory(new PropertyValueFactory<>("precoCompra"));
        colCompra.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal preco, boolean vazio) {
                setText(vazio || preco == null ? "" : formatarValor(preco));
            }
        });

        TableColumn<ProdutoMock, BigDecimal> colVenda = new TableColumn<>("Preço de venda");
        colVenda.setCellValueFactory(new PropertyValueFactory<>("precoVenda"));
        colVenda.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal preco, boolean vazio) {
                setText(vazio || preco == null ? "" : formatarValor(preco));
            }
        });

        TableColumn<ProdutoMock, Integer> colEstoque = new TableColumn<>("Estoque");
        colEstoque.setCellValueFactory(new PropertyValueFactory<>("estoque"));

        TableColumn<ProdutoMock, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> criarCelulaStatus());

        tabela.getColumns().addAll(colCodigo, colProduto, colCategoria, colMarca,
                colCompra, colVenda, colEstoque, colStatus);

        conteudo.getChildren().add(tabela);
        return conteudo;
    }

    // =========================
    // ABA: FINANCEIRO
    // =========================

    private Node criarAbaFinanceiro() {
        VBox conteudo = new VBox(18);

        BigDecimal receitas = BigDecimal.ZERO;
        BigDecimal despesas = BigDecimal.ZERO;
        for (FinanceiroMock f : financeiroMock) {
            if ("Receita".equals(f.getTipo())) {
                receitas = receitas.add(f.getValor());
            } else {
                despesas = despesas.add(f.getValor());
            }
        }
        BigDecimal saldo = receitas.subtract(despesas);

        HBox cards = new HBox(16,
                new StatCard("Receitas", formatarValor(receitas)),
                new StatCard("Despesas", formatarValor(despesas), true),
                new StatCard("Saldo", formatarValor(saldo)));
        cards.getChildren().forEach(no -> {
            HBox.setHgrow(no, Priority.ALWAYS);
            ((Region) no).setMaxWidth(Double.MAX_VALUE);
        });

        TableView<FinanceiroMock> tabela = new TableView<>(
                FXCollections.observableArrayList(financeiroMock)
        );
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setFixedCellSize(44);
        tabela.setMaxHeight(300);

        TableColumn<FinanceiroMock, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));

        TableColumn<FinanceiroMock, String> colDescricao = new TableColumn<>("Descrição");
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        TableColumn<FinanceiroMock, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTipo.setCellFactory(col -> criarCelulaStatus());

        TableColumn<FinanceiroMock, String> colCategoria = new TableColumn<>("Categoria");
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        TableColumn<FinanceiroMock, BigDecimal> colValor = new TableColumn<>("Valor");
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colValor.setCellFactory(col -> criarCelulaValorFinanceiro());

        tabela.getColumns().addAll(colData, colDescricao, colTipo, colCategoria, colValor);

        conteudo.getChildren().addAll(cards, tabela);
        return conteudo;
    }

    // =========================
    // ABA: COMPRAS
    // =========================

    private Node criarAbaCompras() {
        VBox conteudo = new VBox(18);

        TableView<CompraMock> tabela = new TableView<>(
                FXCollections.observableArrayList(comprasMock)
        );
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setFixedCellSize(44);
        tabela.setMaxHeight(300);

        TableColumn<CompraMock, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));

        TableColumn<CompraMock, String> colNumero = new TableColumn<>("Número da compra");
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));

        TableColumn<CompraMock, String> colFornecedor = new TableColumn<>("Fornecedor");
        colFornecedor.setCellValueFactory(new PropertyValueFactory<>("fornecedor"));

        TableColumn<CompraMock, Integer> colItens = new TableColumn<>("Qtd. produtos");
        colItens.setCellValueFactory(new PropertyValueFactory<>("quantidadeProdutos"));

        TableColumn<CompraMock, BigDecimal> colValor = new TableColumn<>("Valor total");
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colValor.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal valor, boolean vazio) {
                setText(vazio || valor == null ? "" : formatarValor(valor));
            }
        });

        TableColumn<CompraMock, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(col -> criarCelulaStatus());

        tabela.getColumns().addAll(colData, colNumero, colFornecedor, colItens,
                colValor, colStatus);

        conteudo.getChildren().add(tabela);
        return conteudo;
    }

    // =========================
    // GRÁFICOS
    // =========================

    private VBox criarSecaoGraficos() {
        HBox linha1 = new HBox(18,
                criarCardGrafico("Vendas por período", criarGraficoLinhaVendas()),
                criarCardGrafico("Produtos mais vendidos", criarGraficoBarras()));

        HBox linha2 = new HBox(18,
                criarCardGrafico("Entradas e saídas", criarGraficoEntradasSaidas()),
                criarCardGrafico("Estoque por categoria", criarGraficoPizza()));

        linha1.setFillHeight(true);
        linha2.setFillHeight(true);

        return new VBox(18, linha1, linha2);
    }

    private LineChart<String, Number> criarGraficoLinhaVendas() {
        CategoryAxis eixoX = new CategoryAxis();
        NumberAxis eixoY = new NumberAxis();
        eixoY.setForceZeroInRange(true);

        LineChart<String, Number> grafico = new LineChart<>(eixoX, eixoY);
        grafico.setCreateSymbols(true);

        String[] meses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
                "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        int[] valores = {620, 710, 540, 830, 920, 610, 780, 890, 650, 1020, 980, 1150};

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Vendas");

        for (int i = 0; i < meses.length; i++) {
            serie.getData().add(new XYChart.Data<>(meses[i], valores[i]));
        }

        grafico.getData().add(serie);
        grafico.setPrefHeight(230);
        VBox.setVgrow(grafico, Priority.ALWAYS);
        return grafico;
    }

    private BarChart<String, Number> criarGraficoBarras() {
        CategoryAxis eixoX = new CategoryAxis();
        NumberAxis eixoY = new NumberAxis();
        eixoY.setForceZeroInRange(true);

        BarChart<String, Number> grafico = new BarChart<>(eixoX, eixoY);
        grafico.setBarGap(6);

        String[] produtos = {"Coca-Cola", "Água 500ml", "Brahma", "Suco Laranja", "Red Bull", "Vinho"};
        int[] valores = {450, 320, 260, 210, 150, 95};

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Unidades vendidas");

        for (int i = 0; i < produtos.length; i++) {
            serie.getData().add(new XYChart.Data<>(produtos[i], valores[i]));
        }

        grafico.getData().add(serie);
        grafico.setPrefHeight(230);
        VBox.setVgrow(grafico, Priority.ALWAYS);
        return grafico;
    }

    private StackedBarChart<String, Number> criarGraficoEntradasSaidas() {
        CategoryAxis eixoX = new CategoryAxis();
        NumberAxis eixoY = new NumberAxis();
        eixoY.setForceZeroInRange(true);

        StackedBarChart<String, Number> grafico = new StackedBarChart<>(eixoX, eixoY);

        String[] meses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun"};
        int[] entradas = {680, 780, 620, 900, 990, 700};
        int[] saidas = {350, 410, 300, 470, 520, 380};

        XYChart.Series<String, Number> serieEntrada = new XYChart.Series<>();
        serieEntrada.setName("Entradas");

        XYChart.Series<String, Number> serieSaida = new XYChart.Series<>();
        serieSaida.setName("Saídas");

        for (int i = 0; i < meses.length; i++) {
            serieEntrada.getData().add(new XYChart.Data<>(meses[i], entradas[i]));
            serieSaida.getData().add(new XYChart.Data<>(meses[i], saidas[i]));
        }

        grafico.getData().addAll(serieEntrada, serieSaida);
        grafico.setPrefHeight(230);
        VBox.setVgrow(grafico, Priority.ALWAYS);
        return grafico;
    }

    private PieChart criarGraficoPizza() {
        ObservableList<PieChart.Data> dados = FXCollections.observableArrayList(
                new PieChart.Data("Refrigerante", 105),
                new PieChart.Data("Cerveja", 240),
                new PieChart.Data("Água", 60),
                new PieChart.Data("Vinho", 12),
                new PieChart.Data("Destilado", 15),
                new PieChart.Data("Suco", 5),
                new PieChart.Data("Energético", 2)
        );

        PieChart grafico = new PieChart(dados);
        grafico.setLabelsVisible(true);
        grafico.setPrefHeight(230);
        VBox.setVgrow(grafico, Priority.ALWAYS);
        return grafico;
    }

    private VBox criarCardGrafico(String titulo, Node grafico) {
        VBox card = new VBox(12);
        card.getStyleClass().add("panel");
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setMaxWidth(Double.MAX_VALUE);

        Label label = new Label(titulo);
        label.getStyleClass().add("section-title");

        card.getChildren().addAll(label, grafico);
        return card;
    }

    // =========================
    // CÉLULAS DE STATUS E VALOR
    // =========================

    private <T> TableCell<T, String> criarCelulaStatus() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean vazio) {
                super.updateItem(status, vazio);
                if (vazio || status == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(new StatusBadge(status, nivelDe(status)));
                    setText(null);
                }
            }
        };
    }

    private StatusBadge.Nivel nivelDe(String status) {
        return switch (status) {
            case "Concluída", "Receita", "Normal" -> StatusBadge.Nivel.OK;
            case "Pendente", "Baixo", "Estoque baixo" -> StatusBadge.Nivel.ATENCAO;
            case "Cancelada", "Despesa", "Sem estoque" -> StatusBadge.Nivel.CRITICO;
            default -> StatusBadge.Nivel.INFO;
        };
    }

    private TableCell<FinanceiroMock, BigDecimal> criarCelulaValorFinanceiro() {
        return new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal valor, boolean vazio) {
                super.updateItem(valor, vazio);
                if (vazio || valor == null) {
                    setText(null);
                    setStyle("");
                } else {
                    FinanceiroMock item = getTableRow() == null ? null : getTableRow().getItem();
                    boolean receita = item != null && "Receita".equals(item.getTipo());
                    setText((receita ? "+ " : "- ") + formatarValor(valor.abs()));
                    setStyle(receita ? "-fx-text-fill: -bm-success-text; -fx-font-weight: bold;"
                            : "-fx-text-fill: -bm-danger-text; -fx-font-weight: bold;");
                }
            }
        };
    }

    // =========================
    // HELPERES
    // =========================

    private Button criarBotaoAcao(String texto, String classe) {
        Button botao = new Button(texto);
        botao.getStyleClass().add(classe);
        botao.setOnAction(event -> {
            feedbackAcoes.setText("Ação \"" + texto + "\" ficará disponível em breve.");
            feedbackAcoes.setVisible(true);
        });
        return botao;
    }

    private HBox criarItemLegenda(String classeDot, String texto) {
        Region dot = new Region();
        dot.getStyleClass().addAll("dot", classeDot);

        Label label = new Label(texto);
        label.getStyleClass().add("text-muted");

        HBox item = new HBox(8, dot, label);
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
    }

    private VBox criarGrupoCampo(String texto, Node campo) {
        Label label = new Label(texto);
        label.getStyleClass().add("field-label");

        VBox grupo = new VBox(6, label, campo);
        return grupo;
    }

    private String formatarValor(BigDecimal valor) {
        return Formatadores.moeda(config.getMoeda(), valor);
    }

    // =========================
    // MODELOS MOCKADOS
    // =========================

    public static class VendaMock {
        private final String data;
        private final String numero;
        private final String cliente;
        private final String vendedor;
        private final int quantidadeItens;
        private final String pagamento;
        private final BigDecimal valor;
        private final String status;

        public VendaMock(String data, String numero, String cliente, String vendedor,
                         int quantidadeItens, String pagamento, String valor, String status) {
            this.data = data;
            this.numero = numero;
            this.cliente = cliente;
            this.vendedor = vendedor;
            this.quantidadeItens = quantidadeItens;
            this.pagamento = pagamento;
            this.valor = toBigDecimal(valor);
            this.status = status;
        }

        public String getData() {
            return data;
        }

        public String getNumero() {
            return numero;
        }

        public String getCliente() {
            return cliente;
        }

        public String getVendedor() {
            return vendedor;
        }

        public int getQuantidadeItens() {
            return quantidadeItens;
        }

        public String getPagamento() {
            return pagamento;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class EstoqueMock {
        private final String codigo;
        private final String produto;
        private final String categoria;
        private final int estoqueAtual;
        private final int estoqueMinimo;
        private final BigDecimal preco;
        private final String status;

        public EstoqueMock(String codigo, String produto, String categoria,
                           int estoqueAtual, int estoqueMinimo, BigDecimal preco, String status) {
            this.codigo = codigo;
            this.produto = produto;
            this.categoria = categoria;
            this.estoqueAtual = estoqueAtual;
            this.estoqueMinimo = estoqueMinimo;
            this.preco = preco;
            this.status = status;
        }

        public String getCodigo() {
            return codigo;
        }

        public String getProduto() {
            return produto;
        }

        public String getCategoria() {
            return categoria;
        }

        public int getEstoqueAtual() {
            return estoqueAtual;
        }

        public int getEstoqueMinimo() {
            return estoqueMinimo;
        }

        public BigDecimal getPreco() {
            return preco;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class ProdutoMock {
        private final String codigo;
        private final String produto;
        private final String categoria;
        private final String marca;
        private final BigDecimal precoCompra;
        private final BigDecimal precoVenda;
        private final int estoque;
        private final String status;

        public ProdutoMock(String codigo, String produto, String categoria, String marca,
                           BigDecimal precoCompra, BigDecimal precoVenda, int estoque, String status) {
            this.codigo = codigo;
            this.produto = produto;
            this.categoria = categoria;
            this.marca = marca;
            this.precoCompra = precoCompra;
            this.precoVenda = precoVenda;
            this.estoque = estoque;
            this.status = status;
        }

        public String getCodigo() {
            return codigo;
        }

        public String getProduto() {
            return produto;
        }

        public String getCategoria() {
            return categoria;
        }

        public String getMarca() {
            return marca;
        }

        public BigDecimal getPrecoCompra() {
            return precoCompra;
        }

        public BigDecimal getPrecoVenda() {
            return precoVenda;
        }

        public int getEstoque() {
            return estoque;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class FinanceiroMock {
        private final String data;
        private final String descricao;
        private final String tipo;
        private final String categoria;
        private final BigDecimal valor;

        public FinanceiroMock(String data, String descricao, String tipo,
                              String categoria, String valor) {
            this.data = data;
            this.descricao = descricao;
            this.tipo = tipo;
            this.categoria = categoria;
            this.valor = toBigDecimal(valor);
        }

        public String getData() {
            return data;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getTipo() {
            return tipo;
        }

        public String getCategoria() {
            return categoria;
        }

        public BigDecimal getValor() {
            return valor;
        }
    }

    public static class CompraMock {
        private final String data;
        private final String numero;
        private final String fornecedor;
        private final int quantidadeProdutos;
        private final BigDecimal valor;
        private final String status;

        public CompraMock(String data, String numero, String fornecedor,
                          int quantidadeProdutos, String valor, String status) {
            this.data = data;
            this.numero = numero;
            this.fornecedor = fornecedor;
            this.quantidadeProdutos = quantidadeProdutos;
            this.valor = toBigDecimal(valor);
            this.status = status;
        }

        public String getData() {
            return data;
        }

        public String getNumero() {
            return numero;
        }

        public String getFornecedor() {
            return fornecedor;
        }

        public int getQuantidadeProdutos() {
            return quantidadeProdutos;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public String getStatus() {
            return status;
        }
    }

    private static BigDecimal toBigDecimal(String valor) {
        return new BigDecimal(valor.replace(",", "."));
    }
}