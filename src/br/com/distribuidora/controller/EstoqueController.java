package br.com.distribuidora.controller;

import br.com.distribuidora.model.Bebida;
import br.com.distribuidora.repository.ConfiguracaoStore;
import br.com.distribuidora.repository.EstoqueRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class EstoqueController {

    private static final EstoqueController INSTANCIA = new EstoqueController();

    private final EstoqueRepository estoque = EstoqueRepository.getInstance();
    private final ConfiguracaoStore config = ConfiguracaoStore.getInstance();

    private EstoqueController() {
    }

    public static EstoqueController getInstance() {
        return INSTANCIA;
    }

    public List<Bebida> listarBebidas() {
        return estoque.listar();
    }

    public void cadastrarBebida(Bebida bebida) {
        estoque.adicionar(bebida);
    }

    public void atualizarBebida(Bebida bebida) {
        estoque.atualizar(bebida);
    }

    public void excluirBebida(int id) {
        estoque.remover(id);
    }

    public List<Bebida> ultimasCadastradas(int quantidade) {
        List<Bebida> todas = estoque.listar();
        if (todas.size() <= quantidade) {
            return todas;
        }
        return List.copyOf(todas.subList(todas.size() - quantidade, todas.size()));
    }

    public List<String> categoriasOrdenadas() {
        return estoque.listar().stream()
                .map(Bebida::getCategoria)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }

    public int totalProdutos() {
        return estoque.totalProdutos();
    }

    public int totalUnidades() {
        return estoque.totalUnidades();
    }

    public int totalEstoqueBaixo() {
        return estoque.estoqueBaixo(config.getLimiteEstoqueBaixo()).size();
    }

    public List<Bebida> estoqueBaixo() {
        return estoque.estoqueBaixo(config.getLimiteEstoqueBaixo());
    }

    public BigDecimal valorTotalEstoque() {
        return estoque.valorTotalEstoque();
    }

    public int limiteEstoqueBaixo() {
        return config.getLimiteEstoqueBaixo();
    }

    public String moeda() {
        return config.getMoeda();
    }
}