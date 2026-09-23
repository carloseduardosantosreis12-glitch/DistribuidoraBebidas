package br.com.distribuidora;

import br.com.distribuidora.controller.ConfiguracaoController;
import javafx.scene.Scene;

public final class ThemeService {

    private static final String TOKENS_CLARO = url("/css/tokens.css");
    private static final String TOKENS_ESCURO = url("/css/tokens-escuro.css");
    private static final String FONTE_PEQUENA = url("/css/fontes-pequena.css");
    private static final String FONTE_GRANDE = url("/css/fontes-grande.css");
    private static final String DENSIDADE_COMPACTA = url("/css/densidade-compacta.css");

    private static Scene cena;
    private static String temaAtual = "claro";
    private static String fonteAtual = "Média";
    private static String densidadeAtual = "Confortável";

    private ThemeService() {
    }

    private static String url(String caminho) {
        return ThemeService.class.getResource(caminho).toExternalForm();
    }

    public static void registrar(Scene cena) {
        ThemeService.cena = cena;
        ConfiguracaoController config = ConfiguracaoController.getInstance();
        aplicarTema(config.tema());
        aplicarFonte(config.fonte());
        aplicarDensidade(config.densidade());
    }

    public static void aplicarTema(String tema) {
        String efetivo;
        if ("escuro".equals(tema)) {
            efetivo = "escuro";
        } else if ("sistema".equals(tema)) {
            efetivo = SistemaTema.detectar();
        } else {
            efetivo = "claro";
        }
        if (cena == null) {
            temaAtual = efetivo;
            return;
        }
        cena.getStylesheets().remove(TOKENS_CLARO);
        cena.getStylesheets().remove(TOKENS_ESCURO);
        cena.getStylesheets().add(efetivo.equals("escuro") ? TOKENS_ESCURO : TOKENS_CLARO);
        temaAtual = efetivo;
    }

    public static void aplicarFonte(String fonte) {
        if (cena == null) {
            fonteAtual = fonte;
            return;
        }
        cena.getStylesheets().remove(FONTE_PEQUENA);
        cena.getStylesheets().remove(FONTE_GRANDE);
        if ("Pequena".equals(fonte)) {
            cena.getStylesheets().add(FONTE_PEQUENA);
        } else if ("Grande".equals(fonte)) {
            cena.getStylesheets().add(FONTE_GRANDE);
        }
        fonteAtual = fonte;
    }

    public static void aplicarDensidade(String densidade) {
        if (cena == null) {
            densidadeAtual = densidade;
            return;
        }
        cena.getStylesheets().remove(DENSIDADE_COMPACTA);
        if ("Compacta".equals(densidade)) {
            cena.getStylesheets().add(DENSIDADE_COMPACTA);
        }
        densidadeAtual = densidade;
    }

    public static String temaAtual() {
        return temaAtual;
    }
}