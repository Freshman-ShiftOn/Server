package com.epicode.util;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Crewezy API 명세서",
                description = "Crewezy 급여 RESTful API 문서",
                version = "v1.0.0"
        )
)
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi salaryApi() {
        return GroupedOpenApi.builder()
                .group("salary-api") // 급여 관련
                .pathsToMatch("/api/salaries/**")
                .build();
    }
}