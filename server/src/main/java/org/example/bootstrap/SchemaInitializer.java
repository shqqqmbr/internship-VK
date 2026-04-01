package org.example.bootstrap;

import io.tarantool.client.TarantoolClient;
import org.example.annotation.TarantoolSchema;
import org.example.storage.TarantoolClientManager;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

public class SchemaInitializer {

    public static void init(TarantoolClientManager clientManager) throws Exception {
        Class<?> clazz = clientManager.getClass();
        TarantoolSchema annotation = clazz.getAnnotation(TarantoolSchema.class);
        if (annotation != null && annotation.autoInit()) {
            String schemaFile = annotation.schemaFile();
            Field clientField = clazz.getDeclaredField("client");
            clientField.setAccessible(true);
            TarantoolClient client = (TarantoolClient) clientField.get(clientManager);
            executeLuaScript(client, schemaFile);
        }
    }

    private static void executeLuaScript(TarantoolClient client, String path) {
        try (InputStream is = SchemaInitializer.class.getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("Schema file not found: " + path);
                return;
            }
            String script = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            client.call(script);
            System.out.println("Schema initialized from: " + path);
        } catch (Exception e) {
            System.err.println("Failed to initialize schema: " + e.getMessage());
        }
    }
}