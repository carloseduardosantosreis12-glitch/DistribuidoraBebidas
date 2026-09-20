package br.com.distribuidora.model;

public class Bebida {

    private int id;
    private String nome;
    private String categoria;
    private double preco;
    private int estoque;

    public Bebida() {
    }

    public Bebida(int id, String nome, String categoria, double preco, int estoque) {
        this.id = id;
        this.nome = nome;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }
    
    public String descricao() {
        return nome + " - " + categoria + " - R$ " + preco + " - Estoque: " + estoque;
    }
}