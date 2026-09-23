package br.com.distribuidora.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfiguracaoStore {

    private static final Path ARQUIVO = Paths.get(
            System.getProperty("user.home"), ".bebmais", "config.properties"
    );

    private static final ConfiguracaoStore INSTANCIA = new ConfiguracaoStore();

    private final Properties props = new Properties();

    private String nomeEmpresa = "BebMais";
    private int limiteEstoqueBaixo = 10;
    private String moeda = "R$";
    private String tema = "claro";
    private String fonte = "Média";
    private String densidade = "Confortável";

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
}