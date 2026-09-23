package br.com.distribuidora.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Bebida {

    private int id;
    private String nome;
    private String marca;
    private Categoria categoria;
    private BigDecimal preco;
    private int estoque;
    private LocalDate validade;

    public Bebida() {
    }

    public Bebida(int id, String nome, Categoria categoria, BigDecimal preco, int estoque) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.preco = preco;
        this.estoque = estoque;
    }

    public Bebida(int id, String nome, String marca, Categoria categoria,
                  BigDecimal preco, int estoque) {
        this.id = id;
        this.nome = nome;
        this.marca = marca;
        this.categoria = categoria;
        this.preco = preco;
        this.estoque = estoque;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }

    public String descricao() {
        return nome + " - " + (categoria == null ? "?" : categoria.getLabel())
                + " - R$ " + preco + " - Estoque: " + estoque;
    }
}