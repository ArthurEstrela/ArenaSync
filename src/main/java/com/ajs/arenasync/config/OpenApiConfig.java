// Exemplo de src/main/java/com/ajs/arenasync/config/OpenApiConfig.java (crie a pasta 'config')
package com.ajs.arenasync.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ArenaSync RESTful API") // Título da sua API
                        .version("v0.0.1") // Versão da sua API
                        .description("API para gerenciamento de torneios, times e jogadores.") // Sua descrição
                        .termsOfService("http://arenasync.com/terms") // Exemplo
                        .license(
                                new License()
                                        .name("Apache 2.0")
                                        .url("http://arenasync.com/license") // Exemplo
                        )
                );
    }
}