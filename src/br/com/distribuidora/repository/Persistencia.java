package br.com.distribuidora.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class Persistencia {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class,
                    (JsonSerializer<LocalDate>) (origem, tipo, contexto) -> new JsonPrimitive(origem.toString()))
            .registerTypeAdapter(LocalDate.class,
                    (JsonDeserializer<LocalDate>) (json, tipo, contexto) -> LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonSerializer<LocalDateTime>) (origem, tipo, contexto) -> new JsonPrimitive(origem.toString()))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, tipo, contexto) -> LocalDateTime.parse(json.getAsString()))
            .create();

    private Persistencia() {
    }

    public static <T> java.util.List<T> lerLista(Path arquivo, Type tipo) {
        if (!Files.exists(arquivo)) {
            return null;
        }
        try (Reader leitor = Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {
            return GSON.fromJson(leitor, tipo);
        } catch (IOException | RuntimeException e) {
            System.err.println("Falha ao ler " + arquivo + ": " + e.getMessage());
            return null;
        }
    }

    public static void gravar(Path arquivo, Object conteudo) {
        try {
            Path pai = arquivo.getParent();
            if (pai != null) {
                Files.createDirectories(pai);
            }
            try (Writer escritor = Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8)) {
                GSON.toJson(conteudo, escritor);
            }
        } catch (IOException e) {
            System.err.println("Falha ao gravar " + arquivo + ": " + e.getMessage());
        }
    }
}