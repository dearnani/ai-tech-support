package com.techsupport.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "qdrant")
public class QdrantProperties {
    private String host = "http://localhost:6333";
    private String apiKey = "qdrant-key";  // Maps to qdrant.api-key in YAML
    private String collection = "tech_support_kb";

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }
}

