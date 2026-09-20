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

    public DashboardView() {
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
        
        Label titulo =new Label("Painel de Controle");
        VBox conteudo = new VBox(20);
        conteudo.setPadding(new Insets(30));
        conteudo.getChildren().add(titulo);
        
        HBox cards = new HBox(20);
        cards.setPrefHeight(120);
        
        StackPane cardEstoque = new StackPane();
        cardEstoque.setPrefSize(200, 120);
        cardEstoque.getStyleClass().add("card");

        Label textoEstoque = new Label("Estoque");
        textoEstoque.getStyleClass().add("card-titulo");
        Label valorEstoque = new Label("25");
        valorEstoque.getStyleClass().add("card-valor");
        
        HBox.setHgrow(cardEstoque, Priority.ALWAYS);
        
        VBox conteudoEstoque =new VBox(5);
        conteudoEstoque.getChildren().addAll(textoEstoque, valorEstoque);
        
        cardEstoque.getChildren().add(conteudoEstoque);
        cards.getChildren().add(cardEstoque);
        
        StackPane cardVendas = new StackPane();
        cardVendas.setPrefSize(200, 120);
        cardVendas.getStyleClass().add("card");
        
        Label textoVendas = new Label("Vendas");
        textoVendas.getStyleClass().add("card-titulo");
        Label valorVendas = new Label("12");
        
        HBox.setHgrow(cardVendas, Priority.ALWAYS);
        
        valorVendas.getStyleClass().add("card-valor");
        
        cards.getChildren().add(cardVendas);
        
        VBox conteudoVendas = new VBox(5);
        conteudoVendas.getChildren().addAll(textoVendas, valorVendas);
        cardVendas.getChildren().add(conteudoVendas);
        
        
        StackPane cardProdutos = new StackPane();
        cardProdutos.setPrefSize(200, 120);
        cardProdutos.getStyleClass().add("card");

        Label textoProdutos = new Label("Produtos");
        textoProdutos.getStyleClass().add("card-titulo");
        
        Label valorProdutos = new Label("8");
        valorProdutos.getStyleClass().add("card-valor");
        
        VBox conteudoProdutos = new VBox(5);
        conteudoProdutos.getChildren().addAll(textoProdutos, valorProdutos);
        
        HBox.setHgrow(cardProdutos, Priority.ALWAYS);
        
        cardProdutos.getChildren().add(conteudoProdutos);
        	
        
        cards.getChildren().add(cardProdutos);
        
        conteudo.getChildren().add(cards);
        
        root.setCenter(conteudo);
        titulo.setId("titulo");
        
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
        
        Button relatorio = new Button("Relatóris");
        menu.getChildren().add(relatorio);
        relatorio.setMaxWidth(Double.MAX_VALUE);
        relatorio.getStyleClass().add("menu-botao");
        
        Button configuracoes = new Button("Configurações");
        menu.getChildren().add(configuracoes);
        configuracoes.setMaxWidth(Double.MAX_VALUE);
        configuracoes.getStyleClass().add("menu-botao");	
        
        String css = getClass()
                .getResource("/css/style.css")
                .toExternalForm();

        root.getStylesheets().add(css);
    }

    public BorderPane getRoot() {
        return root;
    }
}