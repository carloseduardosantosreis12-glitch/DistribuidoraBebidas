package br.com.distribuidora.view.components;

import br.com.distribuidora.model.Bebida;
import br.com.distribuidora.util.Formatadores;
import java.math.BigDecimal;
import java.time.LocalDate;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public final class TabelaCelulas {

    private TabelaCelulas() {
    }

    public static TableCell<Bebida, BigDecimal> preco(String simbolo) {
        TableCell<Bebida, BigDecimal> celula = new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal valor, boolean vazio) {
                setText(vazio || valor == null ? "" : Formatadores.moeda(simbolo, valor));
            }
        };
        celula.setAlignment(Pos.CENTER);
        return celula;
    }

    public static TableCell<Bebida, Integer> estoque(int limite) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Integer estoque, boolean vazio) {
                if (vazio || estoque == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                Label numero = new Label(String.valueOf(estoque));
                HBox linha = new HBox(6, numero);
                linha.setAlignment(Pos.CENTER);
                linha.setMaxWidth(Double.MAX_VALUE);
                StatusBadge.paraEstoque(estoque, limite)
                        .ifPresent(badge -> linha.getChildren().add(0, badge));
                setGraphic(linha);
                setText(null);
                setAlignment(Pos.CENTER);
            }
        };
    }

    public static TableCell<Bebida, LocalDate> validade() {
        return new TableCell<>() {
            @Override
            protected void updateItem(LocalDate data, boolean vazio) {
                setText(vazio || data == null ? "—" : Formatadores.data(data));
            }
        };
    }
}