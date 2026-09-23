package br.com.distribuidora.controller;

import br.com.distribuidora.repository.ConfiguracaoStore;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfiguracaoController {

    private static final ConfiguracaoController INSTANCIA = new ConfiguracaoController();

    private final ConfiguracaoStore config = ConfiguracaoStore.getInstance();

    private ConfiguracaoController() {
    }

    public static ConfiguracaoController getInstance() {
        return INSTANCIA;
    }

    public String nomeEmpresa() {
        return config.getNomeEmpresa();
    }

    public String moeda() {
        return config.getMoeda();
    }

    public int limiteEstoqueBaixo() {
        return config.getLimiteEstoqueBaixo();
    }

    public void salvarNomeEmpresa(String nome) throws IOException {
        config.setNomeEmpresa(nome);
        config.salvar();
    }

    public void salvarLimiteEstoqueBaixo(int limite) throws IOException {
        config.setLimiteEstoqueBaixo(limite);
        config.salvar();
    }

    public void salvarMoeda(String moeda) throws IOException {
        config.setMoeda(moeda);
        config.salvar();
    }

    public String tema() {
        return config.getTema();
    }

    public void salvarTema(String tema) throws IOException {
        config.setTema(tema);
        config.salvar();
    }

    public String fonte() {
        return config.getFonte();
    }

    public void salvarFonte(String fonte) throws IOException {
        config.setFonte(fonte);
        config.salvar();
    }

    public String densidade() {
        return config.getDensidade();
    }

    public void salvarDensidade(String densidade) throws IOException {
        config.setDensidade(densidade);
        config.salvar();
    }

    public List<String> formasPagamentoPadrao() {
        return new ArrayList<>(ConfiguracaoStore.FORMAS_PAGAMENTO_PADRAO);
    }

    public List<String> formasPagamentoHabilitadas() {
        return config.getFormasPagamentoHabilitadas();
    }

    public void salvarFormasPagamento(List<String> formas) throws IOException {
        config.setFormasPagamentoHabilitadas(formas);
        config.salvar();
    }

    public boolean permitirDesconto() {
        return config.isPermitirDesconto();
    }

    public void salvarPermitirDesconto(boolean permitir) throws IOException {
        config.setPermitirDesconto(permitir);
        config.salvar();
    }

    public double limiteDescontoPercentual() {
        return config.getLimiteDescontoPercentual();
    }

    public void salvarLimiteDescontoPercentual(double limite) throws IOException {
        config.setLimiteDescontoPercentual(limite);
        config.salvar();
    }

    public boolean imprimirComprovante() {
        return config.isImprimirComprovante();
    }

    public void salvarImprimirComprovante(boolean imprimir) throws IOException {
        config.setImprimirComprovante(imprimir);
        config.salvar();
    }
}