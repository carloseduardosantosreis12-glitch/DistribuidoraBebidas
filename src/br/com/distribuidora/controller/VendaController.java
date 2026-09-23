package br.com.distribuidora.controller;

import br.com.distribuidora.model.Bebida;
import br.com.distribuidora.model.ItemVenda;
import br.com.distribuidora.model.Venda;
import br.com.distribuidora.repository.ConfiguracaoStore;
import br.com.distribuidora.repository.EstoqueRepository;
import br.com.distribuidora.repository.VendaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VendaController {

    private static final VendaController INSTANCIA = new VendaController();

    private final VendaRepository vendas = VendaRepository.getInstance();
    private final ConfiguracaoStore config = ConfiguracaoStore.getInstance();
    private final EstoqueRepository estoque = EstoqueRepository.getInstance();

    private VendaController() {
    }

    public static VendaController getInstance() {
        return INSTANCIA;
    }

    public List<String> formasPagamentoDisponiveis() {
        return config.getFormasPagamentoHabilitadas();
    }

    public boolean permitirDesconto() {
        return config.isPermitirDesconto();
    }

    public double limiteDescontoPercentual() {
        return config.getLimiteDescontoPercentual();
    }

    public boolean imprimirComprovante() {
        return config.isImprimirComprovante();
    }

    public Venda registrarVenda(List<ItemVenda> itens, String formaPagamento,
                                double descontoPercentual) {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Adicione ao menos um item ao carrinho.");
        }
        for (ItemVenda item : itens) {
            if (item.getQuantidade() <= 0) {
                throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
            }
            Bebida bebida = estoque.buscarPorId((int) item.getBebidaId());
            if (bebida == null) {
                throw new IllegalArgumentException(
                        "Bebida \"" + item.getNomeBebida() + "\" não encontrada no estoque.");
            }
            if (item.getQuantidade() > bebida.getEstoque()) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para \"" + item.getNomeBebida()
                                + "\" (disponível: " + bebida.getEstoque() + ").");
            }
        }

        if (formaPagamento == null || formaPagamento.isBlank()
                || !config.getFormasPagamentoHabilitadas().contains(formaPagamento)) {
            throw new IllegalArgumentException(
                    "Selecione uma forma de pagamento habilitada nas configurações.");
        }

        if (descontoPercentual < 0) {
            throw new IllegalArgumentException("O desconto não pode ser negativo.");
        }
        if (descontoPercentual > 0 && !config.isPermitirDesconto()) {
            throw new IllegalArgumentException(
                    "O desconto não está habilitado nas configurações de vendas.");
        }
        if (descontoPercentual > config.getLimiteDescontoPercentual()) {
            throw new IllegalArgumentException(
                    "Desconto acima do limite configurado ("
                            + exibirLimite(config.getLimiteDescontoPercentual()) + "%).");
        }

        Venda venda = new Venda(
                vendas.proximoId(),
                LocalDateTime.now(),
                itens,
                formaPagamento,
                descontoPercentual,
                "Concluída"
        );
        vendas.adicionar(venda);

        for (ItemVenda item : itens) {
            Bebida original = estoque.buscarPorId((int) item.getBebidaId());
            Bebida atualizada = new Bebida(
                    original.getId(),
                    original.getNome(),
                    original.getMarca(),
                    original.getCategoria(),
                    original.getPreco(),
                    original.getEstoque() - item.getQuantidade()
            );
            estoque.atualizar(atualizada);
        }

        return venda;
    }

    public List<Venda> listarVendas() {
        return vendas.listar();
    }

    private String exibirLimite(double limite) {
        if (limite == Math.floor(limite)) {
            return String.valueOf((long) limite);
        }
        return String.valueOf(limite).replace('.', ',');
    }
}