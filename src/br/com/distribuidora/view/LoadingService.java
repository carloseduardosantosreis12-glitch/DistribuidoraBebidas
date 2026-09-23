package br.com.distribuidora.view;

import br.com.distribuidora.view.components.LoadingOverlay;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public final class LoadingService {

    private static final long MIN_DISPLAY = 400L;

    private static LoadingOverlay camada;
    private static int pedidos;
    private static long inicio;
    private static PauseTransition pendente;

    private LoadingService() {
    }

    public static void registrar(LoadingOverlay camada) {
        LoadingService.camada = camada;
    }

    public static void barra() {
        pedidos++;
        cancelarPendente();
        if (camada != null) {
            camada.mostrarBarra();
        }
        inicio = System.currentTimeMillis();
    }

    public static void central(String rotulo) {
        pedidos++;
        cancelarPendente();
        if (camada != null) {
            camada.mostrarCentral(rotulo);
        }
        inicio = System.currentTimeMillis();
    }

    public static void parar() {
        if (pedidos <= 0) {
            return;
        }
        pedidos--;
        if (pedidos > 0 || camada == null) {
            return;
        }
        long restante = MIN_DISPLAY - (System.currentTimeMillis() - inicio);
        pendente = new PauseTransition(Duration.millis(Math.max(0, restante)));
        pendente.setOnFinished(e -> camada.esconder());
        pendente.play();
    }

    private static void cancelarPendente() {
        if (pendente != null) {
            pendente.stop();
            pendente = null;
        }
    }
}