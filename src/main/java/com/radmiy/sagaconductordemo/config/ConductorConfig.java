package com.radmiy.sagaconductordemo.config;

import com.netflix.conductor.client.http.ConductorClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConductorConfig {

    @Value("${conductor.server.url}")
    private String rootUri;

    @Bean
    public ConductorClient conductorClient() {
        return ConductorClient.builder()
                .basePath(rootUri)
                .build();
    }
}
