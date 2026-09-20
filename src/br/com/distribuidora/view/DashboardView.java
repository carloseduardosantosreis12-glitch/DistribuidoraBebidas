package br.com.distribuidora.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class DashboardView {

    private BorderPane root;

    public DashboardView(int totalEstoque, int totalVendas, int totalProdutos) {

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

        Label logo = new Label("BebMais");
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

        // =========================
        // CONTEÚDO PRINCIPAL
        // =========================

        Label titulo = new Label("Painel de Controle");
        titulo.setId("titulo");

        Label subtitulo = new Label("Visão geral do estoque da distribuidora");
        subtitulo.getStyleClass().add("dashboard-subtitulo");

        VBox cabecalhoDashboard = new VBox(4);
        cabecalhoDashboard.getChildren().addAll(titulo, subtitulo);

        VBox conteudo = new VBox(25);
        conteudo.setPadding(new Insets(35));

        conteudo.getChildren().add(cabecalhoDashboard);

        // =========================
        // CARDS
        // =========================

        HBox cards = new HBox(18);
        cards.setPrefHeight(150);
        cards.setFillHeight(true);

        StackPane cardProdutos = criarCard(
                "Bebidas cadastradas",
                String.valueOf(totalProdutos),
                "Produtos cadastrados"
        );

        StackPane cardEstoque = criarCard(
                "Unidades em estoque",
                String.valueOf(totalEstoque),
                "Total disponível"
        );

        StackPane cardBaixo = criarCard(
                "Estoque baixo",
                "3",
                "Produtos para repor"
        );

        StackPane cardValor = criarCard(
                "Vendas",
                String.valueOf(totalVendas),
                "Vendas realizadas"
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

        HBox item1 = new HBox(20);
        item1.getStyleClass().add("item-estoque");

        Label produto1 = new Label("Coca-Cola");
        Label quantidade1 = new Label("5 unidades");

        HBox.setHgrow(produto1, Priority.ALWAYS);

        item1.getChildren().addAll(
                produto1,
                quantidade1
        );

        HBox item2 = new HBox(20);
        item2.getStyleClass().add("item-estoque");

        Label produto2 = new Label("Guaraná Antarctica");
        Label quantidade2 = new Label("3 unidades");

        HBox.setHgrow(produto2, Priority.ALWAYS);

        item2.getChildren().addAll(
                produto2,
                quantidade2
        );

        HBox item3 = new HBox(20);
        item3.getStyleClass().add("item-estoque");

        Label produto3 = new Label("Fanta Laranja");
        Label quantidade3 = new Label("2 unidades");

        HBox.setHgrow(produto3, Priority.ALWAYS);

        item3.getChildren().addAll(
                produto3,
                quantidade3
        );

        listaEstoque.getChildren().addAll(
                item1,
                item2,
                item3
        );

        conteudo.getChildren().add(tituloEstoque);
        conteudo.getChildren().add(listaEstoque);

        // =========================
        // VENDAS RECENTES
        // =========================

        Label tituloVendas = new Label("Vendas recentes");
        tituloVendas.getStyleClass().add("secao-titulo");

        VBox listaVendas = new VBox(8);
        listaVendas.getStyleClass().add("lista-vendas");

        HBox venda1 = new HBox(20);
        venda1.getStyleClass().add("item-venda");

        Label cliente1 = new Label("Cliente: João Silva");
        Label valor1 = new Label("R$ 150,00");

        HBox.setHgrow(cliente1, Priority.ALWAYS);

        venda1.getChildren().addAll(
                cliente1,
                valor1
        );

        HBox venda2 = new HBox(20);
        venda2.getStyleClass().add("item-venda");

        Label cliente2 = new Label("Cliente: Mercado Central");
        Label valor2 = new Label("R$ 320,00");

        HBox.setHgrow(cliente2, Priority.ALWAYS);

        venda2.getChildren().addAll(
                cliente2,
                valor2
        );

        HBox venda3 = new HBox(20);
        venda3.getStyleClass().add("item-venda");

        Label cliente3 = new Label("Cliente: Bar do Zé");
        Label valor3 = new Label("R$ 85,00");

        HBox.setHgrow(cliente3, Priority.ALWAYS);

        venda3.getChildren().addAll(
                cliente3,
                valor3
        );

        listaVendas.getChildren().addAll(
                venda1,
                venda2,
                venda3
        );

        conteudo.getChildren().add(tituloVendas);
        conteudo.getChildren().add(listaVendas);

        // =========================
        // CENTRO DO DASHBOARD
        // =========================

        root.setCenter(conteudo);
        
        
     // =========================
     // NAVEGAÇÃO
     // =========================

     // Voltar para o Dashboard
     inicio.setOnAction(event -> {
         root.setCenter(conteudo);
     });

     // Abrir tela de cadastro
     cadastrar.setOnAction(event -> {
         CadastroBebidaView cadastroView = new CadastroBebidaView();
         root.setCenter(cadastroView.getRoot());
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