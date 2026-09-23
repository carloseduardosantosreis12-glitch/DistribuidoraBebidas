package br.com.distribuidora.view;

import br.com.distribuidora.ThemeService;
import br.com.distribuidora.controller.ConfiguracaoController;
import br.com.distribuidora.view.components.PageHeader;
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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfiguracaoView {

    private final ConfiguracaoController configuracao = ConfiguracaoController.getInstance();
    private final Runnable onSalvar;

    private TextField campoEmpresa;
    private TextField campoLimite;
    private final List<CheckBox> checksPagamento = new ArrayList<>();
    private ToggleButton togglePermitirDesconto;
    private TextField campoLimiteDesconto;
    private ToggleButton toggleImprimirComprovante;

    public ConfiguracaoView(Runnable onSalvar) {
        this.onSalvar = onSalvar;
    }

    public VBox getRoot() {
        PageHeader cabecalho = new PageHeader(
                "Configurações",
                "Personalize os dados, o estoque, as vendas, a aparência e as preferências do sistema"
        );

        TabPane abas = new TabPane();
        abas.getStyleClass().add("tabs");

        Tab abaEmpresa = new Tab("Empresa", criarAbaEmpresa());
        Tab abaEstoque = new Tab("Estoque", criarAbaEstoque());
        Tab abaVendas = new Tab("Vendas", criarAbaVendas());
        Tab abaAparencia = new Tab("Aparência", criarAbaAparencia());
        Tab abaGerais = new Tab("Configurações Gerais", criarAbaGerais());

        abas.getTabs().addAll(abaEmpresa, abaEstoque, abaVendas, abaAparencia, abaGerais);
        abas.getSelectionModel().selectedIndexProperty().addListener((obs, o, n) -> {
            LoadingService.barra();
            LoadingService.parar();
        });

        VBox raiz = new VBox(20, cabecalho, abas);
        raiz.setMaxWidth(Double.MAX_VALUE);
        return raiz;
    }

    // =========================
    // ABA: EMPRESA
    // =========================

    private Node criarAbaEmpresa() {
        VBox conteudo = new VBox(20);

        Label descricao = new Label("Mantenha os dados da empresa sempre atualizados.");
        descricao.getStyleClass().add("field-help");

        HBox linha = new HBox(26);

        StackPane areaLogo = new StackPane();
        areaLogo.setPrefSize(220, 150);
        areaLogo.getStyleClass().add("logo-area");

        Label textoLogo = new Label("Logo da empresa");
        textoLogo.getStyleClass().add("logo-texto");

        Button alterarLogo = new Button("Alterar logo");
        alterarLogo.getStyleClass().add("btn-secondary");

        VBox interiorLogo = new VBox(12, textoLogo, alterarLogo);
        interiorLogo.setAlignment(Pos.CENTER);

        areaLogo.getChildren().add(interiorLogo);

        campoEmpresa = new TextField(configuracao.nomeEmpresa());
        campoEmpresa.setMaxWidth(Double.MAX_VALUE);

        TextField campoCnpj = criarCampoFormulario("00.000.000/0000-00");
        TextField campoTelefone = criarCampoFormulario("(00) 0000-0000");
        TextField campoEmail = criarCampoFormulario("contato@empresa.com.br");
        TextField campoEndereco = criarCampoFormulario("Rua das Bebidas, 123");
        TextField campoCidade = criarCampoFormulario("São Paulo");
        TextField campoEstado = criarCampoFormulario("SP");
        TextField campoCep = criarCampoFormulario("00000-000");

        GridPane formulario = new GridPane();
        formulario.setHgap(24);
        formulario.setVgap(16);

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

        HBox.setHgrow(formulario, Priority.ALWAYS);

        linha.getChildren().addAll(areaLogo, formulario);

        Button salvar = new Button("Salvar alterações");
        salvar.getStyleClass().add("btn-primary");

        Label feedback = new Label();
        feedback.getStyleClass().add("field-error");
        feedback.setVisible(false);

        HBox rodape = new HBox(12, feedback, salvar);
        rodape.setAlignment(Pos.CENTER_RIGHT);

        alterarLogo.setOnAction(event -> mostrarFeedback(
                feedback, "Seletor de imagem ficará disponível em breve.", false));

        salvar.setOnAction(event -> {
            String empresa = campoEmpresa.getText().trim();

            if (empresa.isEmpty()) {
                mostrarFeedback(feedback, "Informe o nome da empresa.", false);
                return;
            }

            try {
                configuracao.salvarNomeEmpresa(empresa);
            } catch (Exception e) {
                mostrarFeedback(feedback, "Erro ao salvar as configurações.", false);
                return;
            }

            mostrarFeedback(feedback, "Dados da empresa salvos com sucesso!", true);
            notificarSalvamento();
        });

        conteudo.getChildren().addAll(descricao, linha, rodape);
        return conteudo;
    }

    // =========================
    // ABA: ESTOQUE
    // =========================

    private Node criarAbaEstoque() {
        VBox conteudo = new VBox(18);

        Label descricao = new Label("Defina os limites e alertas de estoque do sistema.");
        descricao.getStyleClass().add("field-help");

        campoLimite = new TextField(String.valueOf(configuracao.limiteEstoqueBaixo()));
        campoLimite.setPrefWidth(180);

        VBox grupoLimite = criarGrupoCampo("Estoque mínimo padrão", campoLimite);

        VBox alertas = new VBox(12);
        Label tituloAlertas = new Label("Alertas de estoque");
        tituloAlertas.getStyleClass().add("section-title");
        alertas.getChildren().addAll(
                tituloAlertas,
                criarSwitch("Ativar alerta de estoque baixo", true),
                criarSwitch("Ativar alerta de produto sem estoque", true)
        );

        Button salvar = new Button("Salvar");
        salvar.getStyleClass().add("btn-primary");

        Label feedback = new Label();
        feedback.getStyleClass().add("field-error");
        feedback.setVisible(false);

        HBox rodape = new HBox(12, feedback, salvar);
        rodape.setAlignment(Pos.CENTER_RIGHT);

        salvar.setOnAction(event -> {
            try {
                int limite = Integer.parseInt(campoLimite.getText().trim());
                if (limite < 0) {
                    throw new NumberFormatException();
                }

                configuracao.salvarLimiteEstoqueBaixo(limite);

                mostrarFeedback(feedback, "Configurações de estoque salvas!", true);
                notificarSalvamento();
            } catch (NumberFormatException e) {
                mostrarFeedback(feedback,
                        "O estoque mínimo deve ser um número inteiro válido.", false);
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
        VBox conteudo = new VBox(18);

        Label descricao = new Label("Configure as preferências para as vendas no balcão.");
        descricao.getStyleClass().add("field-help");

        Label tituloPagamento = new Label("Formas de pagamento");
        tituloPagamento.getStyleClass().add("section-title");

        VBox listaPagamentos = new VBox(10);
        checksPagamento.clear();
        List<String> habilitadas = configuracao.formasPagamentoHabilitadas();
        for (String forma : configuracao.formasPagamentoPadrao()) {
            CheckBox check = criarCheckBox(forma, habilitadas.contains(forma));
            checksPagamento.add(check);
            listaPagamentos.getChildren().add(check);
        }

        Label tituloDesconto = new Label("Descontos");
        tituloDesconto.getStyleClass().add("section-title");

        togglePermitirDesconto = novoSwitch(configuracao.permitirDesconto());
        HBox linhaPermitirDesconto = linhaSwitch(
                "Permitir desconto nas vendas", togglePermitirDesconto);

        campoLimiteDesconto = new TextField(limiteTexto(configuracao.limiteDescontoPercentual()));
        campoLimiteDesconto.setPrefWidth(110);

        Label sufixo = new Label("%");
        sufixo.getStyleClass().add("field-label");

        VBox grupoLimite = criarGrupoCampo("Limite de desconto (%)", campoLimiteDesconto);

        HBox linhaLimite = new HBox(10, grupoLimite, sufixo);
        linhaLimite.setAlignment(Pos.CENTER_LEFT);

        Label tituloFluxo = new Label("Fluxo de venda");
        tituloFluxo.getStyleClass().add("section-title");

        VBox fluxo = new VBox(10);
        toggleImprimirComprovante = novoSwitch(configuracao.imprimirComprovante());
        fluxo.getChildren().addAll(
                linhaSwitch("Exigir confirmação antes de cancelar venda", novoSwitch(true)),
                linhaSwitch("Imprimir comprovante automaticamente", toggleImprimirComprovante)
        );

        Button salvar = new Button("Salvar");
        salvar.getStyleClass().add("btn-primary");

        Label feedback = new Label();
        feedback.getStyleClass().add("field-error");
        feedback.setVisible(false);

        HBox rodape = new HBox(12, feedback, salvar);
        rodape.setAlignment(Pos.CENTER_RIGHT);

        salvar.setOnAction(event -> {
            double limite;
            try {
                limite = Double.parseDouble(campoLimiteDesconto.getText().trim().replace(",", "."));
                if (limite < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                mostrarFeedback(feedback,
                        "O limite de desconto deve ser um número válido.", false);
                return;
            }

            List<String> habilitadasSalvar = checksPagamento.stream()
                    .filter(CheckBox::isSelected)
                    .map(CheckBox::getText)
                    .toList();
            try {
                configuracao.salvarFormasPagamento(habilitadasSalvar);
                configuracao.salvarPermitirDesconto(togglePermitirDesconto.isSelected());
                configuracao.salvarLimiteDescontoPercentual(limite);
                configuracao.salvarImprimirComprovante(toggleImprimirComprovante.isSelected());
                mostrarFeedback(feedback, "Preferências de vendas salvas!", true);
            } catch (IOException e) {
                mostrarFeedback(feedback, "Erro ao salvar as preferências de vendas.", false);
            }
        });

        conteudo.getChildren().addAll(
                descricao,
                tituloPagamento,
                listaPagamentos,
                tituloDesconto,
                linhaPermitirDesconto,
                linhaLimite,
                tituloFluxo,
                fluxo,
                rodape
        );

        return conteudo;
    }

    private String limiteTexto(double valor) {
        if (valor == Math.floor(valor)) {
            return String.valueOf((long) valor);
        }
        return String.valueOf(valor).replace('.', ',');
    }

    // =========================
    // ABA: APARÊNCIA
    // =========================

    private Node criarAbaAparencia() {
        VBox conteudo = new VBox(18);

        Label descricao = new Label("Personalize o tema, a fonte e a densidade da interface.");
        descricao.getStyleClass().add("field-help");

        Label tituloTema = new Label("Tema");
        tituloTema.getStyleClass().add("section-title");

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
        String temaSalvo = configuracao.tema();
        if ("escuro".equals(temaSalvo)) {
            escuro.setSelected(true);
        } else if ("sistema".equals(temaSalvo)) {
            sistema.setSelected(true);
        }

        HBox linhasTema = new HBox(22, claro, escuro, sistema);
        linhasTema.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> tamanhoFonte = new ComboBox<>();
        tamanhoFonte.getItems().addAll("Pequena", "Média", "Grande");
        tamanhoFonte.setValue(configuracao.fonte());
        tamanhoFonte.setPrefWidth(160);

        ComboBox<String> densidade = new ComboBox<>();
        densidade.getItems().addAll("Confortável", "Compacta");
        densidade.setValue(configuracao.densidade());
        densidade.setPrefWidth(160);

        HBox opcoes = new HBox(26,
                criarGrupoCampo("Tamanho da fonte", tamanhoFonte),
                criarGrupoCampo("Densidade da interface", densidade));
        opcoes.setAlignment(Pos.CENTER_LEFT);

        Button aplicar = new Button("Aplicar");
        aplicar.getStyleClass().add("btn-primary");

        Button restaurar = new Button("Restaurar padrão");
        restaurar.getStyleClass().add("btn-secondary");

        Label feedback = new Label();
        feedback.getStyleClass().add("field-error");
        feedback.setVisible(false);

        HBox rodape = new HBox(12, feedback, restaurar, aplicar);
        rodape.setAlignment(Pos.CENTER_RIGHT);

        aplicar.setOnAction(event -> {
            String tema = String.valueOf(grupoTema.getSelectedToggle() == null
                    ? "claro" : grupoTema.getSelectedToggle().getUserData());
            String fonteEscolhida = tamanhoFonte.getValue();
            String densidadeEscolhida = densidade.getValue();
            try {
                configuracao.salvarTema(tema);
                configuracao.salvarFonte(fonteEscolhida);
                configuracao.salvarDensidade(densidadeEscolhida);
                ThemeService.aplicarTema(tema);
                ThemeService.aplicarFonte(fonteEscolhida);
                ThemeService.aplicarDensidade(densidadeEscolhida);
                String temaMsg = "sistema".equals(tema)
                        ? "sistema (" + ThemeService.temaAtual() + ")"
                        : tema;
                mostrarFeedback(feedback,
                        "Tema \"" + temaMsg + "\", fonte \"" + fonteEscolhida
                                + "\", densidade \"" + densidadeEscolhida + "\" aplicados.", true);
            } catch (IOException e) {
                mostrarFeedback(feedback, "Erro ao salvar as preferências de aparência.", false);
            }
        });

        restaurar.setOnAction(event -> {
            claro.setSelected(true);
            tamanhoFonte.setValue("Média");
            densidade.setValue("Confortável");
            try {
                configuracao.salvarTema("claro");
                configuracao.salvarFonte("Média");
                configuracao.salvarDensidade("Confortável");
                ThemeService.aplicarTema("claro");
                ThemeService.aplicarFonte("Média");
                ThemeService.aplicarDensidade("Confortável");
                mostrarFeedback(feedback, "Aparência restaurada ao padrão.", true);
            } catch (IOException e) {
                mostrarFeedback(feedback, "Erro ao restaurar a aparência.", false);
            }
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
        VBox conteudo = new VBox(18);

        Label descricao = new Label("Ajuste formatos e preferências gerais do sistema.");
        descricao.getStyleClass().add("field-help");

        ComboBox<String> formatoData = new ComboBox<>();
        formatoData.getItems().addAll("dd/MM/aaaa", "MM/dd/aaaa", "aaaa-MM-dd");
        formatoData.setValue("dd/MM/aaaa");
        formatoData.setPrefWidth(160);

        ComboBox<String> formatoMoeda = new ComboBox<>();
        formatoMoeda.getItems().addAll("R$", "US$", "€");
        formatoMoeda.setValue(configuracao.moeda());
        formatoMoeda.setPrefWidth(120);

        HBox formatos = new HBox(26,
                criarGrupoCampo("Formato de data", formatoData),
                criarGrupoCampo("Formato de moeda", formatoMoeda));
        formatos.setAlignment(Pos.CENTER_LEFT);

        Label tituloPreferencias = new Label("Preferências do sistema");
        tituloPreferencias.getStyleClass().add("section-title");

        VBox preferencias = new VBox(10);
        preferencias.getChildren().addAll(
                criarSwitch("Impressão automática", false),
                criarSwitch("Notificações", true),
                criarSwitch("Sons do sistema", false)
        );

        Button salvar = new Button("Salvar");
        salvar.getStyleClass().add("btn-primary");

        Label feedback = new Label();
        feedback.getStyleClass().add("field-error");
        feedback.setVisible(false);

        HBox rodape = new HBox(12, feedback, salvar);
        rodape.setAlignment(Pos.CENTER_RIGHT);

        salvar.setOnAction(event -> {
            try {
                configuracao.salvarMoeda(formatoMoeda.getValue());
            } catch (Exception e) {
                mostrarFeedback(feedback, "Erro ao salvar as configurações.", false);
                return;
            }

            mostrarFeedback(feedback, "Configurações gerais salvas com sucesso!", true);
            notificarSalvamento();
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

    private void notificarSalvamento() {
        if (onSalvar != null) {
            onSalvar.run();
        }
    }

    private TextField criarCampoFormulario(String prompt) {
        TextField campo = new TextField();
        campo.setPromptText(prompt);
        campo.setMaxWidth(Double.MAX_VALUE);
        return campo;
    }

    private CheckBox criarCheckBox(String texto, boolean selecionado) {
        CheckBox check = new CheckBox(texto);
        check.setSelected(selecionado);
        return check;
    }

    private HBox criarSwitch(String texto, boolean ativo) {
        return linhaSwitch(texto, novoSwitch(ativo));
    }

    private ToggleButton novoSwitch(boolean ativo) {
        ToggleButton toggle = new ToggleButton();
        toggle.setSelected(ativo);
        toggle.setFocusTraversable(false);
        toggle.getStyleClass().add("switch");
        return toggle;
    }

    private HBox linhaSwitch(String texto, ToggleButton toggle) {
        Label label = new Label(texto);
        label.getStyleClass().add("field-label");

        HBox linha = new HBox(12, label, toggle);
        linha.getStyleClass().add("setting-row");
        linha.setMaxWidth(Double.MAX_VALUE);
        linha.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(label, Priority.ALWAYS);
        return linha;
    }

    private VBox criarGrupoCampo(String texto, Node campo) {
        Label label = new Label(texto);
        label.getStyleClass().add("field-label");

        VBox grupo = new VBox(6, label, campo);
        return grupo;
    }

    private void mostrarFeedback(Label feedback, String mensagem, boolean sucesso) {
        feedback.setText(mensagem);
        feedback.getStyleClass().removeAll("field-error", "field-success");
        feedback.getStyleClass().add(sucesso ? "field-success" : "field-error");
        feedback.setVisible(true);
    }
}