package br.com.distribuidora.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venda {

    private int id;
    private LocalDateTime dataHora;
    private List<ItemVenda> itens = new ArrayList<>();
    private String formaPagamento;
    private double descontoPercentual;
    private String status;

    public Venda() {
    }

    public Venda(int id, LocalDateTime dataHora, List<ItemVenda> itens,
                 String formaPagamento, double descontoPercentual, String status) {
        this.id = id;
        this.dataHora = dataHora;
        this.itens = new ArrayList<>(itens);
        this.formaPagamento = formaPagamento;
        this.descontoPercentual = descontoPercentual;
        this.status = status;
    }

    public BigDecimal subtotalBruto() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemVenda item : itens) {
            total = total.add(item.subtotal());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal valorDesconto() {
        return subtotalBruto()
                .multiply(BigDecimal.valueOf(descontoPercentual))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal valorTotal() {
        return subtotalBruto().subtract(valorDesconto());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens == null ? new ArrayList<>() : new ArrayList<>(itens);
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public double getDescontoPercentual() {
        return descontoPercentual;
    }

    public void setDescontoPercentual(double descontoPercentual) {
        this.descontoPercentual = descontoPercentual;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}