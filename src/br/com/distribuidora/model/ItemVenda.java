package br.com.distribuidora.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ItemVenda {

    private int bebidaId;
    private String nomeBebida;
    private int quantidade;
    private BigDecimal precoUnitario;

    public ItemVenda() {
    }

    public ItemVenda(int bebidaId, String nomeBebida, int quantidade, BigDecimal precoUnitario) {
        this.bebidaId = bebidaId;
        this.nomeBebida = nomeBebida;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal subtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public int getBebidaId() {
        return bebidaId;
    }

    public void setBebidaId(int bebidaId) {
        this.bebidaId = bebidaId;
    }

    public String getNomeBebida() {
        return nomeBebida;
    }

    public void setNomeBebida(String nomeBebida) {
        this.nomeBebida = nomeBebida;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
}