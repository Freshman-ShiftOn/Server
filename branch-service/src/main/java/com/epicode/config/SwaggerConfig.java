package com.epicode.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "Branch Service API 명세서",
                     description = "Crewezy Branch Service RESTful API 명세서",
                     version ="v1.0.0")
)
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi customTestOpenAPI() {
        String[] paths = {"/api/branch/**"};

        return GroupedOpenApi.builder()
                .group("Branch service 위한 API")
                .pathsToMatch(paths)
                .build();
    }
}
