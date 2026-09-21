package br.com.distribuidora.view.components;

import java.util.Optional;

import javafx.scene.control.Label;

public class StatusBadge extends Label {

    public static final int LIMITE_ESTOQUE_BAIXO = 10;

    public enum Nivel {
        OK("badge-success"),
        ATENCAO("badge-warning"),
        CRITICO("badge-danger"),
        INFO("badge-info");

        private final String classe;

        Nivel(String classe) {
            this.classe = classe;
        }
    }

    public StatusBadge(String texto, Nivel nivel) {
        super(texto);
        getStyleClass().add("badge");
        getStyleClass().add(nivel.classe);
    }

    public static Optional<StatusBadge> paraEstoque(int estoque, int limite) {
        if (estoque <= 0) {
            return Optional.of(new StatusBadge("Esgotado", Nivel.CRITICO));
        }
        if (estoque <= limite) {
            return Optional.of(new StatusBadge("Baixo", Nivel.ATENCAO));
        }
        return Optional.empty();
    }

    public static Optional<StatusBadge> paraEstoque(int estoque) {
        return paraEstoque(estoque, LIMITE_ESTOQUE_BAIXO);
    }
}
