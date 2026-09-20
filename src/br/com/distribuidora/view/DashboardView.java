package br.com.distribuidora.view;

import br.com.distribuidora.model.Bebida;
import br.com.distribuidora.repository.ConfiguracaoStore;
import br.com.distribuidora.repository.EstoqueRepository;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class DashboardView {

    private BorderPane root;
    private final EstoqueRepository estoque = EstoqueRepository.getInstance();
    private final ConfiguracaoStore config = ConfiguracaoStore.getInstance();
    private Label logo;

    public DashboardView() {

        root = new BorderPane();
        root.setId("root");

        // =========================
        // MENU LATERAL
        // =========================

        VBox menu = new VBox(10);

        menu.setPrefWidth(230);
        menu.setPadding(new Insets(20));
        menu.setId("menu-lateral");

        root.setLeft(menu);

        logo = new Label(config.getNomeEmpresa());
        logo.setId("logo");
        menu.getChildren().add(logo);

        Button inicio = new Button("Início");
        inicio.setMaxWidth(Double.MAX_VALUE);
        inicio.getStyleClass().add("menu-ativo");
        menu.getChildren().add(inicio);

        Button bebidas = new Button("Bebidas");
        bebidas.setMaxWidth(Double.MAX_VALUE);
        bebidas.getStyleClass().add("menu-botao");
        menu.getChildren().add(bebidas);

        Button cadastrar = new Button("Cadastrar Bebida");
        cadastrar.setMaxWidth(Double.MAX_VALUE);
        cadastrar.getStyleClass().add("menu-botao");
        menu.getChildren().add(cadastrar);

        Button vendas = new Button("Vendas");
        vendas.setMaxWidth(Double.MAX_VALUE);
        vendas.getStyleClass().add("menu-botao");
        menu.getChildren().add(vendas);

        Button relatorio = new Button("Relatórios");
        relatorio.setMaxWidth(Double.MAX_VALUE);
        relatorio.getStyleClass().add("menu-botao");
        menu.getChildren().add(relatorio);

        Button configuracoes = new Button("Configurações");
        configuracoes.setMaxWidth(Double.MAX_VALUE);
        configuracoes.getStyleClass().add("menu-botao");
        menu.getChildren().add(configuracoes);

        root.setCenter(criarConteudoDashboard());

        // =========================
        // NAVEGAÇÃO
        // =========================

        inicio.setOnAction(event -> root.setCenter(criarConteudoDashboard()));

        cadastrar.setOnAction(event -> {
            CadastroBebidaView cadastroView = new CadastroBebidaView();
            root.setCenter(cadastroView.getRoot());
        });

        relatorio.setOnAction(event -> {
            RelatorioView relatorioView = new RelatorioView();
            root.setCenter(relatorioView.getRoot());
        });

        configuracoes.setOnAction(event -> {
            ConfiguracaoView configuracaoView = new ConfiguracaoView(() -> {
                logo.setText(config.getNomeEmpresa());
                root.setCenter(criarConteudoDashboard());
            });
            root.setCenter(configuracaoView.getRoot());
        });

        // =========================
        // CSS
        // =========================

        String css = getClass()
                .getResource("/css/style.css")
                .toExternalForm();

        root.getStylesheets().add(css);
    }

    // =========================
    // CONTEÚDO DO DASHBOARD
    // =========================

    private VBox criarConteudoDashboard() {

        Label titulo = new Label("Painel de Controle");
        titulo.setId("titulo");

        Label subtitulo = new Label(
                "Visão geral do estoque da " + config.getNomeEmpresa()
        );
        subtitulo.getStyleClass().add("dashboard-subtitulo");

        VBox cabecalhoDashboard = new VBox(4);
        cabecalhoDashboard.getChildren().addAll(titulo, subtitulo);

        VBox conteudo = new VBox(25);
        conteudo.setPadding(new Insets(35));

        conteudo.getChildren().add(cabecalhoDashboard);

        // =========================
        // CARDS
        // =========================

        List<Bebida> baixo = estoque.estoqueBaixo(config.getLimiteEstoqueBaixo());

        HBox cards = new HBox(18);
        cards.setPrefHeight(150);
        cards.setFillHeight(true);

        StackPane cardProdutos = criarCard(
                "Bebidas cadastradas",
                String.valueOf(estoque.totalProdutos()),
                "Produtos cadastrados"
        );

        StackPane cardEstoque = criarCard(
                "Unidades em estoque",
                String.valueOf(estoque.totalUnidades()),
                "Total disponível"
        );

        StackPane cardBaixo = criarCard(
                "Estoque baixo",
                String.valueOf(baixo.size()),
                "Produtos para repor"
        );

        StackPane cardValor = criarCard(
                "Valor do estoque",
                config.getMoeda() + " " + String.format("%.2f", estoque.valorTotalEstoque()),
                "Preço de custo + margem"
        );

        cardProdutos.getStyleClass().add("card-azul");
        cardEstoque.getStyleClass().add("card-verde");
        cardBaixo.getStyleClass().add("card-laranja");
        cardValor.getStyleClass().add("card-roxo");

        cards.getChildren().addAll(
                cardProdutos,
                cardEstoque,
                cardBaixo,
                cardValor
        );

        conteudo.getChildren().add(cards);

        // =========================
        // ESTOQUE BAIXO
        // =========================

        Label tituloEstoque = new Label("Estoque baixo");
        tituloEstoque.getStyleClass().add("secao-titulo");

        VBox listaEstoque = new VBox(10);

        if (baixo.isEmpty()) {
            Label vazio = new Label(
                    "Nenhum produto abaixo do limite definido."
            );
            vazio.getStyleClass().add("item-estoque");
            listaEstoque.getChildren().add(vazio);
        } else {
            for (Bebida b : baixo) {
                HBox item = new HBox(20);
                item.getStyleClass().add("item-estoque");

                Label produto = new Label(b.getNome() + " (" + b.getMarca() + ")");
                Label quantidade = new Label(b.getEstoque() + " unidades");

                HBox.setHgrow(produto, Priority.ALWAYS);

                item.getChildren().addAll(produto, quantidade);
                listaEstoque.getChildren().add(item);
            }
        }

        conteudo.getChildren().add(tituloEstoque);
        conteudo.getChildren().add(listaEstoque);

        // =========================
        // RESUMO DE VENDAS
        // =========================

        Label tituloVendas = new Label("Resumo de vendas");
        tituloVendas.getStyleClass().add("secao-titulo");

        HBox resumoVendas = new HBox(20);
        resumoVendas.getStyleClass().add("lista-vendas");

        Label rotuloVendas = new Label("Vendas registradas");
        Label valorVendas = new Label(String.valueOf(estoque.totalVendas()));

        HBox.setHgrow(rotuloVendas, Priority.ALWAYS);

        resumoVendas.getChildren().addAll(rotuloVendas, valorVendas);

        conteudo.getChildren().add(tituloVendas);
        conteudo.getChildren().add(resumoVendas);

        return conteudo;
    }

    // =========================
    // MÉTODO PARA CRIAR CARDS
    // =========================

    private StackPane criarCard(
            String titulo,
            String valor,
            String descricao) {

        StackPane card = new StackPane();

        card.setPrefSize(200, 120);
        card.getStyleClass().add("card");

        HBox.setHgrow(card, Priority.ALWAYS);

        Label labelTitulo = new Label(titulo);
        labelTitulo.getStyleClass().add("card-titulo");

        Label labelValor = new Label(valor);
        labelValor.getStyleClass().add("card-valor");

        Label labelDescricao = new Label(descricao);
        labelDescricao.getStyleClass().add("card-descricao");

        VBox conteudo = new VBox(5);

        conteudo.getChildren().addAll(
                labelTitulo,
                labelValor,
                labelDescricao
        );

        card.getChildren().add(conteudo);

        return card;
    }

    public BorderPane getRoot() {
        return root;
    }
}