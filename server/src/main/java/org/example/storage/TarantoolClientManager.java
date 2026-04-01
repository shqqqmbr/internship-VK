package org.example.storage;

import io.tarantool.client.TarantoolClient;
import io.tarantool.client.factory.TarantoolCrudClientBuilder;
import org.example.annotation.TarantoolSchema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TarantoolSchema(schemaFile = "/db/init.lua")
public class TarantoolClientManager {

    private final TarantoolClient client;

    public TarantoolClientManager() throws Exception {
        this.client = new TarantoolCrudClientBuilder()
                .withPort(3301)
                .build();
    }

    public void put(String key, byte[] value) {
        if (value == null) {
            client.call("box.space.kv:insert", Arrays.asList(key, null));
        } else {
            client.call("box.space.kv:insert", Arrays.asList(key, value));
        }
    }

    public byte[] get(String key) {
        List<?> result = (List<?>) client.call("box.space.kv:select", Arrays.asList(key));
        if (result != null && !result.isEmpty()) {
            List<?> tuple = (List<?>) result.get(0);
            return (byte[]) tuple.get(1);
        }
        return null;
    }

    public void delete(String key) {
        client.call("box.space.kv:delete", Arrays.asList(key));
    }

    public List<KeyValue> range(String keySince, String keyTo) {
        List<?> result = (List<?>) client.call("box.space.kv:select", Arrays.asList(keySince, keyTo));
        List<KeyValue> list = new ArrayList<>();
        for (Object obj : result) {
            List<?> tuple = (List<?>) obj;
            String key = (String) tuple.get(0);
            byte[] value = (byte[]) tuple.get(1);
            list.add(new KeyValue(key, value));
        }
        return list;
    }

    public long count() {
        List<?> result = (List<?>) client.call("box.space.kv:len");
        if (result != null && !result.isEmpty()) {
            return ((Number) result.get(0)).longValue();
        }
        return 0;
    }

    public static class KeyValue {
        private final String key;
        private final byte[] value;

        public KeyValue(String key, byte[] value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public byte[] getValue() {
            return value;
        }
    }
}