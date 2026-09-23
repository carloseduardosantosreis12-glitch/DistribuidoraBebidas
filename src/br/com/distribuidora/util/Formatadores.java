package br.com.distribuidora.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class Formatadores {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Formatadores() {
    }

    public static String moeda(String simbolo, BigDecimal valor) {
        return simbolo + " " + String.format("%.2f", valor).replace('.', ',');
    }

    public static String data(LocalDate data) {
        return data == null ? "—" : data.format(DATA);
    }

    public static String codigo(int id) {
        return String.format("%03d", id);
    }
}