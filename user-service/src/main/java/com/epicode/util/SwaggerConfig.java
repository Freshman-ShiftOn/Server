package com.epicode.util;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "User Service API 명세서",
                     description = "Crewezy User Service RESTful API 명세서",
                     version ="v1.0.0")
)
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi salaryApi() {
        return GroupedOpenApi.builder()
                .group("Salary API")
                .pathsToMatch("/api/salaries/**")
                .build();
    }

    @Bean
    public GroupedOpenApi webAuthApi() {
        return GroupedOpenApi.builder()
                .group("Web Auth(일반로그인) API")
                .pathsToMatch("/api/web/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("Kakao Auth API")
                .pathsToMatch("/api/auth/**")
                .build();
    }
}
