package br.com.distribuidora.view;

import br.com.distribuidora.controller.EstoqueController;
import br.com.distribuidora.model.Bebida;
import br.com.distribuidora.util.Formatadores;
import br.com.distribuidora.view.components.PageHeader;
import br.com.distribuidora.view.components.TabelaCelulas;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class BebidasView {

    private final EstoqueController controle = EstoqueController.getInstance();
    private final Runnable onNovaBebida;
    private final Consumer<Bebida> onEditar;
    private final Consumer<String> notificar;

    public BebidasView(Runnable onNovaBebida, Consumer<Bebida> onEditar,
                       Consumer<String> notificar) {
        this.onNovaBebida = onNovaBebida;
        this.onEditar = onEditar;
        this.notificar = notificar;
    }

    public VBox getRoot() {
        List<Bebida> cadastradas = controle.listarBebidas();

        Label subtitulo = new Label();
        subtitulo.getStyleClass().add("page-subtitle");

        PageHeader cabecalho = new PageHeader(
                new Label("Bebidas"),
                subtitulo,
                novoBebida()
        );

        ObservableList<Bebida> base = FXCollections.observableArrayList(cadastradas);
        FilteredList<Bebida> filtrada = new FilteredList<>(base);
        subtitulo.setText(nomearSubtitulo(base.size()));
        base.addListener((ListChangeListener<Bebida>) c ->
                subtitulo.setText(nomearSubtitulo(base.size())));

        TextField busca = new TextField();
        busca.setPromptText("Buscar por nome, marca ou categoria");
        busca.setPrefWidth(300);
        busca.setMaxWidth(380);

        ComboBox<String> categorias = filtrarPorCategoria();
        categorias.setPrefWidth(180);

        Runnable aplicarFiltro = () -> filtrada.setPredicate(
                b -> atende(b, busca.getText(), categorias.getValue()));
        aplicarFiltro.run();
        busca.textProperty().addListener((obs, o, n) -> aplicarFiltro.run());
        categorias.valueProperty().addListener((obs, o, n) -> aplicarFiltro.run());

        TableView<Bebida> tabela = montarTabela(filtrada);

        Label rodape = new Label();
        rodape.getStyleClass().add("text-muted");
        filtrada.addListener((ListChangeListener<Bebida>) c ->
                rodape.setText(nomearRodape(filtrada.size(), base.size())));
        rodape.setText(nomearRodape(filtrada.size(), base.size()));

        tabela.setPlaceholder(placeholderVazio(base));

        tabela.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                Bebida selecionada = tabela.getSelectionModel().getSelectedItem();
                if (selecionada != null) {
                    confirmarExclusao(selecionada);
                    event.consume();
                }
            }
        });

        HBox barra = new HBox(16, busca, categorias);
        barra.setAlignment(Pos.CENTER_LEFT);

        VBox raiz = new VBox(20, cabecalho, barra, tabela, rodape);
        raiz.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(tabela, Priority.ALWAYS);

        raiz.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.N) {
                onNovaBebida.run();
                event.consume();
            }
        });

        return raiz;
    }

    private ComboBox<String> filtrarPorCategoria() {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().add("Todos");
        combo.getItems().addAll(controle.categoriasOrdenadas());
        combo.setValue("Todos");
        return combo;
    }

    private boolean atende(Bebida bebida, String texto, String categoria) {
        boolean okCategoria = categoria == null || "Todos".equals(categoria)
                || categoria.equals(bebida.getCategoria().getLabel());

        String busca = texto == null ? "" : texto.trim().toLowerCase();
        if (busca.isEmpty()) {
            return okCategoria;
        }

        String alvo = (bebida.getNome() + " " + bebida.getMarca() + " "
                + bebida.getCategoria().getLabel())
                .toLowerCase();
        return okCategoria && alvo.contains(busca);
    }

    private TableView<Bebida> montarTabela(FilteredList<Bebida> filtrada) {
        int limite = controle.limiteEstoqueBaixo();
        String moeda = controle.moeda();

        TableView<Bebida> tabela = new TableView<>(filtrada);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabela.setFixedCellSize(44);

        TableColumn<Bebida, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(d ->
                new ReadOnlyStringWrapper(Formatadores.codigo(d.getValue().getId() + 1)));
        colCodigo.setMinWidth(64);

        TableColumn<Bebida, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getNome()));
        colNome.setPrefWidth(220);

        TableColumn<Bebida, String> colCategoria = new TableColumn<>("Categoria");
        colCategoria.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getCategoria().getLabel()));
        colCategoria.setMinWidth(110);

        TableColumn<Bebida, String> colMarca = new TableColumn<>("Marca");
        colMarca.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getMarca()));
        colMarca.setMinWidth(90);

        TableColumn<Bebida, BigDecimal> colPreco = new TableColumn<>("Preço");
        colPreco.getStyleClass().add("numeric-column");
        colPreco.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getPreco()));
        colPreco.setCellFactory(col -> TabelaCelulas.preco(moeda));
        colPreco.setMinWidth(90);

        TableColumn<Bebida, Integer> colEstoque = new TableColumn<>("Estoque");
        colEstoque.getStyleClass().add("numeric-column");
        colEstoque.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getEstoque()));
        colEstoque.setCellFactory(col -> TabelaCelulas.estoque(limite));
        colEstoque.setMinWidth(110);

        TableColumn<Bebida, LocalDate> colValidade = new TableColumn<>("Validade");
        colValidade.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getValidade()));
        colValidade.setCellFactory(col -> TabelaCelulas.validade());
        colValidade.setMinWidth(100);

        TableColumn<Bebida, Void> colAcoes = new TableColumn<>("Ações");
        colAcoes.setMinWidth(84);
        colAcoes.setMaxWidth(84);
        colAcoes.setCellFactory(col -> celulaAcoes());

        tabela.getColumns().addAll(colCodigo, colNome, colCategoria, colMarca,
                colPreco, colEstoque, colValidade, colAcoes);
        return tabela;
    }

    private TableCell<Bebida, Void> celulaAcoes() {
        return new TableCell<>() {
            private final HBox caixa = new HBox(8);

            {
                Button editar = botaoIcone(FontAwesomeSolid.PEN, "Editar bebida",
                        () -> {
                            Bebida selecionada = getTableView().getItems().get(getIndex());
                            onEditar.accept(selecionada);
                        });
                Button excluir = botaoIcone(FontAwesomeSolid.TRASH_ALT, "Excluir bebida",
                        () -> {
                            Bebida selecionada = getTableView().getItems().get(getIndex());
                            confirmarExclusao(selecionada);
                        });
                excluir.getStyleClass().add("btn-danger-icon");
                caixa.getChildren().addAll(editar, excluir);
                caixa.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean vazio) {
                setGraphic(vazio ? null : caixa);
            }
        };
    }

    private void confirmarExclusao(Bebida bebida) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Excluir bebida");
        alerta.setHeaderText("Excluir " + bebida.getNome() + "?");
        alerta.setContentText("Esta ação não poderá ser desfeita. Tem certeza "
                + "de que deseja excluir esta bebida do estoque?");
        alerta.getDialogPane().getStyleClass().add("dialog-pane");

        ButtonType confirmar = new ButtonType("Excluir", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alerta.getButtonTypes().setAll(confirmar, cancelar);

        alerta.showAndWait()
                .filter(resposta -> resposta == confirmar)
                .ifPresent(resposta -> {
                    LoadingService.central("Excluindo bebida...");
                    try {
                        controle.excluirBebida(bebida.getId());
                        notificar.accept("Bebida excluída: " + bebida.getNome()
                                + " (" + Formatadores.codigo(bebida.getId() + 1) + ")");
                    } finally {
                        LoadingService.parar();
                    }
                });
    }

    private Button botaoIcone(FontAwesomeSolid codigo, String dica, Runnable acao) {
        Button botao = new Button();
        botao.getStyleClass().add("btn-icon");
        FontIcon icone = new FontIcon(codigo);
        icone.setIconSize(14);
        botao.setGraphic(icone);
        botao.setTooltip(new Tooltip(dica));
        botao.setOnAction(event -> acao.run());
        return botao;
    }

    private Button novoBebida() {
        Button botao = new Button("Nova bebida");
        botao.getStyleClass().add("btn-primary");
        FontIcon icone = new FontIcon(FontAwesomeSolid.PLUS);
        icone.setIconSize(14);
        botao.setGraphic(icone);
        botao.setOnAction(event -> onNovaBebida.run());
        return botao;
    }

    private Node placeholderVazio(ObservableList<Bebida> base) {
        FontIcon icone = new FontIcon(FontAwesomeSolid.WINE_BOTTLE);
        icone.getStyleClass().add("table-placeholder-icon");
        icone.setIconSize(28);

        Label mensagem = new Label();
        mensagem.getStyleClass().add("text-muted");

        Button nova = novoBebida();

        VBox caixa = new VBox(12, icone, mensagem, nova);
        caixa.setAlignment(Pos.CENTER);

        Runnable atualizarMensagem = () -> mensagem.setText(base.isEmpty()
                ? "Nenhuma bebida cadastrada."
                : "Nenhuma bebida encontrada para os filtros.");
        base.addListener((ListChangeListener<Bebida>) c -> atualizarMensagem.run());
        atualizarMensagem.run();

        return caixa;
    }

    private String nomearRodape(int filtrados, int total) {
        return "Mostrando " + filtrados + " de " + total + " bebidas";
    }

    private String nomearSubtitulo(int total) {
        return total == 1 ? "1 bebida cadastrada" : total + " bebidas cadastradas";
    }
}