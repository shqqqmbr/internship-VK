package org.example.service;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.example.proto.*;
import org.example.storage.TarantoolClientManager;

public class KvServiceImpl extends KvServiceGrpc.KvServiceImplBase {
    private final TarantoolClientManager storage;

    public KvServiceImpl(TarantoolClientManager storage) {
        this.storage = storage;
    }

    @Override
    public void put(PutRequest request, StreamObserver<PutResponse> responseObserver) {
        try {
            String key = request.getKey();
            byte[] value = request.getValue().toByteArray();
            storage.put(key, value);

            responseObserver.onNext(PutResponse.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void get(GetRequest request, StreamObserver<GetResponse> responseObserver) {
        try {
            String key = request.getKey();
            byte[] value = storage.get(key);

            GetResponse.Builder response = GetResponse.newBuilder();
            if (value != null) {
                response.setValue(ByteString.copyFrom(value));
            }

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void delete(DeleteRequest request, StreamObserver<DeleteResponse> responseObserver) {
        try {
            String key = request.getKey();
            storage.delete(key);

            responseObserver.onNext(DeleteResponse.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void range(RangeRequest request, StreamObserver<KeyValue> responseObserver) {
        try {
            storage.range(request.getKeySince(), request.getKeyTo()).stream()
                    .map(keyValue -> KeyValue.newBuilder()
                            .setKey(keyValue.getKey())
                            .setValue(ByteString.copyFrom(keyValue.getValue()))
                            .build())
                    .forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void count(Empty request, StreamObserver<CountResponse> responseObserver) {
        try {
            long count = storage.count();
            CountResponse response = CountResponse.newBuilder()
                    .setCount(count)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
