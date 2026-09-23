package br.com.distribuidora;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SistemaTema {

    private static final Pattern REG_DWORD = Pattern.compile("0x([0-9a-fA-F]+)");

    private SistemaTema() {
    }

    public static String detectar() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (!os.contains("win")) {
            return "claro";
        }
        try {
            Process processo = new ProcessBuilder(
                    "reg", "query",
                    "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
                    "/v", "AppsUseLightTheme")
                    .redirectErrorStream(true).start();
            String saida = new String(processo.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            if (processo.waitFor() != 0) {
                return "claro";
            }
            Matcher m = REG_DWORD.matcher(saida);
            String valor = null;
            while (m.find()) {
                valor = m.group(1);
            }
            if (valor != null && "1".equals(valor)) {
                return "claro";
            }
            return "escuro";
        } catch (Exception e) {
            return "claro";
        }
    }
}