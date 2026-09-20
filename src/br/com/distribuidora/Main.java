package br.com.distribuidora;

import br.com.distribuidora.view.DashboardView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        DashboardView dashboard = new DashboardView();
        Scene scene = new Scene(dashboard.getRoot(), 1280, 720);

        var cssUrl = getClass().getResource("/css/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setScene(scene);
        stage.setTitle("Distribuidora de Bebidas");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
