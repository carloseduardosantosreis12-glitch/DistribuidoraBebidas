package br.com.distribuidora;

import br.com.distribuidora.view.DashboardView;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    private static final double MIN_W = 1200, MIN_H = 680;
    private static final double INICIAL_W = 1360, INICIAL_H = 800;

    @Override
    public void start(Stage stage) {
        DashboardView dashboard = new DashboardView(25, 12, 8);
        Scene scene = new Scene(dashboard.getRoot(), INICIAL_W, INICIAL_H);

        carregarCss(scene, "/css/tokens.css");
        carregarCss(scene, "/css/base.css");
        carregarCss(scene, "/css/style.css");

        Rectangle2D area = Screen.getPrimary().getVisualBounds();
        stage.setMinWidth(MIN_W);
        stage.setMinHeight(MIN_H);
        stage.setWidth(Math.max(MIN_W, Math.min(INICIAL_W, area.getWidth() * 0.92)));
        stage.setHeight(Math.max(MIN_H, Math.min(INICIAL_H, area.getHeight() * 0.92)));
        stage.setScene(scene);
        stage.setTitle("Distribuidora de Bebidas");
        stage.centerOnScreen();
        stage.show();
    }

    private void carregarCss(Scene scene, String caminho) {
        URL url = getClass().getResource(caminho);
        if (url == null) {
            throw new IllegalStateException("CSS nao encontrado no classpath: " + caminho);
        }
        scene.getStylesheets().add(url.toExternalForm());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
