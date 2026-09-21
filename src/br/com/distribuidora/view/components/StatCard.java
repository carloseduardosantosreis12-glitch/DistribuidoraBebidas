package br.com.distribuidora.view.components;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class StatCard extends HBox {

    public StatCard(String rotulo, String valor) {
        this(rotulo, valor, false);
    }

    public StatCard(String rotulo, String valor, boolean atencao) {
        getStyleClass().add("stat-card");
        if (atencao) {
            getStyleClass().add("warning");
        }
        setMinHeight(96);
        setPrefHeight(96);
        setMaxHeight(96);

        Region barra = new Region();
        barra.getStyleClass().add("stat-card-bar");
        barra.setMinWidth(4);
        barra.setPrefWidth(4);
        barra.setMaxWidth(4);

        Label rotuloLabel = new Label(rotulo);
        rotuloLabel.getStyleClass().add("stat-card-label");

        Label valorLabel = new Label(valor);
        valorLabel.getStyleClass().add("stat-card-value");

        VBox conteudo = new VBox(6, rotuloLabel, valorLabel);
        conteudo.getStyleClass().add("stat-card-content");

        getChildren().addAll(barra, conteudo);
    }
}
