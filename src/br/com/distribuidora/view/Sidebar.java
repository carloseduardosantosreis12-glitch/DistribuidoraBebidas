package br.com.distribuidora.view;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class Sidebar extends VBox {

    public enum Item {
        INICIO("Início", FontAwesomeSolid.HOME),
        BEBIDAS("Bebidas", FontAwesomeSolid.WINE_BOTTLE),
        CADASTRAR("Cadastrar bebida", FontAwesomeSolid.PLUS),
        VENDAS("Vendas", FontAwesomeSolid.SHOPPING_CART),
        RELATORIOS("Relatórios", FontAwesomeSolid.CHART_BAR),
        CONFIGURACOES("Configurações", FontAwesomeSolid.COGS);

        private final String rotulo;
        private final FontAwesomeSolid icone;

        Item(String rotulo, FontAwesomeSolid icone) {
            this.rotulo = rotulo;
            this.icone = icone;
        }
    }

    private final Map<Item, HBox> itens = new EnumMap<>(Item.class);
    private Label tituloEmpresa;

    public Sidebar(Consumer<Item> onNavigate) {
        getStyleClass().add("sidebar");
        setMinWidth(220);
        setPrefWidth(220);
        setMaxWidth(220);

        getChildren().add(criarMarca());

        for (Item item : Item.values()) {
            HBox linha = criarItem(item);
            linha.setOnMouseClicked(evento -> onNavigate.accept(item));
            itens.put(item, linha);
            getChildren().add(linha);
        }

        Region espaco = new Region();
        VBox.setVgrow(espaco, Priority.ALWAYS);
        getChildren().add(espaco);

        Label rodape = new Label("Dados mantidos em memória");
        rodape.getStyleClass().add("sidebar-footer");
        getChildren().add(rodape);
    }

    public void setActive(Item ativo) {
        itens.forEach((item, linha) -> {
            linha.getStyleClass().remove("active");
            if (item == ativo) {
                linha.getStyleClass().add("active");
            }
        });
    }

    public void setNomeEmpresa(String nome) {
        tituloEmpresa.setText(nome);
    }

    private HBox criarMarca() {
        FontIcon icone = new FontIcon(FontAwesomeSolid.WINE_BOTTLE);
        icone.getStyleClass().add("brand-icon");

        tituloEmpresa = new Label("BebMais");
        tituloEmpresa.getStyleClass().add("brand-title");

        Label legenda = new Label("Distribuidora de Bebidas");
        legenda.getStyleClass().add("brand-subtitle");

        HBox marca = new HBox(12, icone, new VBox(2, tituloEmpresa, legenda));
        marca.setAlignment(Pos.CENTER_LEFT);
        marca.getStyleClass().add("brand");
        return marca;
    }

    private HBox criarItem(Item item) {
        FontIcon icone = new FontIcon(item.icone);
        icone.getStyleClass().add("nav-icon");

        Label texto = new Label(item.rotulo);
        texto.getStyleClass().add("nav-label");

        HBox linha = new HBox(12, icone, texto);
        linha.setAlignment(Pos.CENTER_LEFT);
        linha.getStyleClass().add("nav-item");
        return linha;
    }
}
