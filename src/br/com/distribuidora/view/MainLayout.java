package br.com.distribuidora.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
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
        camadaConteudo.getChildren().setAll(placeholder(item));
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
