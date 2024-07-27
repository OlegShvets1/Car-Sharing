package mate.academy.carsharing.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearer";
    private static final String BEARER_FORMAT = "JWT";
    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme(BEARER_SCHEME)
                                .bearerFormat(BEARER_FORMAT)))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}
