package com.yian.jwt_authflow.config;

import io.swagger.v3.oas.models.OpenAPI;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "com.yian.jwtauthflow")
public class OpenApiConfigurationProperties {

    private OpenAPI openAPI = new OpenAPI();

    @Getter
    @Setter
    public class OpenAPI {
        private Boolean enabled;
        private String title;
        private String description;
        private String apiVersion;
    }
}
