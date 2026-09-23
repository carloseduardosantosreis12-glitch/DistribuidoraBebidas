package br.com.distribuidora.view;

import br.com.distribuidora.controller.EstoqueController;
import br.com.distribuidora.model.Bebida;
import br.com.distribuidora.util.Formatadores;
import br.com.distribuidora.view.components.PageHeader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.converter.LocalDateStringConverter;

public class CadastroBebidaView {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Bebida bebidaExistente;
    private final Consumer<String> notificar;
    private final Runnable onVoltar;
    private final Runnable onSalvoEdicao;

    private TextField campoNome;
    private TextField campoMarca;
    private ComboBox<String> campoCategoria;
    private TextField campoPreco;
    private TextField campoQuantidade;
    private DatePicker campoValidade;

    private Label erroNome;
    private Label erroMarca;
    private Label erroCategoria;
    private Label erroPreco;
    private Label erroQuantidade;
    private Label erroValidade;

    public CadastroBebidaView(Consumer<String> notificar, Runnable onVoltar) {
        this(null, notificar, onVoltar, null);
    }

    public CadastroBebidaView(Bebida bebidaExistente, Consumer<String> notificar,
                              Runnable onVoltar, Runnable onSalvoEdicao) {
        this.bebidaExistente = bebidaExistente;
        this.notificar = notificar;
        this.onVoltar = onVoltar;
        this.onSalvoEdicao = onSalvoEdicao;
    }

