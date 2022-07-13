package com.example.athena;

import com.example.athena.common.PreparedStatement;
import com.example.athena.common.View;
import com.example.athena.config.ConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@SpringBootApplication
@EnableConfigurationProperties(ConfigProperties.class)
@EnableCaching
public class AthenaApplication {
    private final PreparedStatement preparedStatement;

    private final View view;

    @Autowired
    public AthenaApplication(PreparedStatement preparedStatement, View view) {
        this.preparedStatement = preparedStatement;
        this.view = view;
    }

    public static void main(String[] args) {
        SpringApplication.run(AthenaApplication.class, args);
    }

    @Bean
    void CreatePreparedStatement() {
        preparedStatement.CreatePreparedStatement();
    }

    @Bean
    void createView() {
        view.CreateView();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.create(connectionFactory);
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues();
    }

}