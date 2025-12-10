package com.ruoyi.framework.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * SpringDoc OpenAPI 接口配置
 * 
 * @author ruoyi
 */
@Configuration
@ConditionalOnProperty(name = "swagger.enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerConfig
{
    /**
     * 创建OpenAPI配置
     */
    @Bean
    public OpenAPI customOpenAPI()
    {
        return new OpenAPI()
                .info(new Info()
                        .title("若依管理系统 API")
                        .version("3.9.0")
                        .description("若依管理系统接口文档")
                        .license(new License().name("MIT")))
                .externalDocs(new ExternalDocumentation()
                        .description("若依官网")
                        .url("http://www.ruoyi.vip"))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .components(new Components()
                        .addSecuritySchemes("Authorization",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    /**
     * 系统模块 API 分组
     */
    @Bean
    public GroupedOpenApi systemApi()
    {
        return GroupedOpenApi.builder()
                .group("1-系统模块")
                .pathsToMatch("/system/**", "/monitor/**")
                .build();
    }

    /**
     * 通用模块 API 分组
     */
    @Bean
    public GroupedOpenApi commonApi()
    {
        return GroupedOpenApi.builder()
                .group("2-通用模块")
                .pathsToMatch("/common/**", "/captchaImage", "/login", "/register", "/logout")
                .build();
    }

    /**
     * 代码生成模块 API 分组
     */
    @Bean
    public GroupedOpenApi toolApi()
    {
        return GroupedOpenApi.builder()
                .group("3-工具模块")
                .pathsToMatch("/tool/**")
                .build();
    }

    /**
     * 客户端模块 API 分组
     */
    @Bean
    public GroupedOpenApi clientApi()
    {
        return GroupedOpenApi.builder()
                .group("4-客户端模块")
                .pathsToMatch("/client/**")
                .build();
    }
}
