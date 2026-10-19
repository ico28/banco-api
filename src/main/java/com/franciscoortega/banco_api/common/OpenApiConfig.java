package com.franciscoortega.banco_api.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bancoOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Banco API")
                .description("API REST de un banco simplificado: clientes, cuentas, movimientos y transferencias")
                .version("1.0.0"));
    }
}