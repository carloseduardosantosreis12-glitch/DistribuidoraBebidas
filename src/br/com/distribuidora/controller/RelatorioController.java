package br.com.distribuidora.controller;

import br.com.distribuidora.model.Bebida;
import br.com.distribuidora.repository.ConfiguracaoStore;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class RelatorioController {

    private static final RelatorioController INSTANCIA = new RelatorioController();

    private final ConfiguracaoStore config = ConfiguracaoStore.getInstance();

    private final List<VendaMock> vendas = List.of(
            new VendaMock("02/01/2026", "V-1001", "Bar do Zé", "Carlos", 5, "Pix", "187,50", "Concluída"),
            new VendaMock("03/01/2026", "V-1002", "Mercado Central", "Ana", 12, "Cartão de crédito", "540,00", "Concluída"),
            new VendaMock("05/01/2026", "V-1003", "Churrascaria Gaúcha", "Carlos", 3, "Dinheiro", "89,90", "Pendente"),
            new VendaMock("08/01/2026", "V-1004", "Conveniência Estrela", "Ana", 8, "Pix", "320,40", "Concluída"),
            new VendaMock("10/01/2026", "V-1005", "Bar do Zé", "Paulo", 15, "Transferência", "950,00", "Cancelada"),
            new VendaMock("12/01/2026", "V-1006", "Restaurante Sabor", "Paulo", 20, "Cartão de débito", "1230,75", "Concluída"),
            new VendaMock("15/01/2026", "V-1007", "Mercado Central", "Ana", 6, "Pix", "275,30", "Pendente"),
            new VendaMock("18/01/2026", "V-1008", "Depósito do Nando", "Carlos", 30, "Boleto", "2800,00", "Concluída")
    );

    private final List<FinanceiroMock> financeiro = List.of(
            new FinanceiroMock("02/01/2026", "Venda à vista", "Receita", "Vendas", "2350,00"),
            new FinanceiroMock("03/01/2026", "Vendas cartão", "Receita", "Vendas", "4860,00"),
            new FinanceiroMock("04/01/2026", "Vendas pix", "Receita", "Vendas", "5270,00"),
            new FinanceiroMock("05/01/2026", "Compra de bebidas", "Despesa", "Compras", "3850,00"),
            new FinanceiroMock("06/01/2026", "Folha de pagamento", "Despesa", "Pessoal", "3120,00"),
            new FinanceiroMock("07/01/2026", "Energia e água", "Despesa", "Operacional", "890,00"),
            new FinanceiroMock("08/01/2026", "Combustível", "Despesa", "Logística", "850,00")
    );

    private final List<CompraMock> compras = List.of(
            new CompraMock("02/01/2026", "C-2001", "Distribuidora Ambev", 12, "1850,00", "Concluída"),
            new CompraMock("05/01/2026", "C-2002", "Coca-Cola Femsa", 9, "2380,00", "Concluída"),
            new CompraMock("09/01/2026", "C-2003", "Casa Valduga", 4, "1420,00", "Pendente"),
            new CompraMock("12/01/2026", "C-2004", "Red Bull Brasil", 3, "990,00", "Concluída"),
            new CompraMock("16/01/2026", "C-2005", "Dell Vale", 6, "610,00", "Cancelada")
    );

    private RelatorioController() {
    }

    public static RelatorioController getInstance() {
        return INSTANCIA;
    }

    public String moeda() {
        return config.getMoeda();
    }

    public List<VendaMock> listarVendas() {
        return vendas;
    }

    public List<FinanceiroMock> listarFinanceiro() {
        return financeiro;
    }

    public List<CompraMock> listarCompras() {
        return compras;
    }

    public List<EstoqueMock> estoque() {
        List<EstoqueMock> registros = new ArrayList<>();
        for (Bebida b : EstoqueController.getInstance().listarBebidas()) {
            registros.add(new EstoqueMock(
                    String.valueOf(b.getId() + 1),
                    b.getNome(),
                    b.getCategoria().getLabel(),
                    b.getEstoque(),
                    config.getLimiteEstoqueBaixo(),
                    b.getPreco(),
                    statusEstoque(b.getEstoque())));
        }
        return registros;
    }

    public List<ProdutoMock> produtos() {
        List<ProdutoMock> registros = new ArrayList<>();
        for (Bebida b : EstoqueController.getInstance().listarBebidas()) {
            registros.add(new ProdutoMock(
                    String.valueOf(b.getId() + 1),
                    b.getNome(),
                    b.getCategoria().getLabel(),
                    b.getMarca(),
                    b.getPreco().multiply(new BigDecimal("0.60")),
                    b.getPreco(),
                    b.getEstoque(),
                    statusEstoque(b.getEstoque())));
        }
        return registros;
    }

    private String statusEstoque(int quantidade) {
        if (quantidade <= 0) {
            return "Sem estoque";
        }
        return quantidade <= config.getLimiteEstoqueBaixo() ? "Estoque baixo" : "Normal";
    }

    public int totalVendas() {
        return vendas.size();
    }

    public BigDecimal valorTotalVendas() {
        BigDecimal total = BigDecimal.ZERO;
        for (VendaMock venda : vendas) {
            total = total.add(venda.getValor());
        }
        return total;
    }

    public int produtosVendidos() {
        int total = 0;
        for (VendaMock venda : vendas) {
            total += venda.getQuantidadeItens();
        }
        return total;
    }

    public BigDecimal ticketMedio() {
        if (vendas.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return valorTotalVendas().divide(
                BigDecimal.valueOf(vendas.size()), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal receitas() {
        return somarTipo("Receita");
    }

    public BigDecimal despesas() {
        return somarTipo("Despesa");
    }

    public BigDecimal saldo() {
        return receitas().subtract(despesas());
    }

    private BigDecimal somarTipo(String tipo) {
        BigDecimal soma = BigDecimal.ZERO;
        for (FinanceiroMock lancamento : financeiro) {
            if (tipo.equals(lancamento.getTipo())) {
                soma = soma.add(lancamento.getValor());
            }
        }
        return soma;
    }

    private static BigDecimal toBigDecimal(String valor) {
        return new BigDecimal(valor.replace(",", "."));
    }

    public static class VendaMock {
        private final String data;
        private final String numero;
        private final String cliente;
        private final String vendedor;
        private final int quantidadeItens;
        private final String pagamento;
        private final BigDecimal valor;
        private final String status;

        public VendaMock(String data, String numero, String cliente, String vendedor,
                         int quantidadeItens, String pagamento, String valor, String status) {
            this.data = data;
            this.numero = numero;
            this.cliente = cliente;
            this.vendedor = vendedor;
            this.quantidadeItens = quantidadeItens;
            this.pagamento = pagamento;
            this.valor = toBigDecimal(valor);
            this.status = status;
        }

        public String getData() {
            return data;
        }

        public String getNumero() {
            return numero;
        }

        public String getCliente() {
            return cliente;
        }

        public String getVendedor() {
            return vendedor;
        }

        public int getQuantidadeItens() {
            return quantidadeItens;
        }

        public String getPagamento() {
            return pagamento;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class EstoqueMock {
        private final String codigo;
        private final String produto;
        private final String categoria;
        private final int estoqueAtual;
        private final int estoqueMinimo;
        private final BigDecimal preco;
        private final String status;

        public EstoqueMock(String codigo, String produto, String categoria,
                           int estoqueAtual, int estoqueMinimo, BigDecimal preco, String status) {
            this.codigo = codigo;
            this.produto = produto;
            this.categoria = categoria;
            this.estoqueAtual = estoqueAtual;
            this.estoqueMinimo = estoqueMinimo;
            this.preco = preco;
            this.status = status;
        }

        public String getCodigo() {
            return codigo;
        }

        public String getProduto() {
            return produto;
        }

        public String getCategoria() {
            return categoria;
        }

        public int getEstoqueAtual() {
            return estoqueAtual;
        }

        public int getEstoqueMinimo() {
            return estoqueMinimo;
        }

        public BigDecimal getPreco() {
            return preco;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class ProdutoMock {
        private final String codigo;
        private final String produto;
        private final String categoria;
        private final String marca;
        private final BigDecimal precoCompra;
        private final BigDecimal precoVenda;
        private final int estoque;
        private final String status;

        public ProdutoMock(String codigo, String produto, String categoria, String marca,
                           BigDecimal precoCompra, BigDecimal precoVenda, int estoque, String status) {
            this.codigo = codigo;
            this.produto = produto;
            this.categoria = categoria;
            this.marca = marca;
            this.precoCompra = precoCompra;
            this.precoVenda = precoVenda;
            this.estoque = estoque;
            this.status = status;
        }

        public String getCodigo() {
            return codigo;
        }

        public String getProduto() {
            return produto;
        }

        public String getCategoria() {
            return categoria;
        }

        public String getMarca() {
            return marca;
        }

        public BigDecimal getPrecoCompra() {
            return precoCompra;
        }

        public BigDecimal getPrecoVenda() {
            return precoVenda;
        }

        public int getEstoque() {
            return estoque;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class FinanceiroMock {
        private final String data;
        private final String descricao;
        private final String tipo;
        private final String categoria;
        private final BigDecimal valor;

        public FinanceiroMock(String data, String descricao, String tipo,
                              String categoria, String valor) {
            this.data = data;
            this.descricao = descricao;
            this.tipo = tipo;
            this.categoria = categoria;
            this.valor = toBigDecimal(valor);
        }

        public String getData() {
            return data;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getTipo() {
            return tipo;
        }

        public String getCategoria() {
            return categoria;
        }

        public BigDecimal getValor() {
            return valor;
        }
    }

    public static class CompraMock {
        private final String data;
        private final String numero;
        private final String fornecedor;
        private final int quantidadeProdutos;
        private final BigDecimal valor;
        private final String status;

        public CompraMock(String data, String numero, String fornecedor,
                          int quantidadeProdutos, String valor, String status) {
            this.data = data;
            this.numero = numero;
            this.fornecedor = fornecedor;
            this.quantidadeProdutos = quantidadeProdutos;
            this.valor = toBigDecimal(valor);
            this.status = status;
        }

        public String getData() {
            return data;
        }

        public String getNumero() {
            return numero;
        }

        public String getFornecedor() {
            return fornecedor;
        }

        public int getQuantidadeProdutos() {
            return quantidadeProdutos;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public String getStatus() {
            return status;
        }
    }
}