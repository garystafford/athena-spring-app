package com.example.athena;

import com.example.athena.common.PreparedStatement;
import com.example.athena.common.View;
import com.example.athena.config.ConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableConfigurationProperties(ConfigProperties.class)
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

}