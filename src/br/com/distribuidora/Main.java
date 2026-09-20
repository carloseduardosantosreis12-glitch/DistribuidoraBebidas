package br.com.distribuidora;

import br.com.distribuidora.model.Bebida;
import br.com.distribuidora.view.DashboardView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
    	
    	Bebida bebida = new Bebida(
    	        1,
    	        "Coca-Cola",
    	        "Refrigerante",
    	        10.50,
    	        25
    	);

    	System.out.println(bebida.descricao());
    	
    	DashboardView dashboard = new DashboardView();
    	Scene scene = new Scene(
    			dashboard.getRoot(),
    			1280,
    			 720
    			);
    	stage.setScene(scene);
    	
    	scene.getStylesheets().add(
    			getClass().getResource("/css/style.css").toExternalForm()
    			);
    	
        stage.setTitle("Distribuidora de Bebidas");
        stage.show();
        
        
    }

    public static void main(String[] args) {
        launch(args);
    
    
    }
    
    
    
}