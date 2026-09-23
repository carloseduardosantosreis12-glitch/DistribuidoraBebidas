package br.com.distribuidora.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Formatadores {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DecimalFormat MOEDA;

    static {
        MOEDA = (DecimalFormat) DecimalFormat.getNumberInstance(new Locale("pt", "BR"));
        MOEDA.setMinimumFractionDigits(2);
        MOEDA.setMaximumFractionDigits(2);
        MOEDA.setGroupingUsed(true);
        MOEDA.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(new Locale("pt", "BR")));
    }

    private Formatadores() {
    }

    public static String moeda(String simbolo, BigDecimal valor) {
        return simbolo + " " + MOEDA.format(valor);
    }

    public static String data(LocalDate data) {
        return data == null ? "—" : data.format(DATA);
    }

    public static String codigo(int id) {
        return String.format("%03d", id);
    }
}