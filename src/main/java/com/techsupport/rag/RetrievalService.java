package com.techsupport.rag;

import com.techsupport.config.QdrantProperties;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Models;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetrievalService {

    private final QdrantClient qdrantClient;
    private final EmbeddingService embeddingService;
    private final QdrantProperties qdrantProperties;

    public List<RetrievedDocument> retrieve(String query, int limit) {
        try {
            float[] queryVector = embeddingService.embed(query);

            var searchResult = qdrantClient.search(qdrantProperties.getCollection())
                    .vector(queryVector)
                    .limit(limit)
                    .withPayload(true)
                    .build()
                    .get();

            return searchResult.stream()
                    .map(point -> RetrievedDocument.builder()
                            .id(String.valueOf(point.getId()))
                            .score(point.getScore())
                            .content(extractContent(point))
                            .build())
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Retrieval failed", e);
        }
    }

    private String extractContent(Models.ScoredPoint point) {
        if (point.getPayload().containsKey("content")) {
            return point.getPayload().get("content").getStringValue();
        }
        return "";
    }
}

