package com.yian.jwt_authflow.utility;

import com.yian.jwt_authflow.config.TokenConfigurationProperties;
import com.yian.jwt_authflow.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Component
@EnableConfigurationProperties(TokenConfigurationProperties.class)
public class JwtUtility {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String SCOPE_CLAIM_NAME = "scp";

    private final TokenConfigurationProperties tokenConfigurationProperties;
    private final String issuer;

    public JwtUtility(TokenConfigurationProperties tokenConfigurationProperties,
                      @Value("") final String issuer
    ) {
        this.tokenConfigurationProperties = tokenConfigurationProperties;
        this.issuer = issuer;
    }
    public String generateAccessToken(@NotNull final User user) {
        return "";
    }

    public UUID extractUserId(@NotNull final String token) {
        return UUID.fromString(token.substring(BEARER_PREFIX.length()));
    }

    public List<GrantedAuthority> getAuthority(@NotNull final String token) {
        return null;
    }

    public Duration getTimeUntilExpiration(@NotNull final String token) {
        return null;
    }
}
