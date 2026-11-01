package com.menu.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(private val apiKeyInterceptor: ApiKeyInterceptor) : WebMvcConfigurer {

    // Only the API is protected; Swagger UI, /v3/api-docs and static images stay public.
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(apiKeyInterceptor).addPathPatterns("/api/**")
    }
}
