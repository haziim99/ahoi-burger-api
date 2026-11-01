package com.menu.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Ahoi Burger API")
                .version("1.0.0")
                .description("Query burgers and drinks of a fictional burger restaurant. Authenticate with the X-API-Key header (use the Authorize button)."),
        )
        .components(
            Components().addSecuritySchemes(
                "apiKey",
                SecurityScheme().type(SecurityScheme.Type.APIKEY).`in`(SecurityScheme.In.HEADER).name(ApiKeyInterceptor.HEADER),
            ),
        )
        .addSecurityItem(SecurityRequirement().addList("apiKey"))
}
