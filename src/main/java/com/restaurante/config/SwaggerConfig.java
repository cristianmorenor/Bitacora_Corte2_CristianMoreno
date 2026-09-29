package com.restaurante.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI restauranteOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API LA BRASA VIVA")
                        .description("API REST para gestionar la carta, mesas, pedidos y pagos de un restaurante de parrilla")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Cristian Santiago Moreno Ruiz")
                                .email("cristian.moreno-r@mail.escuelaing.edu.co")));
    }
}