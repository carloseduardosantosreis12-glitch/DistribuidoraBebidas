package br.com.distribuidora.repository;

import br.com.distribuidora.model.Bebida;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EstoqueRepository {

    private static final EstoqueRepository INSTANCIA = new EstoqueRepository();

    private final List<Bebida> bebidas = new ArrayList<>();
    private int totalVendas = 25;

    private EstoqueRepository() {
        int id = 0;

        bebidas.add(nova(id++, "Coca-Cola 2L", "Coca-Cola", "Refrigerante", "7,50", 45));
        bebidas.add(nova(id++, "Guaraná Antarctica 2L", "Antarctica", "Refrigerante", "6,90", 38));
        bebidas.add(nova(id++, "Fanta Laranja 2L", "Coca-Cola", "Refrigerante", "6,50", 22));
        bebidas.add(nova(id++, "Água Mineral 500ml", "Crystal", "Água", "2,50", 60));
        bebidas.add(nova(id++, "Suco de Laranja 1L", "Dell Vale", "Suco", "9,90", 5));
        bebidas.add(nova(id++, "Cerveja Brahma 350ml", "Ambev", "Cerveja", "3,90", 4));
        bebidas.add(nova(id++, "Energético Red Bull 250ml", "Red Bull", "Energético", "12,50", 2));
        bebidas.add(nova(id++, "Vinho Tinto Seco 750ml", "Casa Valduga", "Vinho", "45,00", 12));
        bebidas.add(nova(id++, "Vodka Absolut 1L", "Absolut", "Destilado", "120,00", 3));
    }

    private Bebida nova(int id, String nome, String marca, String categoria,
                        String preco, int estoque) {
        return new Bebida(id, nome, marca, categoria,
                new BigDecimal(preco.replace(",", ".")), estoque);
    }

    public static EstoqueRepository getInstance() {
        return INSTANCIA;
    }

    public List<Bebida> listar() {
        return new ArrayList<>(bebidas);
    }

    public void adicionar(Bebida bebida) {
        bebida.setId(proximoId());
        bebidas.add(bebida);
    }

    public void atualizar(Bebida atualizada) {
        for (int i = 0; i < bebidas.size(); i++) {
            if (bebidas.get(i).getId() == atualizada.getId()) {
                bebidas.set(i, atualizada);
                return;
            }
        }
    }

    public void remover(int id) {
        bebidas.removeIf(bebida -> bebida.getId() == id);
    }

    public Bebida buscarPorId(int id) {
        for (Bebida bebida : bebidas) {
            if (bebida.getId() == id) {
                return bebida;
            }
        }
        return null;
    }

    private int proximoId() {
        int max = 0;
        for (Bebida b : bebidas) {
            if (b.getId() > max) {
                max = b.getId();
            }
        }
        return max + 1;
    }

    public int totalProdutos() {
        return bebidas.size();
    }

    public int totalUnidades() {
        int total = 0;
        for (Bebida b : bebidas) {
            total += b.getEstoque();
        }
        return total;
    }

    public BigDecimal valorTotalEstoque() {
        BigDecimal total = BigDecimal.ZERO;
        for (Bebida b : bebidas) {
            total = total.add(b.getPreco().multiply(BigDecimal.valueOf(b.getEstoque())));
        }
        return total;
    }

    public List<Bebida> estoqueBaixo(int limite) {
        List<Bebida> resultado = new ArrayList<>();
        for (Bebida b : bebidas) {
            if (b.getEstoque() <= limite) {
                resultado.add(b);
            }
        }
        return resultado;
    }

    public int totalVendas() {
        return totalVendas;
    }
}