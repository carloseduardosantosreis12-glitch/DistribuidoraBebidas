package br.com.distribuidora.view.components;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class PageHeader extends HBox {

    public PageHeader(String titulo, String subtitulo) {
        this(titulo, subtitulo, null);
    }

    public PageHeader(String titulo, String subtitulo, Node acao) {
        getStyleClass().add("page-header");
        setAlignment(Pos.CENTER_LEFT);

        Label tituloLabel = new Label(titulo);
        tituloLabel.getStyleClass().add("page-title");

        VBox textos = new VBox(4, tituloLabel);

        if (subtitulo != null && !subtitulo.isBlank()) {
            Label subtituloLabel = new Label(subtitulo);
            subtituloLabel.getStyleClass().add("page-subtitle");
            textos.getChildren().add(subtituloLabel);
        }

        getChildren().add(textos);

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);
        getChildren().add(espaco);

        if (acao != null) {
            getChildren().add(acao);
        }
    }
}
