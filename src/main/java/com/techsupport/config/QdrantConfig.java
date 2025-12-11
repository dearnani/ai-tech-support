package com.techsupport.config;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Models;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class QdrantConfig {

    private final QdrantProperties qdrantProperties;

    @Bean
    public QdrantClient qdrantClient() {
        QdrantClient client = new QdrantClient(QdrantClient.buildUrl(qdrantProperties.getHost()));
        initializeCollection(client);
        return client;
    }

    @Bean
    public String collectionName() {
        return qdrantProperties.getCollection();
    }

    private void initializeCollection(QdrantClient client) {
        try {
            // Collection already exists
            client.getCollectionInfo(qdrantProperties.getCollection());
        } catch (Exception e) {
            // Create collection if not exists (1536 dims for text-embedding-3-small)
            try {
                client.createCollection(qdrantProperties.getCollection(),
                    Models.VectorParams.newBuilder()
                        .setSize(1536)
                        .setDistance(Models.Distance.Cosine)
                        .build());
            } catch (Exception ex) {
                throw new RuntimeException("Failed to create Qdrant collection", ex);
            }
        }
    }
}
