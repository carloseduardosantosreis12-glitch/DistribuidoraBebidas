package br.com.distribuidora.view;

import br.com.distribuidora.repository.ConfiguracaoStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ConfiguracaoView {

    private final BorderPane root = new BorderPane();
    private final ConfiguracaoStore config = ConfiguracaoStore.getInstance();
    private final Runnable onSalvar;

    private TextField campoEmpresa;
    private TextField campoLimite;

    public ConfiguracaoView(Runnable onSalvar) {
        this.onSalvar = onSalvar;

        root.getStyleClass().add("cadastro-root");

        VBox pagina = new VBox(25);
        pagina.setPadding(new Insets(35));

        Label titulo = new Label("Configurações");
        titulo.getStyleClass().add("cadastro-titulo");

        Label subtitulo = new Label(
                "Personalize os dados, o estoque, as vendas, a aparência e as preferências do sistema"
        );
        subtitulo.getStyleClass().add("cadastro-subtitulo");

        VBox cabecalho = new VBox(5);
        cabecalho.getChildren().addAll(titulo, subtitulo);

        TabPane abas = new TabPane();
        abas.getStyleClass().add("config-abas");

        Tab abaEmpresa = new Tab("Empresa", criarAbaEmpresa());
        Tab abaEstoque = new Tab("Estoque", criarAbaEstoque());
        Tab abaVendas = new Tab("Vendas", criarAbaVendas());
        Tab abaAparencia = new Tab("Aparência", criarAbaAparencia());
        Tab abaGerais = new Tab("Configurações Gerais", criarAbaGerais());

        abas.getTabs().addAll(abaEmpresa, abaEstoque, abaVendas, abaAparencia, abaGerais);

        pagina.getChildren().addAll(cabecalho, abas);

        javafx.scene.control.ScrollPane rolagem = new javafx.scene.control.ScrollPane(pagina);
        rolagem.setFitToWidth(true);
        rolagem.getStyleClass().add("pagina-scroll");

        root.setCenter(rolagem);
    }

    // =========================
    // ABA: EMPRESA
    // =========================

    private Node criarAbaEmpresa() {
        VBox conteudo = new VBox(22);

        Label descricao = new Label("Mantenha os dados da empresa sempre atualizados.");
        descricao.getStyleClass().add("formulario-descricao");

        HBox linha = new HBox(26);
        linha.setFillHeight(true);

        StackPane areaLogo = new StackPane();
        areaLogo.setPrefSize(220, 150);
        areaLogo.getStyleClass().add("logo-area");

        Label textoLogo = new Label("Logo da empresa");
        textoLogo.getStyleClass().add("logo-texto");

        Button alterarLogo = new Button("Alterar logo");
        alterarLogo.getStyleClass().add("botao-secundario");

        VBox interiorLogo = new VBox(12);
        interiorLogo.setAlignment(Pos.CENTER);
        interiorLogo.getChildren().addAll(textoLogo, alterarLogo);

        areaLogo.getChildren().add(interiorLogo);

        campoEmpresa = new TextField(config.getNomeEmpresa());
        campoEmpresa.getStyleClass().add("campo-formulario");
        campoEmpresa.setMaxWidth(Double.MAX_VALUE);

        TextField campoCnpj = criarCampoFormulario("00.000.000/0000-00");
        TextField campoTelefone = criarCampoFormulario("(00) 0000-0000");
        TextField campoEmail = criarCampoFormulario("contato@empresa.com.br");
        TextField campoEndereco = criarCampoFormulario("Rua das Bebidas, 123");
        TextField campoCidade = criarCampoFormulario("São Paulo");
        TextField campoEstado = criarCampoFormulario("SP");
        TextField campoCep = criarCampoFormulario("00000-000");

        GridPane formulario = new GridPane();
        formulario.setHgap(25);
        formulario.setVgap(20);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(50);
        c1.setHgrow(Priority.ALWAYS);

        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(50);
        c2.setHgrow(Priority.ALWAYS);

        formulario.getColumnConstraints().addAll(c1, c2);

        formulario.add(criarGrupoCampo("Nome da empresa", campoEmpresa), 0, 0);
        formulario.add(criarGrupoCampo("CNPJ", campoCnpj), 1, 0);
        formulario.add(criarGrupoCampo("Telefone", campoTelefone), 0, 1);
        formulario.add(criarGrupoCampo("E-mail", campoEmail), 1, 1);
        formulario.add(criarGrupoCampo("Endereço", campoEndereco), 0, 2);
        formulario.add(criarGrupoCampo("Cidade", campoCidade), 1, 2);
        formulario.add(criarGrupoCampo("Estado", campoEstado), 0, 3);
        formulario.add(criarGrupoCampo("CEP", campoCep), 1, 3);

        GridPane.setHgrow(formulario, Priority.ALWAYS);

        linha.getChildren().addAll(areaLogo, formulario);
        HBox.setHgrow(formulario, Priority.ALWAYS);

        Button salvar = new Button("Salvar alterações");
        salvar.getStyleClass().add("botao-principal");

        Label feedback = new Label();
        feedback.getStyleClass().add("cadastro-subtitulo");
        feedback.setVisible(false);

        HBox rodape = new HBox(12);
        rodape.setAlignment(Pos.CENTER_RIGHT);
        rodape.getChildren().addAll(feedback, salvar);

        alterarLogo.setOnAction(event -> mostrarFeedback(
                feedback, "Seletor de imagem ficará disponível em breve.", false));

        salvar.setOnAction(event -> {
            String empresa = campoEmpresa.getText().trim();

            if (empresa.isEmpty()) {
                mostrarFeedback(feedback, "Informe o nome da empresa.", false);
                return;
            }

            config.setNomeEmpresa(empresa);
            try {
                config.salvar();
            } catch (Exception e) {
                mostrarFeedback(feedback, "Erro ao salvar as configurações.", false);
                return;
            }

            mostrarFeedback(feedback, "Dados da empresa salvos com sucesso!", true);

            if (onSalvar != null) {
                onSalvar.run();
            }
        });

        conteudo.getChildren().addAll(descricao, linha, rodape);
        return conteudo;
    }

    // =========================
    // ABA: ESTOQUE
    // =========================

    private Node criarAbaEstoque() {
        VBox conteudo = new VBox(20);

        Label descricao = new Label("Defina os limites e alertas de estoque do sistema.");
        descricao.getStyleClass().add("formulario-descricao");

        campoLimite = new TextField(String.valueOf(config.getLimiteEstoqueBaixo()));
        campoLimite.getStyleClass().add("campo-formulario");
        campoLimite.setPrefWidth(180);

        VBox grupoLimite = criarGrupoCampo("Estoque mínimo padrão", campoLimite);

        VBox alertas = new VBox(14);

        Label tituloAlertas = new Label("Alertas de estoque");
        tituloAlertas.getStyleClass().add("formulario-titulo");

        alertas.getChildren().addAll(
                tituloAlertas,
                criarSwitch("Ativar alerta de estoque baixo", true),
                criarSwitch("Ativar alerta de produto sem estoque", true)
        );

        Button salvar = new Button("Salvar");
        salvar.getStyleClass().add("botao-principal");

        Label feedback = new Label();
        feedback.getStyleClass().add("cadastro-subtitulo");
        feedback.setVisible(false);

        HBox rodape = new HBox(12);
        rodape.setAlignment(Pos.CENTER_RIGHT);
        rodape.getChildren().addAll(feedback, salvar);

        salvar.setOnAction(event -> {
            try {
                int limite = Integer.parseInt(campoLimite.getText().trim());
                if (limite < 0) {
                    throw new NumberFormatException();
                }

                config.setLimiteEstoqueBaixo(limite);
                config.salvar();

                mostrarFeedback(feedback, "Configurações de estoque salvas!", true);

                if (onSalvar != null) {
                    onSalvar.run();
                }
            } catch (NumberFormatException e) {
                mostrarFeedback(feedback, "O estoque mínimo deve ser um número inteiro válido.", false);
            } catch (Exception e) {
                mostrarFeedback(feedback, "Erro ao salvar as configurações.", false);
            }
        });

        conteudo.getChildren().addAll(descricao, grupoLimite, alertas, rodape);
        return conteudo;
    }

    // =========================
    // ABA: VENDAS
    // =========================

    private Node criarAbaVendas() {
        VBox conteudo = new VBox(20);

        Label descricao = new Label("Configure as preferências para as vendas no balcão.");
        descricao.getStyleClass().add("formulario-descricao");

        Label tituloPagamento = new Label("Formas de pagamento");
        tituloPagamento.getStyleClass().add("formulario-titulo");

        VBox listaPagamentos = new VBox(10);
        listaPagamentos.getChildren().addAll(
                criarCheckBox("Dinheiro", true),
                criarCheckBox("Cartão de crédito", true),
                criarCheckBox("Cartão de débito", true),
                criarCheckBox("Pix", true),
                criarCheckBox("Transferência bancária", true)
        );

        Label tituloDesconto = new Label("Descontos");
        tituloDesconto.getStyleClass().add("formulario-titulo");

        HBox permitirDesconto = criarSwitch("Permitir desconto nas vendas", true);

        TextField limiteDesconto = new TextField("10");
        limiteDesconto.getStyleClass().add("campo-formulario");
        limiteDesconto.setPrefWidth(110);

        Label sufixo = new Label("%");
        sufixo.getStyleClass().add("campo-label");

        HBox grupoLimite = new HBox(10);
        grupoLimite.setAlignment(Pos.CENTER_LEFT);
        grupoLimite.getChildren().addAll(
                criarGrupoCampo("Limite de desconto (%)", limiteDesconto),
                sufixo
        );

        Label tituloFluxo = new Label("Fluxo de venda");
        tituloFluxo.getStyleClass().add("formulario-titulo");

        VBox fluxo = new VBox(10);
        fluxo.getChildren().addAll(
                criarSwitch("Exigir confirmação antes de cancelar venda", true),
                criarSwitch("Imprimir comprovante automaticamente", false)
        );

        Button salvar = new Button("Salvar");
        salvar.getStyleClass().add("botao-principal");

        Label feedback = new Label();
        feedback.getStyleClass().add("cadastro-subtitulo");
        feedback.setVisible(false);

        HBox rodape = new HBox(12);
        rodape.setAlignment(Pos.CENTER_RIGHT);
        rodape.getChildren().addAll(feedback, salvar);

        salvar.setOnAction(event -> {
            String limite = limiteDesconto.getText().trim();

            try {
                Double.parseDouble(limite.replace(",", "."));
            } catch (NumberFormatException e) {
                mostrarFeedback(feedback, "O limite de desconto deve ser um número válido.", false);
                return;
            }

            mostrarFeedback(feedback, "Preferências de vendas salvas!", true);
        });

        conteudo.getChildren().addAll(
                descricao,
                tituloPagamento,
                listaPagamentos,
                tituloDesconto,
                permitirDesconto,
                grupoLimite,
                tituloFluxo,
                fluxo,
                rodape
        );

        return conteudo;
    }

    // =========================
    // ABA: APARÊNCIA
    // =========================

    private Node criarAbaAparencia() {
        VBox conteudo = new VBox(20);

        Label descricao = new Label("Personalize o tema, a fonte e a densidade da interface.");
        descricao.getStyleClass().add("formulario-descricao");

        Label tituloTema = new Label("Tema");
        tituloTema.getStyleClass().add("formulario-titulo");

        ToggleGroup grupoTema = new ToggleGroup();

        RadioButton claro = new RadioButton("Claro");
        RadioButton escuro = new RadioButton("Escuro");
        RadioButton sistema = new RadioButton("Sistema");

        claro.setUserData("claro");
        escuro.setUserData("escuro");
        sistema.setUserData("sistema");

        claro.setToggleGroup(grupoTema);
        escuro.setToggleGroup(grupoTema);
        sistema.setToggleGroup(grupoTema);
        claro.setSelected(true);

        HBox linhasTema = new HBox(22);
        linhasTema.setAlignment(Pos.CENTER_LEFT);
        linhasTema.getChildren().addAll(claro, escuro, sistema);

        ComboBox<String> tamanhoFonte = new ComboBox<>();
        tamanhoFonte.getItems().addAll("Pequena", "Média", "Grande");
        tamanhoFonte.setValue("Média");
        tamanhoFonte.setPrefWidth(160);

        ComboBox<String> densidade = new ComboBox<>();
        densidade.getItems().addAll("Confortável", "Compacta");
        densidade.setValue("Confortável");
        densidade.setPrefWidth(160);

        HBox opcoes = new HBox(26);
        opcoes.setAlignment(Pos.CENTER_LEFT);
        opcoes.getChildren().addAll(
                criarGrupoCampo("Tamanho da fonte", tamanhoFonte),
                criarGrupoCampo("Densidade da interface", densidade)
        );

        Button aplicar = new Button("Aplicar");
        aplicar.getStyleClass().add("botao-principal");

        Button restaurar = new Button("Restaurar padrão");
        restaurar.getStyleClass().add("botao-secundario");

        Label feedback = new Label();
        feedback.getStyleClass().add("cadastro-subtitulo");
        feedback.setVisible(false);

        HBox rodape = new HBox(12);
        rodape.setAlignment(Pos.CENTER_RIGHT);
        rodape.getChildren().addAll(feedback, restaurar, aplicar);

        aplicar.setOnAction(event -> {
            Object tema = grupoTema.getSelectedToggle() == null
                    ? "claro" : grupoTema.getSelectedToggle().getUserData();
            mostrarFeedback(feedback,
                    "Tema \"" + tema + "\" aplicado (" + tamanhoFonte.getValue()
                            + ", " + densidade.getValue() + ").", true);
        });

        restaurar.setOnAction(event -> {
            claro.setSelected(true);
            tamanhoFonte.setValue("Média");
            densidade.setValue("Confortável");
            mostrarFeedback(feedback, "Aparência restaurada ao padrão.", true);
        });

        conteudo.getChildren().addAll(
                descricao,
                tituloTema,
                linhasTema,
                opcoes,
                rodape
        );

        return conteudo;
    }

    // =========================
    // ABA: CONFIGURAÇÕES GERAIS
    // =========================

    private Node criarAbaGerais() {
        VBox conteudo = new VBox(20);

        Label descricao = new Label("Ajuste formatos e preferências gerais do sistema.");
        descricao.getStyleClass().add("formulario-descricao");

        ComboBox<String> formatoData = new ComboBox<>();
        formatoData.getItems().addAll("dd/MM/aaaa", "MM/dd/aaaa", "aaaa-MM-dd");
        formatoData.setValue("dd/MM/aaaa");
        formatoData.setPrefWidth(160);

        ComboBox<String> formatoMoeda = new ComboBox<>();
        formatoMoeda.getItems().addAll("R$", "US$", "€");
        formatoMoeda.setValue(config.getMoeda());
        formatoMoeda.setPrefWidth(120);

        HBox formatos = new HBox(26);
        formatos.setAlignment(Pos.CENTER_LEFT);
        formatos.getChildren().addAll(
                criarGrupoCampo("Formato de data", formatoData),
                criarGrupoCampo("Formato de moeda", formatoMoeda)
        );

        Label tituloPreferencias = new Label("Preferências do sistema");
        tituloPreferencias.getStyleClass().add("formulario-titulo");

        VBox preferencias = new VBox(10);
        preferencias.getChildren().addAll(
                criarSwitch("Impressão automática", false),
                criarSwitch("Notificações", true),
                criarSwitch("Sons do sistema", false)
        );

        Button salvar = new Button("Salvar");
        salvar.getStyleClass().add("botao-principal");

        Label feedback = new Label();
        feedback.getStyleClass().add("cadastro-subtitulo");
        feedback.setVisible(false);

        HBox rodape = new HBox(12);
        rodape.setAlignment(Pos.CENTER_RIGHT);
        rodape.getChildren().addAll(feedback, salvar);

        salvar.setOnAction(event -> {
            config.setMoeda(formatoMoeda.getValue());
            try {
                config.salvar();
            } catch (Exception e) {
                mostrarFeedback(feedback, "Erro ao salvar as configurações.", false);
                return;
            }

            mostrarFeedback(feedback, "Configurações gerais salvas com sucesso!", true);

            if (onSalvar != null) {
                onSalvar.run();
            }
        });

        conteudo.getChildren().addAll(
                descricao,
                formatos,
                tituloPreferencias,
                preferencias,
                rodape
        );

        return conteudo;
    }

    // =========================
    // HELPERES
    // =========================

    private TextField criarCampoFormulario(String prompt) {
        TextField campo = new TextField();
        campo.setPromptText(prompt);
        campo.getStyleClass().add("campo-formulario");
        campo.setMaxWidth(Double.MAX_VALUE);
        return campo;
    }

    private CheckBox criarCheckBox(String texto, boolean selecionado) {
        CheckBox check = new CheckBox(texto);
        check.setSelected(selecionado);
        check.getStyleClass().add("checkbox-config");
        return check;
    }

    private HBox criarSwitch(String texto, boolean ativo) {
        ToggleButton toggle = new ToggleButton();
        toggle.setSelected(ativo);
        toggle.setFocusTraversable(false);
        toggle.getStyleClass().add("switch");

        Label label = new Label(texto);
        label.getStyleClass().add("campo-label");

        HBox linha = new HBox(12);
        linha.setAlignment(Pos.CENTER_LEFT);

        HBox.setHgrow(label, Priority.ALWAYS);

        linha.getChildren().addAll(label, toggle);
        return linha;
    }

    private VBox criarGrupoCampo(String texto, Node campo) {
        Label label = new Label(texto);
        label.getStyleClass().add("campo-label");

        VBox grupo = new VBox(8);
        grupo.getChildren().addAll(label, campo);
        return grupo;
    }

    private void mostrarFeedback(Label feedback, String mensagem, boolean sucesso) {
        feedback.setText(mensagem);
        feedback.setStyle(sucesso ? "" : "-fx-text-fill: #dc2626;");
        feedback.setVisible(true);
    }

    public BorderPane getRoot() {
        return root;
    }
}