package br.com.distribuidora.view.components;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public final class Toast {

    private static final Duration DURACAO = Duration.seconds(3);

    private Toast() {
    }

    public static void mostrar(Pane camada, String mensagem) {
        FontIcon icone = new FontIcon(FontAwesomeSolid.CHECK_CIRCLE);
        icone.getStyleClass().add("toast-icon");

        Label texto = new Label(mensagem);
        texto.getStyleClass().add("toast-text");

        HBox toast = new HBox(8, icone, texto);
        toast.setAlignment(Pos.CENTER_LEFT);
        toast.getStyleClass().add("toast");
        toast.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        camada.getChildren().add(toast);

        PauseTransition espera = new PauseTransition(DURACAO);
        espera.setOnFinished(evento -> camada.getChildren().remove(toast));
        espera.play();
    }
}
