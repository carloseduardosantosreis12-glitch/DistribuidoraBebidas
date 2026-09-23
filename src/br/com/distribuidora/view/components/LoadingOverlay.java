package br.com.distribuidora.view.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoadingOverlay extends StackPane {

    private static final double ALTURA_BARRA = 3;

    private final ProgressBar barra = new ProgressBar();
    private final StackPane fundo = new StackPane();
    private final Label rotulo = new Label();

    public LoadingOverlay() {
        barra.getStyleClass().add("loading-bar");
        barra.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        barra.setPrefHeight(ALTURA_BARRA);
        barra.setMinHeight(ALTURA_BARRA);
        barra.setMaxHeight(ALTURA_BARRA);
        barra.setMaxWidth(Double.MAX_VALUE);
        barra.setMouseTransparent(true);

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.getStyleClass().add("loading-spinner");
        spinner.setPrefSize(46, 46);

        rotulo.getStyleClass().add("loading-label");

        VBox cartao = new VBox(14, spinner, rotulo);
        cartao.getStyleClass().add("loading-card");
        cartao.setAlignment(Pos.CENTER);
        cartao.setMaxWidth(Double.MAX_VALUE);

        fundo.getStyleClass().add("loading-fundo");
        fundo.setAlignment(Pos.CENTER);
        fundo.getChildren().add(cartao);

        setAlignment(Pos.TOP_CENTER);
        setPickOnBounds(false);
        getChildren().addAll(barra, fundo);

        esconder();
    }

    public void mostrarBarra() {
        fundo.setVisible(false);
        fundo.setManaged(false);
        fundo.setMouseTransparent(true);
        barra.setVisible(true);
        barra.setManaged(true);
        setMouseTransparent(true);
    }

    public void mostrarCentral(String texto) {
        rotulo.setText(texto);
        barra.setVisible(false);
        barra.setManaged(false);
        fundo.setVisible(true);
        fundo.setManaged(true);
        fundo.setMouseTransparent(false);
        setMouseTransparent(false);
    }

    public void esconder() {
        barra.setVisible(false);
        barra.setManaged(false);
        fundo.setVisible(false);
        fundo.setManaged(false);
        fundo.setMouseTransparent(true);
        setMouseTransparent(true);
    }
}