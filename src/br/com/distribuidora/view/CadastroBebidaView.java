package br.com.distribuidora.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CadastroBebidaView {

    private BorderPane root;

    public CadastroBebidaView() {

        root = new BorderPane();
        root.getStyleClass().add("cadastro-root");

        // =========================
        // CONTEÚDO PRINCIPAL
        // =========================

        VBox pagina = new VBox(25);
        pagina.setPadding(new Insets(35));

        // =========================
        // CABEÇALHO
        // =========================

        Label titulo = new Label("Cadastrar Bebida");
        titulo.getStyleClass().add("cadastro-titulo");

        Label subtitulo = new Label(
                "Adicione uma nova bebida ao estoque da distribuidora"
        );
        subtitulo.getStyleClass().add("cadastro-subtitulo");

        VBox cabecalho = new VBox(5);
        cabecalho.getChildren().addAll(titulo, subtitulo);

        // =========================
        // CARD DO FORMULÁRIO
        // =========================

        VBox cardFormulario = new VBox(22);
        cardFormulario.getStyleClass().add("cadastro-card");

        Label tituloFormulario = new Label("Informações da Bebida");
        tituloFormulario.getStyleClass().add("formulario-titulo");

        Label descricaoFormulario = new Label(
                "Preencha os dados abaixo para cadastrar o produto."
        );
        descricaoFormulario.getStyleClass().add("formulario-descricao");

        VBox topoFormulario = new VBox(4);
        topoFormulario.getChildren().addAll(
                tituloFormulario,
                descricaoFormulario
        );

        // =========================
        // GRID
        // =========================

        GridPane formulario = new GridPane();

        formulario.setHgap(25);
        formulario.setVgap(20);

        ColumnConstraints coluna1 = new ColumnConstraints();
        coluna1.setPercentWidth(50);
        coluna1.setHgrow(Priority.ALWAYS);

        ColumnConstraints coluna2 = new ColumnConstraints();
        coluna2.setPercentWidth(50);
        coluna2.setHgrow(Priority.ALWAYS);

        formulario.getColumnConstraints().addAll(
                coluna1,
                coluna2
        );

        // =========================
        // NOME
        // =========================

        Label labelNome = new Label("Nome da bebida");
        labelNome.getStyleClass().add("campo-label");

        TextField campoNome = new TextField();
        campoNome.setPromptText("Ex.: Coca-Cola 2L");
        campoNome.getStyleClass().add("campo-formulario");
        campoNome.setMaxWidth(Double.MAX_VALUE);

        VBox grupoNome = criarGrupoCampo(
                labelNome,
                campoNome
        );

        // =========================
        // MARCA
        // =========================

        Label labelMarca = new Label("Marca");
        labelMarca.getStyleClass().add("campo-label");

        TextField campoMarca = new TextField();
        campoMarca.setPromptText("Ex.: Coca-Cola");
        campoMarca.getStyleClass().add("campo-formulario");
        campoMarca.setMaxWidth(Double.MAX_VALUE);

        VBox grupoMarca = criarGrupoCampo(
                labelMarca,
                campoMarca
        );

        // =========================
        // CATEGORIA
        // =========================

        Label labelCategoria = new Label("Categoria");
        labelCategoria.getStyleClass().add("campo-label");

        ComboBox<String> campoCategoria = new ComboBox<>();

        campoCategoria.getItems().addAll(
                "Refrigerante",
                "Água",
                "Suco",
                "Cerveja",
                "Energético",
                "Vinho",
                "Destilado",
                "Outro"
        );

        campoCategoria.setPromptText("Selecione uma categoria");
        campoCategoria.getStyleClass().add("campo-formulario");
        campoCategoria.setMaxWidth(Double.MAX_VALUE);

        VBox grupoCategoria = criarGrupoCampo(
                labelCategoria,
                campoCategoria
        );

        // =========================
        // PREÇO
        // =========================

        Label labelPreco = new Label("Preço");
        labelPreco.getStyleClass().add("campo-label");

        TextField campoPreco = new TextField();
        campoPreco.setPromptText("Ex.: 9,90");
        campoPreco.getStyleClass().add("campo-formulario");
        campoPreco.setMaxWidth(Double.MAX_VALUE);

        VBox grupoPreco = criarGrupoCampo(
                labelPreco,
                campoPreco
        );

        // =========================
        // QUANTIDADE
        // =========================

        Label labelQuantidade = new Label("Quantidade em estoque");
        labelQuantidade.getStyleClass().add("campo-label");

        TextField campoQuantidade = new TextField();
        campoQuantidade.setPromptText("Ex.: 120");
        campoQuantidade.getStyleClass().add("campo-formulario");
        campoQuantidade.setMaxWidth(Double.MAX_VALUE);

        VBox grupoQuantidade = criarGrupoCampo(
                labelQuantidade,
                campoQuantidade
        );

        // =========================
        // VALIDADE
        // =========================

        Label labelValidade = new Label("Data de validade");
        labelValidade.getStyleClass().add("campo-label");

        DatePicker campoValidade = new DatePicker();
        campoValidade.getStyleClass().add("campo-formulario");
        campoValidade.setMaxWidth(Double.MAX_VALUE);

        VBox grupoValidade = criarGrupoCampo(
                labelValidade,
                campoValidade
        );

        // =========================
        // POSIÇÕES
        // =========================

        formulario.add(grupoNome, 0, 0);
        formulario.add(grupoMarca, 1, 0);

        formulario.add(grupoCategoria, 0, 1);
        formulario.add(grupoPreco, 1, 1);

        formulario.add(grupoQuantidade, 0, 2);
        formulario.add(grupoValidade, 1, 2);

        // =========================
        // BOTÕES
        // =========================

        Button limpar = new Button("Limpar");
        limpar.getStyleClass().add("botao-secundario");

        Button cadastrar = new Button("Cadastrar Bebida");
        cadastrar.getStyleClass().add("botao-principal");

        HBox botoes = new HBox(12);
        botoes.getStyleClass().add("cadastro-botoes");

        botoes.getChildren().addAll(
                limpar,
                cadastrar
        );

        // =========================
        // LIMPAR CAMPOS
        // =========================

        limpar.setOnAction(event -> {

            campoNome.clear();
            campoMarca.clear();
            campoCategoria.getSelectionModel().clearSelection();
            campoPreco.clear();
            campoQuantidade.clear();
            campoValidade.setValue(null);

        });

        // =========================
        // MONTAGEM
        // =========================

        cardFormulario.getChildren().addAll(
                topoFormulario,
                formulario,
                botoes
        );

        pagina.getChildren().addAll(
                cabecalho,
                cardFormulario
        );

        root.setCenter(pagina);
    }

    private VBox criarGrupoCampo(
            Label label,
            javafx.scene.Node campo) {

        VBox grupo = new VBox(8);

        grupo.getChildren().addAll(
                label,
                campo
        );

        return grupo;
    }

    public BorderPane getRoot() {
        return root;
    }
}