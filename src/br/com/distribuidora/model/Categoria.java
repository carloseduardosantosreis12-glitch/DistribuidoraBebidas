package br.com.distribuidora.model;

import java.util.Arrays;
import java.util.List;

public enum Categoria {

    REFRIGERANTE("Refrigerante"),
    AGUA("Água"),
    SUCO("Suco"),
    CERVEJA("Cerveja"),
    ENERGETICO("Energético"),
    VINHO("Vinho"),
    DESTILADO("Destilado"),
    OUTRO("Outro");

    private final String label;

    Categoria(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Categoria porLabel(String label) {
        for (Categoria c : values()) {
            if (c.label.equals(label)) {
                return c;
            }
        }
        return OUTRO;
    }

    public static List<String> labels() {
        return Arrays.stream(values()).map(Categoria::getLabel).toList();
    }
}