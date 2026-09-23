package br.com.distribuidora.view;

import br.com.distribuidora.controller.ConfiguracaoController;
import br.com.distribuidora.view.components.Toast;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class MainLayout {

    private final BorderPane root = new BorderPane();
    private final ScrollPane conteudo = new ScrollPane();
    private final StackPane camadaToast = new StackPane();
    private final Sidebar sidebar;

    public MainLayout() {
        sidebar = new Sidebar(this::navegar);

        conteudo.getStyleClass().add("content-scroll");
        conteudo.setFitToWidth(true);
        conteudo.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        conteudo.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        camadaToast.setMouseTransparent(true);
        camadaToast.setAlignment(Pos.BOTTOM_RIGHT);
        camadaToast.setPadding(new Insets(16));
        camadaToast.setPickOnBounds(false);

        StackPane centro = new StackPane(conteudo, camadaToast);
        centro.setAlignment(Pos.TOP_LEFT);

        root.setLeft(sidebar);
        root.setCenter(centro);

        navegar(Sidebar.Item.INICIO);
    }

    private void navegar(Sidebar.Item item) {
        switch (item) {
            case INICIO -> {
                sidebar.setActive(item);
                conteudo.setContent(novoDashboard());
            }
            case CADASTRAR -> {
                sidebar.setActive(item);
                conteudo.setContent(novoCadastro());
            }
            case BEBIDAS -> {
                sidebar.setActive(item);
                conteudo.setContent(novoBebidas());
            }
            case RELATORIOS -> {
                sidebar.setActive(item);
                conteudo.setContent(novoRelatorio());
            }
            case CONFIGURACOES -> {
                sidebar.setActive(item);
                conteudo.setContent(novaConfiguracao());
            }
            case VENDAS -> {
            }
        }
    }

    private Node novoDashboard() {
        return new DashboardView(
                () -> navegar(Sidebar.Item.CADASTRAR),
                () -> navegar(Sidebar.Item.BEBIDAS)
        ).getRoot();
    }

    private Node novoBebidas() {
        return new BebidasView(
                () -> navegar(Sidebar.Item.CADASTRAR),
                mensagem -> Toast.mostrar(camadaToast, mensagem)
        ).getRoot();
    }

    private Node novoCadastro() {
        return new CadastroBebidaView(
                mensagem -> Toast.mostrar(camadaToast, mensagem)
        ).getRoot();
    }

    private Node novoRelatorio() {
        return new RelatorioView().getRoot();
    }

    private Node novaConfiguracao() {
        return new ConfiguracaoView(() -> {
            sidebar.setNomeEmpresa(ConfiguracaoController.getInstance().nomeEmpresa());
            navegar(Sidebar.Item.INICIO);
        }).getRoot();
    }

    public Pane getCamadaToast() {
        return camadaToast;
    }

    public BorderPane getRoot() {
        return root;
    }
}