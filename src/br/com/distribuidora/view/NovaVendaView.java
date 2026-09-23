package br.com.distribuidora.view;

import br.com.distribuidora.controller.ConfiguracaoController;
import br.com.distribuidora.controller.EstoqueController;
import br.com.distribuidora.controller.VendaController;
import br.com.distribuidora.model.Bebida;
import br.com.distribuidora.model.ItemVenda;
import br.com.distribuidora.model.Venda;
import br.com.distribuidora.util.Formatadores;
import br.com.distribuidora.view.components.PageHeader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class NovaVendaView {

    private final VendaController controle = VendaController.getInstance();
    private final String simbolo = ConfiguracaoController.getInstance().moeda();

    private final Consumer<String> notificar;

    private final ObservableList<ItemVenda> itens = FXCollections.observableArrayList();

    private ComboBox<Bebida> comboBebida;
    private TextField campoQuantidade;
    private ComboBox<String> comboPagamento;
    private TextField campoDesconto;
    private Label labelSubtotal;
    private Label labelDesconto;
    private Label labelTotal;
    private Label feedback;
    private Button btnFinalizar;

    public NovaVendaView(Consumer<String> notificar) {
        this.notificar = notificar;
    }

    public VBox getRoot() {
        PageHeader cabecalho = new PageHeader(
                "Nova Venda",
                "Registre uma venda no balcão com baixa automática no estoque.");

        VBox painelEsquerda = painelItens();
        VBox painelDireita = painelPagamento();

        HBox colunas = new HBox(20, painelEsquerda, painelDireita);
        colunas.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(painelEsquerda, Priority.ALWAYS);

        VBox raiz = new VBox(20, cabecalho, colunas);
        raiz.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(painelEsquerda, Priority.ALWAYS);
        raiz.setPrefHeight(680);
        return raiz;
    }

    private VBox painelItens() {
        Label titulo = new Label("Itens da venda");
        titulo.getStyleClass().add("section-title");

        comboBebida = new ComboBox<>();
        comboBebida.setMaxWidth(Double.MAX_VALUE);
        comboBebida.setCellFactory(lista -> celulaBebida());
        comboBebida.setButtonCell(celulaBebida());
        comboBebida.getItems().addAll(controleContaBebidas());

        campoQuantidade = new TextField("1");
        campoQuantidade.setPrefWidth(90);
        campoQuantidade.setTextFormatter(formatterQuantidade());

        Button adicionar = new Button("Adicionar");
        adicionar.getStyleClass().add("btn-primary");
        FontIcon iconeMais = new FontIcon(FontAwesomeSolid.PLUS);
        iconeMais.setIconSize(14);
        adicionar.setGraphic(iconeMais);
        adicionar.setOnAction(event -> adicionarItem());

        HBox linhaAdicionar = new HBox(10, comboBebida, campoQuantidade, adicionar);
        linhaAdicionar.setAlignment(Pos.CENTER_LEFT);

        TableView<ItemVenda> tabela = montarTabela();

        VBox painel = new VBox(16, titulo, linhaAdicionar, tabela);
        painel.getStyleClass().add("panel");
        painel.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(tabela, Priority.ALWAYS);
        return painel;
    }

    private List<Bebida> controleContaBebidas() {
        return EstoqueController.getInstance().listarBebidas();
    }

    private VBox painelPagamento() {
        Label titulo = new Label("Pagamento e resumo");
        titulo.getStyleClass().add("section-title");

        comboPagamento = new ComboBox<>();
        comboPagamento.setMaxWidth(Double.MAX_VALUE);
        comboPagamento.getItems().addAll(controle.formasPagamentoDisponiveis());
        if (!comboPagamento.getItems().isEmpty()) {
            comboPagamento.setValue(comboPagamento.getItems().get(0));
        }

        VBox grupoPagamento = new VBox(6);
        Label labelPagamento = new Label("Forma de pagamento");
        labelPagamento.getStyleClass().add("field-label");
        grupoPagamento.getChildren().addAll(labelPagamento, comboPagamento);

        VBox areaDesconto = new VBox(6);
        Label rotuloDesconto = new Label("Desconto (%)");
        rotuloDesconto.getStyleClass().add("field-label");

        campoDesconto = new TextField("0");
        campoDesconto.setPrefWidth(110);

        Button aplicarDesconto = new Button("Aplicar desconto");
        aplicarDesconto.getStyleClass().add("btn-secondary");
        aplicarDesconto.setOnAction(event -> aplicarDesconto());
        campoDesconto.setOnAction(event -> aplicarDesconto());

        HBox campoAplicar = new HBox(8, campoDesconto, aplicarDesconto);
        campoAplicar.setAlignment(Pos.CENTER_LEFT);

        Label ajudaLimite = new Label();
        ajudaLimite.getStyleClass().add("field-help");

        areaDesconto.getChildren().addAll(rotuloDesconto, campoAplicar, ajudaLimite);
        HBox linhaDesconto = new HBox(6, areaDesconto);

        if (controle.permitirDesconto()) {
            ajudaLimite.setText("Desconto máximo permitido: "
                    + exibir(controle.limiteDescontoPercentual()) + "%.");
        } else {
            areaDesconto.setVisible(false);
            areaDesconto.setManaged(false);
        }

        labelSubtotal = valorResumo();
        labelDesconto = valorResumo();
        labelTotal = valorResumo();
        labelTotal.getStyleClass().add("row-valor");

        VBox resumo = new VBox(6);
        resumo.getChildren().addAll(
                linhaResumo("Subtotal", labelSubtotal),
                linhaResumo("Desconto", labelDesconto),
                linhaResumo("Total", labelTotal)
        );
        resumo.getStyleClass().add("panel");

        feedback = new Label();
        feedback.getStyleClass().add("field-error");
        feedback.setWrapText(true);
        feedback.setVisible(false);

        Button cancelar = new Button("Cancelar");
        cancelar.getStyleClass().add("btn-secondary");
        cancelar.setOnAction(event -> cancelarVenda());

        btnFinalizar = new Button("Finalizar venda");
        btnFinalizar.getStyleClass().add("btn-primary");
        btnFinalizar.disableProperty().bind(Bindings.isEmpty(itens));
        btnFinalizar.setOnAction(event -> finalizarVenda());

        HBox botoes = new HBox(12, cancelar, btnFinalizar);
        botoes.setAlignment(Pos.CENTER_RIGHT);

        VBox painel = new VBox(16,
                titulo,
                grupoPagamento,
                linhaDesconto,
                resumo,
                feedback,
                botoes);
        painel.getStyleClass().add("panel");
        painel.setPrefWidth(320);
        return painel;
    }

    private Label valorResumo() {
        Label valor = new Label();
        valor.getStyleClass().add("row-title");
        return valor;
    }

    private HBox linhaResumo(String rotulo, Label valor) {
        HBox linha = new HBox(12);
        linha.setAlignment(Pos.CENTER_LEFT);
        linha.setMaxWidth(Double.MAX_VALUE);

        Label titulo = new Label(rotulo);
        titulo.getStyleClass().add("text-muted");

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);
        linha.getChildren().addAll(titulo, espaco, valor);
        return linha;
    }

    private TableView<ItemVenda> montarTabela() {
        TableView<ItemVenda> tabela = new TableView<>(itens);
        tabela.getStyleClass().add("cart-table");
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabela.setFixedCellSize(44);

        Rectangle recorte = new Rectangle();
        recorte.widthProperty().bind(tabela.widthProperty());
        recorte.heightProperty().bind(tabela.heightProperty());
        recorte.setArcWidth(24);
        recorte.setArcHeight(24);
        tabela.setClip(recorte);

        TableColumn<ItemVenda, String> colNome = new TableColumn<>("Bebida");
        colNome.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getNomeBebida()));
        colNome.setPrefWidth(220);

        TableColumn<ItemVenda, Integer> colQtd = new TableColumn<>("Qtd");
        colQtd.getStyleClass().add("numeric-column");
        colQtd.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getQuantidade()));
        colQtd.setMinWidth(60);

        TableColumn<ItemVenda, BigDecimal> colPreco = new TableColumn<>("Preço");
        colPreco.getStyleClass().add("numeric-column");
        colPreco.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getPrecoUnitario()));
        colPreco.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal valor, boolean vazio) {
                setText(vazio || valor == null ? "" : Formatadores.moeda(simbolo, valor));
            }
        });
        colPreco.setMinWidth(90);

        TableColumn<ItemVenda, BigDecimal> colSub = new TableColumn<>("Subtotal");
        colSub.getStyleClass().add("numeric-column");
        colSub.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().subtotal()));
        colSub.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal valor, boolean vazio) {
                setText(vazio || valor == null ? "" : Formatadores.moeda(simbolo, valor));
            }
        });
        colSub.setMinWidth(90);

        TableColumn<ItemVenda, Void> colAcoes = new TableColumn<>("");
        colAcoes.setMinWidth(40);
        colAcoes.setMaxWidth(64);
        colAcoes.setCellFactory(col -> celulaRemover());

        tabela.getColumns().addAll(colNome, colQtd, colPreco, colSub, colAcoes);
        tabela.setPlaceholder(new Label("Nenhum item no carrinho."));
        return tabela;
    }

    private TableCell<ItemVenda, Void> celulaRemover() {
        return new TableCell<>() {
            private final Button botao = new Button();

            {
                botao.getStyleClass().add("btn-danger-icon");
                botao.setMinSize(28, 28);
                botao.setMaxSize(28, 28);
                botao.setPadding(new Insets(0));
                FontIcon icone = new FontIcon(FontAwesomeSolid.TIMES);
                icone.setIconSize(12);
                botao.setGraphic(icone);
                botao.setTooltip(new Tooltip("Remover item"));
                botao.setOnAction(event -> {
                    ItemVenda item = getTableView().getItems().get(getIndex());
                    itens.remove(item);
                    atualizarResumo();
                });
            }

            @Override
            protected void updateItem(Void item, boolean vazio) {
                setGraphic(vazio ? null : botao);
                setAlignment(Pos.CENTER);
            }
        };
    }

    private ListCell<Bebida> celulaBebida() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Bebida b, boolean vazio) {
                super.updateItem(b, vazio);
                setText(vazio || b == null ? null : rotuloBebida(b));
            }
        };
    }

    private String rotuloBebida(Bebida b) {
        return b.getNome() + " (" + b.getMarca() + ") — Estoque: " + b.getEstoque();
    }

    private TextFormatter<String> formatterQuantidade() {
        UnaryOperator<TextFormatter.Change> filtro = change -> {
            String novo = change.getControlNewText();
            if (novo.isEmpty()) {
                return change;
            }
            return novo.matches("\\d{1,4}") ? change : null;
        };
        return new TextFormatter<>(filtro);
    }

    private void adicionarItem() {
        feedback.setVisible(false);

        Bebida bebida = comboBebida.getValue();
        if (bebida == null) {
            mostrarErro("Selecione uma bebida.");
            return;
        }

        Integer quantidade = parseInteiro(campoQuantidade.getText());
        if (quantidade == null || quantidade <= 0) {
            mostrarErro("Informe uma quantidade maior que zero.");
            return;
        }
        if (quantidade > bebida.getEstoque()) {
            mostrarErro("Estoque insuficiente para \"" + bebida.getNome()
                    + "\" (disponível: " + bebida.getEstoque() + ").");
            return;
        }

        ItemVenda novo = new ItemVenda(
                bebida.getId(), bebida.getNome(), quantidade, bebida.getPreco());

        ItemVenda existente = null;
        for (ItemVenda item : itens) {
            if (item.getBebidaId() == bebida.getId()) {
                existente = item;
                break;
            }
        }

        if (existente != null) {
            int novaQtd = existente.getQuantidade() + quantidade;
            if (novaQtd > bebida.getEstoque()) {
                mostrarErro("Estoque insuficiente para \"" + bebida.getNome()
                        + "\" (disponível: " + bebida.getEstoque() + ").");
                return;
            }
            itens.set(itens.indexOf(existente),
                    new ItemVenda(bebida.getId(), bebida.getNome(), novaQtd, bebida.getPreco()));
        } else {
            itens.add(novo);
        }

        campoQuantidade.setText("1");
        atualizarResumo();
    }

    private void finalizarVenda() {
        feedback.setVisible(false);
        campoDesconto.getStyleClass().remove("input-error");

        Double desconto = parseDesconto();
        if (desconto == null) {
            campoDesconto.getStyleClass().add("input-error");
            mostrarErro("Informe um desconto válido (ex.: 0 ou 10,5).");
            return;
        }
        if (desconto < 0 || desconto > 100) {
            campoDesconto.getStyleClass().add("input-error");
            mostrarErro("O desconto deve estar entre 0 e 100%.");
            return;
        }

        LoadingService.central("Finalizando venda...");
        try {
            Venda venda = controle.registrarVenda(
                    List.copyOf(itens),
                    comboPagamento.getValue(),
                    desconto);
            notificar.accept("Venda " + Formatadores.codigo(venda.getId() + 1)
                    + " concluída em " + Formatadores.dataHora(venda.getDataHora())
                    + " — " + Formatadores.moeda(simbolo, venda.valorTotal()));
            if (controle.imprimirComprovante()) {
                notificar.accept("Comprovante impresso para a venda "
                        + Formatadores.codigo(venda.getId() + 1) + ".");
            }
            cancelarVenda();
        } catch (IllegalArgumentException e) {
            mostrarErro(e.getMessage());
        } finally {
            LoadingService.parar();
        }
    }

    private void aplicarDesconto() {
        feedback.setVisible(false);
        campoDesconto.getStyleClass().remove("input-error");

        Double desconto = parseDesconto();
        if (desconto == null) {
            campoDesconto.getStyleClass().add("input-error");
            mostrarErro("Informe um desconto válido (ex.: 0 ou 10,5).");
            return;
        }
        if (desconto < 0 || desconto > 100) {
            campoDesconto.getStyleClass().add("input-error");
            mostrarErro("O desconto deve estar entre 0 e 100%.");
            return;
        }
        if (desconto > controle.limiteDescontoPercentual()) {
            campoDesconto.getStyleClass().add("input-error");
            mostrarErro("Desconto acima do limite configurado ("
                    + exibir(controle.limiteDescontoPercentual()) + "%).");
            return;
        }

        atualizarResumo();
        feedback.getStyleClass().removeAll("field-error", "field-success");
        feedback.getStyleClass().add("field-success");
        feedback.setText("Desconto de " + exibir(desconto) + "% aplicado ao total.");
        feedback.setVisible(true);
    }

    private Double parseDesconto() {
        String texto = campoDesconto == null ? null : campoDesconto.getText();
        if (texto == null || texto.isBlank()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void cancelarVenda() {
        itens.clear();
        campoQuantidade.setText("1");
        campoDesconto.setText("0");
        campoDesconto.getStyleClass().remove("input-error");
        feedback.getStyleClass().removeAll("field-error", "field-success");
        comboPagamento.getSelectionModel().clearSelection();
        if (!comboPagamento.getItems().isEmpty()) {
            comboPagamento.setValue(comboPagamento.getItems().get(0));
        }
        comboBebida.getSelectionModel().clearSelection();
        feedback.setVisible(false);
        atualizarResumo();
    }

    private void atualizarResumo() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemVenda item : itens) {
            subtotal = subtotal.add(item.subtotal());
        }

        Double pctObj = parseDesconto();
        double pct = (pctObj == null || pctObj < 0 || pctObj > 100) ? 0 : pctObj;

        BigDecimal desconto = subtotal
                .multiply(BigDecimal.valueOf(pct))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.subtract(desconto);

        labelSubtotal.setText(Formatadores.moeda(simbolo, subtotal));
        labelDesconto.setText(Formatadores.moeda(simbolo, desconto));
        labelTotal.setText(Formatadores.moeda(simbolo, total));
    }

    private void mostrarErro(String mensagem) {
        feedback.getStyleClass().removeAll("field-error", "field-success");
        feedback.getStyleClass().add("field-error");
        feedback.setText(mensagem);
        feedback.setVisible(true);
    }

    private Integer parseInteiro(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String exibir(double valor) {
        if (valor == Math.floor(valor)) {
            return String.valueOf((long) valor);
        }
        return String.valueOf(valor).replace('.', ',');
    }
}