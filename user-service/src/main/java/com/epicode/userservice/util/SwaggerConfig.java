package com.epicode.userservice.util;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "Auth Service API 명세서",
                     description = "Crewezy OAuth2.0 Service RESTful API 명세서",
                     version ="v1.0.0")
)
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi customTestOpenAPI() {
        String[] paths = {"/api/manuals/**"};

        return GroupedOpenApi.builder()
                .group("인증 service 위한 API")
                .pathsToMatch(paths)
                .build();
    }
}
