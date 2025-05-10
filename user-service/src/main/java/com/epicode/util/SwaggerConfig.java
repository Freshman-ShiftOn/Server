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
                description = "Crewezy RESTful API 문서",
                version = "v1.0.0"
        )
)
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi webAuthApi() {
        return GroupedOpenApi.builder()
                .group("web-auth-api") // 웹용 인증 관련
                .pathsToMatch("/api/web/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi generalAuthApi() {
        return GroupedOpenApi.builder()
                .group("auth-api") // 일반 인증
                .pathsToMatch("/api/auth/**")
                .build();
    }
    @Bean
    public GroupedOpenApi generalProfileApi() {
        return GroupedOpenApi.builder()
                .group("profile-api")
                .pathsToMatch("/api/profile/**")
                .build();
    }

}