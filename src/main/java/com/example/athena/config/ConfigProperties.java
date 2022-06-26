package com.example.athena.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "athena")
public class ConfigProperties {
    private String region;
    private String workGroup;
    private String catalog;
    private String database;
    private String resultsBucket;
    private String namedQueryId;
    private int clientExecutionTimeout;
    private int limit;
    private int retrySleep;
}