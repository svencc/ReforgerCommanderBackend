package com.recom.api.configuration;

import com.recom.api.commons.HttpCommons;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                title = "RECOM Backend API",
                version = "0.0.1",
                description = "RECOM API v1"
        )
)
@SecurityScheme(
        name = HttpCommons.BEARER_AUTHENTICATION_REQUIREMENT,
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER,
        scheme = HttpCommons.BEARER_SCHEME,
        bearerFormat = HttpCommons.BEARER_FORMAT
)
public class OpenApiConfiguration {
}
