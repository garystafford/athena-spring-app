package com.example.athena.common;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.athena.AthenaClient;

@Component
public class AthenaClientFactoryImp implements AthenaClientFactory {

    public AthenaClient createClient() {
        return AthenaClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
    }

}