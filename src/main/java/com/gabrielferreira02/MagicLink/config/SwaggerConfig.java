package com.gabrielferreira02.MagicLink.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Magic link api",
                description = "Api that provides login access from magic link sent by email",
                contact = @Contact(
                        name = "Gabriel Ferreira",
                        url = "https://www.linkedin.com/in/gabriel-ferreira-5414382a8/",
                        email = "gabrielf.04.2002@gmail.com"
                ),
                version = "1.0.0"
        ),
        servers = @Server(
                url = "http://localhost:8080",
                description = "DEVELOPMENT SERVER"
        )
)
public class SwaggerConfig {
}
