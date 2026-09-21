package br.com.distribuidora.view;

import br.com.distribuidora.model.Bebida;
import br.com.distribuidora.repository.ConfiguracaoStore;
import br.com.distribuidora.repository.EstoqueRepository;
import br.com.distribuidora.view.components.PageHeader;
import br.com.distribuidora.view.components.StatCard;
import br.com.distribuidora.view.components.StatusBadge;
import java.math.BigDecimal;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class DashboardView {

    private final EstoqueRepository estoque = EstoqueRepository.getInstance();
    private final ConfiguracaoStore config = ConfiguracaoStore.getInstance();

    public VBox getRoot() {
        PageHeader cabecalho = new PageHeader(
                "Painel de controle",
                "Visão geral do estoque da " + config.getNomeEmpresa()
        );

        int limite = config.getLimiteEstoqueBaixo();
        List<Bebida> baixo = estoque.estoqueBaixo(limite);

        HBox cards = new HBox(16,
                new StatCard("Bebidas cadastradas",
                        String.valueOf(estoque.totalProdutos())),
                new StatCard("Unidades em estoque",
                        String.valueOf(estoque.totalUnidades())),
                new StatCard("Alertas de estoque",
                        String.valueOf(baixo.size()), true),
                new StatCard("Valor do estoque",
                        formatarMoeda(estoque.valorTotalEstoque())));
        cards.getChildren().forEach(no -> {
            HBox.setHgrow(no, Priority.ALWAYS);
            ((Region) no).setMaxWidth(Double.MAX_VALUE);
        });

        VBox raiz = new VBox(24,
                cabecalho,
                cards,
                secaoEstoqueBaixo(baixo, limite),
                secaoResumoVendas());
        raiz.setMaxWidth(Double.MAX_VALUE);
        return raiz;
    }

    private VBox secaoEstoqueBaixo(List<Bebida> baixo, int limite) {
        Label titulo = new Label("Estoque baixo");
        titulo.getStyleClass().add("section-title");

        VBox lista = new VBox();
        lista.getStyleClass().add("panel");

        if (baixo.isEmpty()) {
            Label vazio = new Label("Nenhum produto abaixo do limite definido.");
            vazio.getStyleClass().add("text-muted");
            lista.getChildren().add(vazio);
        } else {
            for (Bebida b : baixo) {
                lista.getChildren().add(linhaProduto(b, limite));
            }
        }

        return new VBox(10, titulo, lista);
    }

    private HBox linhaProduto(Bebida b, int limite) {
        Label produto = new Label(b.getNome());
        produto.getStyleClass().add("row-title");

        Label detalhe = new Label(b.getMarca() + " · " + b.getCategoria());
        detalhe.getStyleClass().add("text-muted");

        HBox nome = new HBox(8, produto, detalhe);
        nome.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(nome, Priority.ALWAYS);

        Label quantidade = new Label(b.getEstoque() + " unidades");
        quantidade.getStyleClass().add("text-muted");

        StatusBadge badge = StatusBadge.paraEstoque(b.getEstoque(), limite).orElseThrow();

        HBox linha = new HBox(12, nome, quantidade, badge);
        linha.setAlignment(Pos.CENTER_LEFT);
        linha.getStyleClass().add("panel-row");
        return linha;
    }

    private VBox secaoResumoVendas() {
        Label titulo = new Label("Resumo de vendas");
        titulo.getStyleClass().add("section-title");

        Label rotulo = new Label("Vendas registradas");
        rotulo.getStyleClass().add("row-title");

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        Label valor = new Label(String.valueOf(estoque.totalVendas()));
        valor.getStyleClass().add("row-valor");

        HBox linha = new HBox(12, rotulo, espaco, valor);
        linha.getStyleClass().addAll("panel", "panel-row");

        return new VBox(10, titulo, linha);
    }

    private String formatarMoeda(BigDecimal valor) {
        return config.getMoeda() + " " + String.format("%.2f", valor).replace('.', ',');
    }
}