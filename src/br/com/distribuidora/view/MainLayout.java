package br.com.distribuidora.view;

import java.math.BigDecimal;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import br.com.distribuidora.model.Bebida;
import br.com.distribuidora.view.components.PageHeader;
import br.com.distribuidora.view.components.StatCard;
import br.com.distribuidora.view.components.StatusBadge;
import br.com.distribuidora.view.components.Toast;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainLayout {

    private final BorderPane root = new BorderPane();
    private final StackPane camadaConteudo = new StackPane();
    private final StackPane camadaToast = new StackPane();
    private final Sidebar sidebar;

    public MainLayout() {
        sidebar = new Sidebar(this::navegar);

        camadaConteudo.setPadding(new Insets(28, 32, 28, 32));
        camadaConteudo.setAlignment(Pos.TOP_LEFT);

        camadaToast.setMouseTransparent(true);
        camadaToast.setAlignment(Pos.BOTTOM_RIGHT);
        camadaToast.setPadding(new Insets(16));

        StackPane centro = new StackPane(camadaConteudo, camadaToast);
        centro.setAlignment(Pos.TOP_LEFT);

        root.setLeft(sidebar);
        root.setCenter(centro);

        navegar(Sidebar.Item.INICIO);
    }

    private void navegar(Sidebar.Item item) {
        sidebar.setActive(item);
        if (item == Sidebar.Item.INICIO) {
            camadaConteudo.getChildren().setAll(previa());
        } else {
            camadaConteudo.getChildren().setAll(placeholder(item));
        }
    }

    private Node previa() {
        PageHeader cabecalho = new PageHeader(
                "Painel de controle",
                "Prévia temporária dos componentes (Fase C)",
                botao("Nova bebida", "btn-primary", FontAwesomeSolid.PLUS, () -> {
                }));

        HBox cards = new HBox(16,
                new StatCard("Total de bebidas", "5"),
                new StatCard("Unidades em estoque", "649"),
                new StatCard("Estoque baixo", "2", true));
        cards.getChildren().forEach(no -> {
            HBox.setHgrow(no, Priority.ALWAYS);
            ((Region) no).setMaxWidth(Double.MAX_VALUE);
        });

        HBox badges = new HBox(8,
                new StatusBadge("Esgotado", StatusBadge.Nivel.CRITICO),
                new StatusBadge("Baixo", StatusBadge.Nivel.ATENCAO),
                new StatusBadge("OK", StatusBadge.Nivel.OK));

        TableView<Bebida> tabela = previaTabela();

        Button disparaToast = botao("Mostrar toast", "btn-secondary", null,
                () -> Toast.mostrar(camadaToast, "Bebida cadastrada."));

        VBox conteudo = new VBox(24, cabecalho, cards, badges, tabela, disparaToast);
        conteudo.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(tabela, Priority.ALWAYS);
        return conteudo;
    }

    private TableView<Bebida> previaTabela() {
        TableColumn<Bebida, String> nome = new TableColumn<>("Nome");
        nome.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getNome()));

        TableColumn<Bebida, String> categoria = new TableColumn<>("Categoria");
        categoria.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getCategoria()));

        TableColumn<Bebida, String> preco = new TableColumn<>("Preço");
        preco.setCellValueFactory(dado -> new SimpleStringProperty(
                "R$ " + dado.getValue().getPreco().toPlainString().replace('.', ',')));

        TableColumn<Bebida, String> estoque = new TableColumn<>("Estoque");
        estoque.setCellValueFactory(dado -> new SimpleStringProperty(
                String.valueOf(dado.getValue().getEstoque())));

        TableView<Bebida> tabela = new TableView<>();
        tabela.getColumns().add(nome);
        tabela.getColumns().add(categoria);
        tabela.getColumns().add(preco);
        tabela.getColumns().add(estoque);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabela.setFixedCellSize(44);
        tabela.setItems(FXCollections.observableArrayList(
                new Bebida(1, "Coca-Cola 2L", "Refrigerante", new BigDecimal("9.90"), 35),
                new Bebida(2, "Água Mineral 500ml", "Água", new BigDecimal("2.50"), 6),
                new Bebida(3, "Energético Red Bull", "Energético", new BigDecimal("8.75"), 8)));
        return tabela;
    }

    private Button botao(String texto, String classe, FontAwesomeSolid icone, Runnable acao) {
        Button botao = new Button(texto);
        botao.getStyleClass().add(classe);
        if (icone != null) {
            FontIcon grafico = new FontIcon(icone);
            grafico.setIconSize(14);
            botao.setGraphic(grafico);
        }
        botao.setOnAction(evento -> acao.run());
        return botao;
    }

    private Node placeholder(Sidebar.Item item) {
        Label titulo = new Label(tituloDe(item));
        titulo.getStyleClass().add("page-title");

        Label subtitulo = new Label("Tela em construção (Fases D e E).");
        subtitulo.getStyleClass().add("page-subtitle");

        return new VBox(4, titulo, subtitulo);
    }

    private String tituloDe(Sidebar.Item item) {
        return switch (item) {
            case BEBIDAS -> "Bebidas";
            case CADASTRAR -> "Cadastrar bebida";
            default -> "Painel de controle";
        };
    }

    public StackPane getCamadaToast() {
        return camadaToast;
    }

    public BorderPane getRoot() {
        return root;
    }
}
