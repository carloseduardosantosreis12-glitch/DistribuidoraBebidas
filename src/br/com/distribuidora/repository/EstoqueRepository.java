package br.com.distribuidora.repository;

import br.com.distribuidora.model.Bebida;
import br.com.distribuidora.model.Categoria;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EstoqueRepository {

    private static final Path ARQUIVO = Paths.get(
            System.getProperty("user.home"), ".bebmais", "estoque.json");

    private static final Type TIPO_ESTOQUE = new TypeToken<List<Bebida>>() {
    }.getType();

    private static final EstoqueRepository INSTANCIA = new EstoqueRepository();

    private final List<Bebida> bebidas = new ArrayList<>();

    private EstoqueRepository() {
        List<Bebida> carregadas = Persistencia.lerLista(ARQUIVO, TIPO_ESTOQUE);
        if (carregadas != null && !carregadas.isEmpty()) {
            bebidas.addAll(carregadas);
            return;
        }

        int id = 0;

        bebidas.add(nova(id++, "Coca-Cola 2L", "Coca-Cola", Categoria.REFRIGERANTE, "7,50", 45));
        bebidas.add(nova(id++, "Guaraná Antarctica 2L", "Antarctica", Categoria.REFRIGERANTE, "6,90", 38));
        bebidas.add(nova(id++, "Fanta Laranja 2L", "Coca-Cola", Categoria.REFRIGERANTE, "6,50", 22));
        bebidas.add(nova(id++, "Água Mineral 500ml", "Crystal", Categoria.AGUA, "2,50", 60));
        bebidas.add(nova(id++, "Suco de Laranja 1L", "Dell Vale", Categoria.SUCO, "9,90", 5));
        bebidas.add(nova(id++, "Cerveja Brahma 350ml", "Ambev", Categoria.CERVEJA, "3,90", 4));
        bebidas.add(nova(id++, "Energético Red Bull 250ml", "Red Bull", Categoria.ENERGETICO, "12,50", 2));
        bebidas.add(nova(id++, "Vinho Tinto Seco 750ml", "Casa Valduga", Categoria.VINHO, "45,00", 12));
        bebidas.add(nova(id++, "Vodka Absolut 1L", "Absolut", Categoria.DESTILADO, "120,00", 3));

        salvar();
    }

    private Bebida nova(int id, String nome, String marca, Categoria categoria,
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
        salvar();
    }

    public void atualizar(Bebida atualizada) {
        for (int i = 0; i < bebidas.size(); i++) {
            if (bebidas.get(i).getId() == atualizada.getId()) {
                bebidas.set(i, atualizada);
                salvar();
                return;
            }
        }
    }

    public void remover(int id) {
        bebidas.removeIf(bebida -> bebida.getId() == id);
        salvar();
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

    private void salvar() {
        Persistencia.gravar(ARQUIVO, bebidas);
    }
}