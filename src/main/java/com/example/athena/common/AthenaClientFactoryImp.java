package com.example.athena.common;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;

@Component
public class AthenaClientFactoryImp implements AthenaClientFactory {

    public AthenaClient createClient(String region, String profile) {
        return AthenaClient.builder()
                .region(Region.of(region))
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
    }

}