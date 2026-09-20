package br.com.distribuidora.view;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Priority;

public class DashboardView {

    private BorderPane root;

    public DashboardView(int totalEstoque, int totalVendas, int totalProdutos) {
        root = new BorderPane();

        VBox menu = new VBox(10);
        menu.setPrefWidth(230);
        menu.setPadding(new Insets(20));
        menu.setId("menu-lateral");

        root.setLeft(menu);
        Label logo = new Label("BebMais");
        menu.getChildren().add(logo);
        logo.setId("logo");

        Button inicio = new Button("Início");
        menu.getChildren().add(inicio);
        inicio.setMaxWidth(Double.MAX_VALUE);
        inicio.getStyleClass().add("menu-botao");

        Button bebidas = new Button("Bebidas");
        menu.getChildren().add(bebidas);
        bebidas.setMaxWidth(Double.MAX_VALUE);
        bebidas.getStyleClass().add("menu-botao");

        Button cadastrar = new Button("Cadastrar Bebida");
        menu.getChildren().add(cadastrar);
        cadastrar.setMaxWidth(Double.MAX_VALUE);
        cadastrar.getStyleClass().add("menu-botao");

        Button vendas = new Button("Vendas");
        menu.getChildren().add(vendas);
        vendas.setMaxWidth(Double.MAX_VALUE);
        vendas.getStyleClass().add("menu-botao");

        Button relatorio = new Button("Relatórios");
        menu.getChildren().add(relatorio);
        relatorio.setMaxWidth(Double.MAX_VALUE);
        relatorio.getStyleClass().add("menu-botao");

        Button configuracoes = new Button("Configurações");
        menu.getChildren().add(configuracoes);
        configuracoes.setMaxWidth(Double.MAX_VALUE);
        configuracoes.getStyleClass().add("menu-botao");

        Label titulo = new Label("Painel de Controle");
        titulo.setId("titulo");

        VBox conteudo = new VBox(20);
        conteudo.setPadding(new Insets(30));
        conteudo.getChildren().add(titulo);

        HBox cards = new HBox(20);
        cards.setPrefHeight(120);

        cards.getChildren().add(criarCard("Estoque", String.valueOf(totalEstoque)));
        cards.getChildren().add(criarCard("Vendas", String.valueOf(totalVendas)));
        cards.getChildren().add(criarCard("Produtos", String.valueOf(totalProdutos)));

        conteudo.getChildren().add(cards);
        root.setCenter(conteudo);
    }

    private StackPane criarCard(String titulo, String valor) {
        StackPane card = new StackPane();
        card.setPrefSize(200, 120);
        card.getStyleClass().add("card");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label labelTitulo = new Label(titulo);
        labelTitulo.getStyleClass().add("card-titulo");

        Label labelValor = new Label(valor);
        labelValor.getStyleClass().add("card-valor");

        VBox conteudo = new VBox(5);
        conteudo.getChildren().addAll(labelTitulo, labelValor);
        card.getChildren().add(conteudo);

        return card;
    }

    public BorderPane getRoot() {
        return root;
    }
}
