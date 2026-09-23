package br.com.distribuidora.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConfiguracaoStore {

    private static final Path ARQUIVO = Paths.get(
            System.getProperty("user.home"), ".bebmais", "config.properties"
    );

    public static final List<String> FORMAS_PAGAMENTO_PADRAO = List.of(
            "Dinheiro",
            "Cartão de crédito",
            "Cartão de débito",
            "Pix",
            "Transferência bancária"
    );

    private static final ConfiguracaoStore INSTANCIA = new ConfiguracaoStore();

    private final Properties props = new Properties();

    private String nomeEmpresa = "BebMais";
    private int limiteEstoqueBaixo = 10;
    private String moeda = "R$";
    private String tema = "claro";
    private String fonte = "Média";
    private String densidade = "Confortável";
    private final List<String> formasPagamentoHabilitadas = new ArrayList<>(FORMAS_PAGAMENTO_PADRAO);
    private boolean permitirDesconto = true;
    private double limiteDescontoPercentual = 10.0;
    private boolean imprimirComprovante = false;

    private ConfiguracaoStore() {
        carregar();
    }

    public static ConfiguracaoStore getInstance() {
        return INSTANCIA;
    }

    public static Path arquivoConfiguracao() {
        return ARQUIVO;
    }

    public void carregar() {
        if (!Files.exists(ARQUIVO)) {
            return;
        }
        try (InputStream in = Files.newInputStream(ARQUIVO)) {
            props.load(in);
            nomeEmpresa = props.getProperty("empresa", "BebMais");
            limiteEstoqueBaixo = parseInt(props.getProperty("limite", "10"), 10);
            moeda = props.getProperty("moeda", "R$");
            tema = props.getProperty("tema", "claro");
            fonte = props.getProperty("fonte", "Média");
            densidade = props.getProperty("densidade", "Confortável");
            formasPagamentoHabilitadas.clear();
            String pagamentos = props.getProperty("pagamentos", null);
            if (pagamentos == null || pagamentos.isBlank()) {
                formasPagamentoHabilitadas.addAll(FORMAS_PAGAMENTO_PADRAO);
            } else {
                for (String p : pagamentos.split(",")) {
                    String nome = p.trim();
                    if (!nome.isEmpty()) {
                        formasPagamentoHabilitadas.add(nome);
                    }
                }
            }
            permitirDesconto = parseBoolean(props.getProperty("desconto", "true"), true);
            limiteDescontoPercentual = parseDouble(
                    props.getProperty("limite_desconto", "10"), 10.0);
            imprimirComprovante = parseBoolean(
                    props.getProperty("imprimir_comprovante", "false"), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void salvar() throws IOException {
        Files.createDirectories(ARQUIVO.getParent());
        props.setProperty("empresa", nomeEmpresa);
        props.setProperty("limite", String.valueOf(limiteEstoqueBaixo));
        props.setProperty("moeda", moeda);
        props.setProperty("tema", tema);
        props.setProperty("fonte", fonte);
        props.setProperty("densidade", densidade);
        props.setProperty("pagamentos", String.join(",", formasPagamentoHabilitadas));
        props.setProperty("desconto", String.valueOf(permitirDesconto));
        props.setProperty("limite_desconto", String.valueOf(limiteDescontoPercentual));
        props.setProperty("imprimir_comprovante", String.valueOf(imprimirComprovante));
        try (OutputStream out = Files.newOutputStream(ARQUIVO)) {
            props.store(out, "Configuracao BebMais");
        }
    }

    private int parseInt(String valor, int padrao) {
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return padrao;
        }
    }

    private boolean parseBoolean(String valor, boolean padrao) {
        return "true".equalsIgnoreCase(valor) ? true
                : "false".equalsIgnoreCase(valor) ? false : padrao;
    }

    private double parseDouble(String valor, double padrao) {
        try {
            return Double.parseDouble(valor.replace(",", ".").trim());
        } catch (NumberFormatException e) {
            return padrao;
        }
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public int getLimiteEstoqueBaixo() {
        return limiteEstoqueBaixo;
    }

    public void setLimiteEstoqueBaixo(int limiteEstoqueBaixo) {
        this.limiteEstoqueBaixo = limiteEstoqueBaixo;
    }

    public String getMoeda() {
        return moeda;
    }

    public void setMoeda(String moeda) {
        this.moeda = moeda;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public String getDensidade() {
        return densidade;
    }

    public void setDensidade(String densidade) {
        this.densidade = densidade;
    }

    public List<String> getFormasPagamentoHabilitadas() {
        return new ArrayList<>(formasPagamentoHabilitadas);
    }

    public void setFormasPagamentoHabilitadas(List<String> formas) {
        formasPagamentoHabilitadas.clear();
        if (formas != null) {
            formasPagamentoHabilitadas.addAll(formas);
        }
    }

    public boolean isPermitirDesconto() {
        return permitirDesconto;
    }

    public void setPermitirDesconto(boolean permitirDesconto) {
        this.permitirDesconto = permitirDesconto;
    }

    public double getLimiteDescontoPercentual() {
        return limiteDescontoPercentual;
    }

    public void setLimiteDescontoPercentual(double limiteDescontoPercentual) {
        this.limiteDescontoPercentual = limiteDescontoPercentual;
    }

    public boolean isImprimirComprovante() {
        return imprimirComprovante;
    }

    public void setImprimirComprovante(boolean imprimirComprovante) {
        this.imprimirComprovante = imprimirComprovante;
    }
}