    public VBox getRoot() {
        boolean edicao = bebidaExistente != null;

        Label titulo = new Label(edicao ? "Editar Bebida" : "Cadastrar Bebida");
        titulo.getStyleClass().add("page-title");

        Label subtitulo = new Label(edicao
                ? "Atualize os dados da bebida #"
                        + Formatadores.codigo(bebidaExistente.getId() + 1)
                : "Adicione uma nova bebida ao estoque da distribuidora");
        subtitulo.getStyleClass().add("page-subtitle");

        PageHeader cabecalho = new PageHeader(titulo, subtitulo, botaoVoltar());

        montarCampos();

        Label tituloFormulario = new Label("Informações da Bebida");
        tituloFormulario.getStyleClass().add("section-title");

        Label descricaoFormulario = new Label(
                edicao
                        ? "Altere os campos desejados e salve as alterações."
                        : "Preencha os dados abaixo para cadastrar o produto."
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

        formulario.add(criarGrupoCampo(new Label("Nome da bebida"), campoNome, erroNome), 0, 0);
        formulario.add(criarGrupoCampo(new Label("Marca"), campoMarca, erroMarca), 1, 0);
        formulario.add(criarGrupoCampo(new Label("Categoria"), campoCategoria, erroCategoria), 0, 1);
        formulario.add(criarGrupoCampo(new Label("Preço"), campoPreco, erroPreco), 1, 1);
        formulario.add(criarGrupoCampo(
                new Label("Quantidade em estoque"), campoQuantidade, erroQuantidade), 0, 2);
        formulario.add(criarGrupoCampo(
                new Label("Data de validade"), campoValidade, erroValidade), 1, 2);

        Button acao = new Button(edicao ? "Salvar alterações" : "Cadastrar Bebida");
        acao.getStyleClass().add("btn-primary");
        acao.setDefaultButton(true);
        acao.setOnAction(event -> salvar());

        Button limpar = new Button("Limpar");
        limpar.getStyleClass().add("btn-secondary");
        limpar.setOnAction(event -> {
            limparCampos();
            limparErros();
        });

        HBox botoes = new HBox(12, acao, limpar);

        VBox card = new VBox(20, topoFormulario, formulario, botoes);
        card.getStyleClass().add("panel");
        card.setMaxWidth(880);

        VBox raiz = new VBox(24, cabecalho, card);
        raiz.setMaxWidth(Double.MAX_VALUE);

        Platform.runLater(campoNome::requestFocus);
        return raiz;
    }

    private Button botaoVoltar() {
        Button voltar = new Button("Voltar");
        voltar.getStyleClass().add("btn-secondary");
        voltar.setCancelButton(true);
        voltar.setOnAction(event -> onVoltar.run());
        return voltar;
    }

    private void montarCampos() {
        boolean edicao = bebidaExistente != null;

        campoNome = new TextField();
        campoNome.setPromptText("Ex.: Coca-Cola 2L");
        campoNome.setMaxWidth(Double.MAX_VALUE);

        campoMarca = new TextField();
        campoMarca.setPromptText("Ex.: Coca-Cola");
        campoMarca.setMaxWidth(Double.MAX_VALUE);

        campoCategoria = new ComboBox<>();
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

        campoPreco = new TextField();
        campoPreco.setPromptText("Ex.: 9,90");
        campoPreco.setMaxWidth(Double.MAX_VALUE);
        campoPreco.setTextFormatter(novoFormatterPreco());
        campoPreco.focusedProperty().addListener((obs, antigo, focado) -> {
            if (!focado) {
                formatarPreco();
            }
        });

        campoQuantidade = new TextField();
        campoQuantidade.setPromptText("Ex.: 120");
        campoQuantidade.setMaxWidth(Double.MAX_VALUE);
        campoQuantidade.setTextFormatter(novoFormatterInteiro());

        campoValidade = new DatePicker();
        campoValidade.setMaxWidth(Double.MAX_VALUE);
        campoValidade.setPromptText("dd/MM/aaaa");
        campoValidade.setConverter(new LocalDateStringConverter(FORMATO_DATA, FORMATO_DATA));

        erroNome = novoErro();
        erroMarca = novoErro();
        erroCategoria = novoErro();
        erroPreco = novoErro();
        erroQuantidade = novoErro();
        erroValidade = novoErro();

        if (edicao) {
            campoNome.setText(bebidaExistente.getNome());
            campoMarca.setText(bebidaExistente.getMarca());
            campoCategoria.setValue(bebidaExistente.getCategoria());
            campoPreco.setText(precoTexto(bebidaExistente.getPreco()));
            campoQuantidade.setText(String.valueOf(bebidaExistente.getEstoque()));
            campoValidade.setValue(bebidaExistente.getValidade());
        }
    }

    private void salvar() {
        limparErros();

        if (!validar()) {
            return;
        }

        String nome = campoNome.getText().trim();
        String marca = campoMarca.getText().trim();
        String categoria = campoCategoria.getValue();
        BigDecimal preco = parsePreco(campoPreco.getText());
        int quantidade = parseInteiro(campoQuantidade.getText());
        LocalDate validade = campoValidade.getValue();

        if (bebidaExistente == null) {
            Bebida bebida = new Bebida(0, nome, marca, categoria, preco, quantidade);
            bebida.setValidade(validade);
            EstoqueController.getInstance().cadastrarBebida(bebida);
            limparCampos();
            notificar.accept("Bebida cadastrada com sucesso!");
        } else {
            bebidaExistente.setNome(nome);
            bebidaExistente.setMarca(marca);
            bebidaExistente.setCategoria(categoria);
            bebidaExistente.setPreco(preco);
            bebidaExistente.setEstoque(quantidade);
            bebidaExistente.setValidade(validade);
            EstoqueController.getInstance().atualizarBebida(bebidaExistente);
            notificar.accept("Bebida atualizada com sucesso!");
            if (onSalvoEdicao != null) {
                onSalvoEdicao.run();
            }
        }
    }

    private boolean validar() {
        boolean ok = true;

        if (campoNome.getText().trim().isEmpty()) {
            marcarErro(campoNome, erroNome, "Informe o nome da bebida.");
            ok = false;
        }

        if (campoMarca.getText().trim().isEmpty()) {
            marcarErro(campoMarca, erroMarca, "Informe a marca do produto.");
            ok = false;
        }

        if (campoCategoria.getValue() == null) {
            marcarErro(campoCategoria, erroCategoria, "Selecione uma categoria.");
            ok = false;
        }

        BigDecimal preco = parsePreco(campoPreco.getText());
        if (preco == null || preco.signum() <= 0) {
            marcarErro(campoPreco, erroPreco, "Informe um preço maior que zero.");
            ok = false;
        }

        Integer quantidade = parseInteiro(campoQuantidade.getText());
        if (quantidade == null || quantidade < 0) {
            marcarErro(campoQuantidade, erroQuantidade,
                    "Informe uma quantidade inteira e não negativa.");
            ok = false;
        }

        LocalDate validade = campoValidade.getValue();
        if (validade != null && validade.isBefore(LocalDate.now())) {
            marcarErro(campoValidade, erroValidade, "A validade não pode estar no passado.");
            ok = false;
        }

        return ok;
    }

    private TextFormatter<String> novoFormatterPreco() {
        UnaryOperator<TextFormatter.Change> filtro = change -> {
            String novo = change.getControlNewText();
            if (!novo.matches("\\d{0,7}([.,]\\d{0,2})?")) {
                return null;
            }
            return change;
        };
        return new TextFormatter<>(filtro);
    }

    private TextFormatter<String> novoFormatterInteiro() {
        UnaryOperator<TextFormatter.Change> filtro = change -> {
            String novo = change.getControlNewText();
            if (!novo.matches("\\d{0,6}")) {
                return null;
            }
            return change;
        };
        return new TextFormatter<>(filtro);
    }

    private void formatarPreco() {
        String texto = campoPreco.getText();
        if (texto == null || texto.isBlank()) {
            return;
        }
        BigDecimal preco = parsePreco(texto);
        if (preco != null) {
            campoPreco.setText(precoTexto(preco));
        }
    }

    private BigDecimal parsePreco(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(texto.replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInteiro(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String precoTexto(BigDecimal preco) {
        return String.format("%.2f", preco).replace('.', ',');
    }

    private VBox criarGrupoCampo(Label label, Node campo, Label erro) {
        label.getStyleClass().add("field-label");
        return new VBox(6, label, campo, erro);
    }

    private Label novoErro() {
        Label erro = new Label();
        erro.getStyleClass().add("field-error");
        erro.setVisible(false);
        return erro;
    }

    private void marcarErro(Node campo, Label erro, String mensagem) {
        campo.getStyleClass().add("input-error");
        erro.setText(mensagem);
        erro.setVisible(true);
    }

    private void limparErros() {
        campoNome.getStyleClass().remove("input-error");
        campoMarca.getStyleClass().remove("input-error");
        campoCategoria.getStyleClass().remove("input-error");
        campoPreco.getStyleClass().remove("input-error");
        campoQuantidade.getStyleClass().remove("input-error");
        campoValidade.getStyleClass().remove("input-error");

        List.of(erroNome, erroMarca, erroCategoria, erroPreco, erroQuantidade, erroValidade)
                .forEach(erro -> erro.setVisible(false));
    }

    private void limparCampos() {
        campoNome.clear();
        campoMarca.clear();
        campoCategoria.getSelectionModel().clearSelection();
        campoPreco.clear();
        campoQuantidade.clear();
        campoValidade.setValue(null);
    }
}