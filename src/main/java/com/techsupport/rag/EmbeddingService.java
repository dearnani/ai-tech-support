package com.techsupport.rag;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmbeddingService {

    @Autowired
    private EmbeddingModel embeddingModel;

    public float[] embed(String text) {
        try {
            return embeddingModel.embed(text);
        } catch (Exception e) {
            throw new RuntimeException("Embedding failed for: " + text, e);
        }
    }

    public List<float[]> embedBatch(List<String> texts) {
        try {
            return embeddingModel.embed(texts);
        } catch (Exception e) {
            throw new RuntimeException("Batch embedding failed", e);
        }
    }
}
