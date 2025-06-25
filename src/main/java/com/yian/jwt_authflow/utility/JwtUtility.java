package com.yian.jwt_authflow.utility;

import com.yian.jwt_authflow.config.TokenConfigurationProperties;
import com.yian.jwt_authflow.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        final var jti = String.valueOf(UUID.randomUUID());
        final var audience = String.valueOf(user.getId());
        final var accessTokenValidity = tokenConfigurationProperties.getAccessToken().getValidity();
        final var expiration = TimeUnit.MINUTES.toMillis(accessTokenValidity);
        final var currentTimeStamp = new Date(System.currentTimeMillis());
        final var expirationTimeStamp = new Date(System.currentTimeMillis() + expiration);
        final var scopes = user.getUserStatus()
                .getScopes()
                .stream()
                .collect(Collectors.joining(StringUtils.SPACE));

        final var claims = new HashMap<String, String>();
        claims.put(SCOPE_CLAIM_NAME, scopes);

        return Jwts.builder()
                .claims(claims)
                .id(jti)
                .issuer(issuer)
                .issuedAt(currentTimeStamp)
                .expiration(expirationTimeStamp)
                .audience().add(audience)
                .and()
                .signWith(getPrivateKey(), Jwts.SIG.RS512) // 프라이빗 키 값을 여기에 넣어줌
                .compact();
    }

    public UUID extractUserId(@NotNull final String token) {
        final var audience = extractClaim(token, Claims::getAudience).iterator().next();

        return UUID.fromString(audience);
    }
    public String jti(@NotNull final String token) {
        return extractClaim(token, Claims::getId);


    }



    public List<GrantedAuthority> getAuthority(@NotNull final String token) {
        final var scopes = extractClaim(token, claims -> claims.get(SCOPE_CLAIM_NAME, String.class));
        return Arrays.stream(scopes.split(StringUtils.SPACE))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

    }

    public Duration getTimeUntilExpiration(@NotNull final String token) {
        final var expirationTimeStamp = extractClaim(token, Claims::getExpiration).toInstant() ;
        final var currentTimeStamp = new Date().toInstant();

        return Duration.between(currentTimeStamp, expirationTimeStamp);
    }

    private <T> T extractClaim(
            @NonNull final String token,
            @NonNull final Function<Claims, T> claimsResolver
    ){
        final var sanitizedToken = token.replace(BEARER_PREFIX, StringUtils.EMPTY);
        final var claims = Jwts.parser()
                .requireIssuer(issuer)
                .verifyWith(getPublicKey())
                .build()
                .parseSignedClaims(sanitizedToken)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    @SneakyThrows
    private PrivateKey getPrivateKey() {
        final var privateKey = tokenConfigurationProperties.getAccessToken().getPrivateKey();
        final var sanitizedPrivateKey = sanitizeKey(privateKey);

        final var decodedPrivateKey = Decoders.BASE64.decode(sanitizedPrivateKey);
        final var spec = new PKCS8EncodedKeySpec(decodedPrivateKey);

        return KeyFactory.getInstance("RSA").generatePrivate(spec);

    }

    @SneakyThrows
    private PublicKey getPublicKey() {
        final var publicKey = tokenConfigurationProperties.getAccessToken().getPublicKey();
        final var sanitizedPublicKey = sanitizeKey(publicKey);

        final var decodedPublicKey = Decoders.BASE64.decode(sanitizedPublicKey);
        final var spec = new PKCS8EncodedKeySpec(decodedPublicKey);

        return KeyFactory.getInstance("RSA").generatePublic(spec);

    }
    private String sanitizeKey(@NonNull final String key) {
        return key
                .replace("-----BEGIN PUBLIC KEY-----", StringUtils.EMPTY)
                .replace("-----END PUBLIC KEY-----", StringUtils.EMPTY)
                .replace("-----BEGIN PRIVATE KEY-----", StringUtils.EMPTY)
                .replace("-----END PRIVATE KEY-----", StringUtils.EMPTY)
                .replaceAll("\\n", StringUtils.EMPTY)
                .replaceAll("\\s", StringUtils.EMPTY);
    }
}
