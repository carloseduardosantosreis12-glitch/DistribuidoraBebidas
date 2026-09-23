package br.com.distribuidora.repository;

import br.com.distribuidora.model.Venda;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class VendaRepository {

    private static final Path ARQUIVO = Paths.get(
            System.getProperty("user.home"), ".bebmais", "vendas.json");

    private static final Type TIPO_VENDAS = new TypeToken<List<Venda>>() {
    }.getType();

    private static final VendaRepository INSTANCIA = new VendaRepository();

    private final List<Venda> vendas = new ArrayList<>();

    private VendaRepository() {
        List<Venda> carregadas = Persistencia.lerLista(ARQUIVO, TIPO_VENDAS);
        if (carregadas != null) {
            vendas.addAll(carregadas);
        }
    }

    public static VendaRepository getInstance() {
        return INSTANCIA;
    }

    public void adicionar(Venda venda) {
        vendas.add(venda);
        salvar();
    }

    public List<Venda> listar() {
        return new ArrayList<>(vendas);
    }

    public int proximoId() {
        int max = 0;
        for (Venda venda : vendas) {
            if (venda.getId() > max) {
                max = venda.getId();
            }
        }
        return max + 1;
    }

    private void salvar() {
        Persistencia.gravar(ARQUIVO, vendas);
    }
}