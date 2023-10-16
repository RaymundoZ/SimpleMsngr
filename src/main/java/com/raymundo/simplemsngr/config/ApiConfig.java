package com.raymundo.simplemsngr.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                title = "SimpleMsngr Api",
                description = "Api for a simple messenger application", version = "1.0.0",
                contact = @Contact(
                        name = "RaymundoZ",
                        url = "https://github.com/RaymundoZ/SimpleMsngr"
                )
        )
)
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class ApiConfig {
}
