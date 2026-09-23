package br.com.distribuidora.view;

import br.com.distribuidora.model.Bebida;
import br.com.distribuidora.repository.ConfiguracaoStore;
import br.com.distribuidora.repository.EstoqueRepository;
import br.com.distribuidora.util.Formatadores;
import br.com.distribuidora.view.components.PageHeader;
import br.com.distribuidora.view.components.StatCard;
import br.com.distribuidora.view.components.TabelaCelulas;
import java.math.BigDecimal;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class DashboardView {

    private final EstoqueRepository estoque = EstoqueRepository.getInstance();
    private final ConfiguracaoStore config = ConfiguracaoStore.getInstance();
    private final Runnable onNovaBebida;
    private final Runnable onVerTodas;

    public DashboardView(Runnable onNovaBebida, Runnable onVerTodas) {
        this.onNovaBebida = onNovaBebida;
        this.onVerTodas = onVerTodas;
    }

    public VBox getRoot() {
        int limite = config.getLimiteEstoqueBaixo();
        List<Bebida> recentes = cincoUltimas();

        PageHeader cabecalho = new PageHeader(
                "Painel de controle",
                "Visão geral do estoque da " + config.getNomeEmpresa(),
                botaoNovaBebida()
        );

        HBox cards = new HBox(16,
                new StatCard("Bebidas cadastradas",
                        String.valueOf(estoque.totalProdutos())),
                new StatCard("Unidades em estoque",
                        String.valueOf(estoque.totalUnidades())),
                new StatCard("Alertas de estoque",
                        String.valueOf(estoque.estoqueBaixo(limite).size()), true),
                new StatCard("Valor do estoque",
                        Formatadores.moeda(config.getMoeda(), estoque.valorTotalEstoque())));
        cards.getChildren().forEach(no -> {
            HBox.setHgrow(no, Priority.ALWAYS);
            ((Region) no).setMaxWidth(Double.MAX_VALUE);
        });

        VBox raiz = new VBox(24,
                cabecalho,
                cards,
                secaoRecentes(recentes, limite));
        raiz.setMaxWidth(Double.MAX_VALUE);
        return raiz;
    }

    private List<Bebida> cincoUltimas() {
        List<Bebida> todas = estoque.listar();
        return todas.subList(Math.max(0, todas.size() - 5), todas.size());
    }

    private VBox secaoRecentes(List<Bebida> recentes, int limite) {
        Label titulo = new Label("Cadastradas recentemente");
        titulo.getStyleClass().add("section-title");

        Button verTodas = new Button("Ver todas");
        verTodas.getStyleClass().add("btn-link");
        verTodas.setOnAction(event -> onVerTodas.run());

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        HBox cabecalhoSecao = new HBox(12, titulo, espaco, verTodas);
        cabecalhoSecao.setAlignment(Pos.CENTER_LEFT);

        TableView<Bebida> tabela = tabelaRecentes(recentes, limite);
        double altura = 44d * (recentes.size() + 1);
        tabela.setPrefHeight(altura);
        tabela.setMaxHeight(altura);

        return new VBox(10, cabecalhoSecao, tabela);
    }

    private TableView<Bebida> tabelaRecentes(List<Bebida> recentes, int limite) {
        TableView<Bebida> tabela = new TableView<>(
                FXCollections.observableArrayList(recentes)
        );
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabela.setFixedCellSize(44);

        String moeda = config.getMoeda();

        TableColumn<Bebida, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(Formatadores.codigo(d.getValue().getId() + 1)));
        colCodigo.setMinWidth(64);

        TableColumn<Bebida, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getNome()));
        colNome.setPrefWidth(280);

        TableColumn<Bebida, String> colCategoria = new TableColumn<>("Categoria");
        colCategoria.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getCategoria()));
        colCategoria.setMinWidth(110);

        TableColumn<Bebida, BigDecimal> colPreco = new TableColumn<>("Preço");
        colPreco.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getPreco()));
        colPreco.setCellFactory(col -> TabelaCelulas.preco(moeda));
        colPreco.setMinWidth(90);

        TableColumn<Bebida, Integer> colEstoque = new TableColumn<>("Estoque");
        colEstoque.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getEstoque()));
        colEstoque.setCellFactory(col -> TabelaCelulas.estoque(limite));
        colEstoque.setMinWidth(110);

        tabela.getColumns().addAll(colCodigo, colNome, colCategoria, colPreco, colEstoque);
        return tabela;
    }

    private Button botaoNovaBebida() {
        Button botao = new Button("Nova bebida");
        botao.getStyleClass().add("btn-primary");
        FontIcon icone = new FontIcon(FontAwesomeSolid.PLUS);
        icone.setIconSize(14);
        botao.setGraphic(icone);
        botao.setOnAction(event -> onNovaBebida.run());
        return botao;
    }
}