package br.com.distribuidora.view;

import br.com.distribuidora.controller.EstoqueController;
import br.com.distribuidora.model.Bebida;
import br.com.distribuidora.view.components.PageHeader;
import java.math.BigDecimal;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CadastroBebidaView {

    private final Consumer<String> notificar;

    public CadastroBebidaView(Consumer<String> notificar) {
        this.notificar = notificar;
    }

    public VBox getRoot() {
        PageHeader cabecalho = new PageHeader(
                "Cadastrar Bebida",
                "Adicione uma nova bebida ao estoque da distribuidora"
        );

        Label tituloFormulario = new Label("Informações da Bebida");
        tituloFormulario.getStyleClass().add("section-title");

        Label descricaoFormulario = new Label(
                "Preencha os dados abaixo para cadastrar o produto."
        );
        descricaoFormulario.getStyleClass().add("field-help");

        VBox topoFormulario = new VBox(4, tituloFormulario, descricaoFormulario);

        GridPane formulario = new GridPane();
        formulario.setHgap(24);
        formulario.setVgap(16);

        ColumnConstraints coluna1 = new ColumnConstraints();
        coluna1.setPercentWidth(50);
        coluna1.setHgrow(Priority.ALWAYS);

        ColumnConstraints coluna2 = new ColumnConstraints();
        coluna2.setPercentWidth(50);
        coluna2.setHgrow(Priority.ALWAYS);

        formulario.getColumnConstraints().addAll(coluna1, coluna2);

        TextField campoNome = new TextField();
        campoNome.setPromptText("Ex.: Coca-Cola 2L");
        campoNome.setMaxWidth(Double.MAX_VALUE);

        TextField campoMarca = new TextField();
        campoMarca.setPromptText("Ex.: Coca-Cola");
        campoMarca.setMaxWidth(Double.MAX_VALUE);

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
        campoCategoria.setMaxWidth(Double.MAX_VALUE);

        TextField campoPreco = new TextField();
        campoPreco.setPromptText("Ex.: 9,90");
        campoPreco.setMaxWidth(Double.MAX_VALUE);

        TextField campoQuantidade = new TextField();
        campoQuantidade.setPromptText("Ex.: 120");
        campoQuantidade.setMaxWidth(Double.MAX_VALUE);

        DatePicker campoValidade = new DatePicker();
        campoValidade.setMaxWidth(Double.MAX_VALUE);

        formulario.add(criarGrupoCampo(new Label("Nome da bebida"), campoNome), 0, 0);
        formulario.add(criarGrupoCampo(new Label("Marca"), campoMarca), 1, 0);

        formulario.add(criarGrupoCampo(new Label("Categoria"), campoCategoria), 0, 1);
        formulario.add(criarGrupoCampo(new Label("Preço"), campoPreco), 1, 1);

        formulario.add(criarGrupoCampo(
                new Label("Quantidade em estoque"), campoQuantidade), 0, 2);
        formulario.add(criarGrupoCampo(new Label("Data de validade"), campoValidade), 1, 2);

        Button cadastrar = new Button("Cadastrar Bebida");
        cadastrar.getStyleClass().add("btn-primary");

        Button limpar = new Button("Limpar");
        limpar.getStyleClass().add("btn-secondary");

        Label erro = new Label();
        erro.getStyleClass().add("field-error");
        erro.setVisible(false);

        HBox botoes = new HBox(12, cadastrar, limpar);

        limpar.setOnAction(event -> {
            campoNome.clear();
            campoMarca.clear();
            campoCategoria.getSelectionModel().clearSelection();
            campoPreco.clear();
            campoQuantidade.clear();
            campoValidade.setValue(null);
            erro.setVisible(false);
        });

        cadastrar.setOnAction(event -> {
            String nome = campoNome.getText().trim();
            String marca = campoMarca.getText().trim();
            String categoria = campoCategoria.getValue();
            String precoTexto = campoPreco.getText().trim();
            String quantidadeTexto = campoQuantidade.getText().trim();

            if (nome.isEmpty() || marca.isEmpty() || categoria == null
                    || precoTexto.isEmpty() || quantidadeTexto.isEmpty()) {
                mostrarErro(erro, "Preencha todos os campos.");
                return;
            }

            try {
                BigDecimal preco = new BigDecimal(precoTexto.replace(",", "."));
                int quantidade = Integer.parseInt(quantidadeTexto);

                if (preco.signum() < 0 || quantidade < 0) {
                    throw new NumberFormatException();
                }

                Bebida bebida = new Bebida(
                        0, nome, marca, categoria, preco, quantidade
                );

                if (campoValidade.getValue() != null) {
                    bebida.setValidade(campoValidade.getValue());
                }

                EstoqueController.getInstance().cadastrarBebida(bebida);

                campoNome.clear();
                campoMarca.clear();
                campoCategoria.getSelectionModel().clearSelection();
                campoPreco.clear();
                campoQuantidade.clear();
                campoValidade.setValue(null);
                erro.setVisible(false);

                notificar.accept("Bebida cadastrada com sucesso!");
            } catch (NumberFormatException e) {
                mostrarErro(erro,
                        "Preço deve ser um número e quantidade um inteiro válido.");
            }
        });

        VBox card = new VBox(20,
                topoFormulario,
                formulario,
                erro,
                botoes);
        card.getStyleClass().add("panel");
        card.setMaxWidth(880);

        VBox raiz = new VBox(24, cabecalho, card);
        raiz.setMaxWidth(Double.MAX_VALUE);
        return raiz;
    }

    private VBox criarGrupoCampo(Label label, Node campo) {
        label.getStyleClass().add("field-label");

        VBox grupo = new VBox(6, label, campo);
        return grupo;
    }

    private void mostrarErro(Label erro, String mensagem) {
        erro.setText(mensagem);
        erro.setVisible(true);
    }
}