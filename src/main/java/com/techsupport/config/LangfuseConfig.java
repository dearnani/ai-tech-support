package com.techsupport.config;

import io.langfuse.client.Lib;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class LangfuseConfig {

    private final LangfuseProperties langfuseProperties;

    @Bean
    public Lib langfuseClient() {
        return new Lib(langfuseProperties.getPublicKey(), 
                      langfuseProperties.getSecretKey(), 
                      langfuseProperties.getHost());
    }
}
