package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.example.bootstrap.SchemaInitializer;
import org.example.service.KvServiceImpl;
import org.example.storage.TarantoolClientManager;

public class ServerApplication {
    public static void main(String[] args) throws Exception {
        TarantoolClientManager clientManager = new TarantoolClientManager();
        SchemaInitializer.init(clientManager);
        KvServiceImpl service = new KvServiceImpl(clientManager);

        Server server = ServerBuilder.forPort(8080)
                .addService(service)
                .build()
                .start();
    }
}
