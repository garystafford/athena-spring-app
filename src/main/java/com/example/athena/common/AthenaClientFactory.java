package com.example.athena.common;

import software.amazon.awssdk.services.athena.AthenaClient;

public interface AthenaClientFactory {
    AthenaClient createClient(String region, String profile);
}
