package br.com.distribuidora.view.components;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

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

        Rectangle recorte = new Rectangle();
        recorte.widthProperty().bind(widthProperty());
        recorte.heightProperty().bind(heightProperty());
        recorte.setArcWidth(24);
        recorte.setArcHeight(24);
        setClip(recorte);

        Label rotuloLabel = new Label(rotulo);
        rotuloLabel.getStyleClass().add("stat-card-label");

        Label valorLabel = new Label(valor);
        valorLabel.getStyleClass().add("stat-card-value");

        VBox conteudo = new VBox(6, rotuloLabel, valorLabel);
        conteudo.getStyleClass().add("stat-card-content");
        HBox.setHgrow(conteudo, Priority.ALWAYS);
        conteudo.setMaxWidth(Double.MAX_VALUE);

        getChildren().add(conteudo);
    }
}
