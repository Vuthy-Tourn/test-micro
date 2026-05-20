package kh.edu.istad.stadoor.consumer.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kh.edu.istad.stadoor.consumer.dto.request.*;
import kh.edu.istad.stadoor.consumer.dto.response.*;
import kh.edu.istad.stadoor.consumer.exception.DomainNotFoundException;
import kh.edu.istad.stadoor.consumer.exception.DomainUnauthorizedException;
import kh.edu.istad.stadoor.consumer.port.input.ConsumerQueryPort;
import kh.edu.istad.stadoor.consumer.port.output.ConsumerQueryRepository;
import kh.edu.istad.stadoor.consumer.projection.ConsumerCredentialProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerQueryService implements ConsumerQueryPort {

    private static final String DUMMY_HASH = "$2a$12$XkMU2HyrqC0wPxmFT1B8puWNoQVKennFVqcBLwfCcuG911GfWPgNe";
    private static final ConsumerCredentialProjection DUMMY_CREDENTIAL = new ConsumerCredentialProjection(
            null, null, null, DUMMY_HASH, null
    );

    private final ConsumerQueryRepository queryRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Integer jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private Integer jwtRefreshExpiration;

    @Override
    public Mono<ConsumerResponse> findById(UUID id) {
        return queryRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new DomainNotFoundException("Consumer not found: " + id)));
    }

    @Override
    public Flux<ConsumerResponse> findByGatewayId(UUID gatewayId) {
        return queryRepository.findByGatewayId(gatewayId);
    }

    @Override
    public Flux<ConsumerResponse> findByTenantId(UUID tenantId) {
        return queryRepository.findByTenantId(tenantId);
    }

    @Override
    public Mono<ValidateCredentialResponse> validateCredential(ValidateCredentialInput input) {
        return switch (input.authType()) {
            case BASIC_AUTH -> queryRepository.findBasicAuthByUsername(input.username(), input.gatewayId())
                    .switchIfEmpty(Mono.just(DUMMY_CREDENTIAL))
                    .filter(p -> {
                        boolean matches = passwordEncoder.matches(input.password(), p.credentialHash());
                        if (!matches) {
                            log.warn("Failed BASIC auth attempt for username: {}", input.username());
                        }
                        return matches;
                    })
                    .filter(p -> p.consumerId() != null)
                    .flatMap(p -> queryRepository.findRoleNamesByConsumerId(p.consumerId())
                            .collectList()
                            .map(roles -> ValidateCredentialResponse.builder()
                                    .valid(true)
                                    .consumerId(p.consumerId())
                                    .tenantId(p.tenantId())
                                    .gatewayId(p.gatewayId())
                                    .roles(roles)
                                    .build()))
                    .defaultIfEmpty(ValidateCredentialResponse.builder().valid(false).build());

            case API_KEY -> queryRepository.findApiKeyByHash(hashWithSha256(input.apiKey()))
                    .switchIfEmpty(Mono.fromRunnable(() ->
                            log.warn("Failed API_KEY auth attempt")))
                    .flatMap(p -> queryRepository.findRoleNamesByConsumerId(p.consumerId())
                            .collectList()
                            .map(roles -> ValidateCredentialResponse.builder()
                                    .valid(true)
                                    .consumerId(p.consumerId())
                                    .tenantId(p.tenantId())
                                    .gatewayId(p.gatewayId())
                                    .roles(roles)
                                    .build()))
                    .defaultIfEmpty(ValidateCredentialResponse.builder().valid(false).build());

            case JWT -> {
                try {
                    SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
                    Claims claims = Jwts.parser()
                            .verifyWith(key)
                            .build()
                            .parseSignedClaims(input.token())
                            .getPayload();
                    String type = claims.get("type", String.class);
                    if (!"access".equals(type)) {
                        yield Mono.just(ValidateCredentialResponse.builder().valid(false).build());
                    }
                    String username = claims.getSubject();
                    if (username == null) {
                        yield Mono.just(ValidateCredentialResponse.builder().valid(false).build());
                    }
                    yield queryRepository.findJwtByUsername(username, input.gatewayId())
                            .flatMap(p -> queryRepository.findRoleNamesByConsumerId(p.consumerId())
                                    .collectList()
                                    .map(roles -> ValidateCredentialResponse.builder()
                                            .valid(true)
                                            .consumerId(p.consumerId())
                                            .tenantId(p.tenantId())
                                            .gatewayId(p.gatewayId())
                                            .roles(roles)
                                            .build()))
                            .defaultIfEmpty(ValidateCredentialResponse.builder().valid(false).build());
                } catch (JwtException e) {
                    log.warn("JWT validation failed: {}", e.getMessage());
                    yield Mono.just(ValidateCredentialResponse.builder().valid(false).build());
                }
            }
        };
    }

    @Override
    public Mono<LoginResponse> login(LoginInput loginInput) {
        return queryRepository.findJwtByUsername(loginInput.username(), loginInput.gatewayId())
                .switchIfEmpty(Mono.error(new DomainUnauthorizedException("Invalid username or password")))
                .flatMap(c -> {
                    if (!passwordEncoder.matches(loginInput.password(), c.credentialHash())) {
                        return Mono.error(new DomainUnauthorizedException("Invalid username or password"));
                    }
                    return Mono.just(c);
                })
                .flatMap(c -> {
                    Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
                    Date now = new Date();
                    Date jwtExpiry = new Date(now.getTime() + jwtExpiration * 1000L);
                    Date jwtRefreshExpiry = new Date(now.getTime() + jwtRefreshExpiration * 1000L);

                    String accessToken = Jwts.builder()
                            .subject(loginInput.username())
                            .issuedAt(now)
                            .expiration(jwtExpiry)
                            .signWith(key)
                            .claim("type", "access")
                            .compact();

                    String refreshToken = Jwts.builder()
                            .subject(loginInput.username())
                            .issuedAt(now)
                            .expiration(jwtRefreshExpiry)
                            .signWith(key)
                            .claim("type", "refresh")
                            .compact();

                    return queryRepository.saveRefreshToken(new SaveRefreshTokenRequest(UUID.randomUUID(),
                            c.jwtCredentialId(), hashWithSha256(refreshToken), jwtRefreshExpiry.toInstant(),
                            Instant.now()))
                            .onErrorResume(e -> {
                                log.error("Failed to save refresh token during login for consumerId={}", c.consumerId(), e);
                                return Mono.error(new DomainUnauthorizedException("Login failed, please try again"));
                            })
                            .thenReturn(new LoginResponse(accessToken, refreshToken, jwtExpiration));
                });
    }

    @Override
    public Mono<RefreshResponse> refresh(RefreshInput request) {
        return queryRepository.findConsumerInfoByTokenHash(hashWithSha256(request.refreshToken()))
                .switchIfEmpty(Mono.error(new DomainUnauthorizedException("Invalid refresh token")))
                .flatMap(r -> {
                    Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
                    Date now = new Date();
                    Date jwtExpiry = new Date(now.getTime() + jwtExpiration * 1000L);
                    Date jwtRefreshExpiry = new Date(now.getTime() + jwtRefreshExpiration * 1000L);

                    String accessToken = Jwts.builder()
                            .subject(r.username())
                            .issuedAt(now)
                            .expiration(jwtExpiry)
                            .signWith(key)
                            .claim("type", "access")
                            .compact();

                    String newRefreshToken = Jwts.builder()
                            .subject(r.username())
                            .issuedAt(now)
                            .expiration(jwtRefreshExpiry)
                            .signWith(key)
                            .claim("type", "refresh")
                            .compact();

                    return queryRepository.saveRefreshToken(new SaveRefreshTokenRequest(UUID.randomUUID(),
                                    r.jwtCredentialId(), hashWithSha256(newRefreshToken), jwtRefreshExpiry.toInstant(),
                                    Instant.now()))
                            .onErrorResume(e -> {
                                log.error("Failed to save new refresh token during refresh for consumerId={}", r.consumerId(), e);
                                return Mono.error(new DomainUnauthorizedException("Token refresh failed, please try again"));
                            })
                            .then(queryRepository.revokeRefreshToken(hashWithSha256(request.refreshToken())))
                            .thenReturn(new RefreshResponse(accessToken, newRefreshToken, jwtExpiration));
                });
    }

    @Override
    public Mono<Void> logout(LogoutInput logoutInput) {
        return queryRepository.revokeRefreshToken(hashWithSha256(logoutInput.refreshToken()));
    }

    @Override
    public Mono<ConsumerAuthTypeCountResponse> countConsumersByAuthType() {
        return queryRepository.countConsumersByAuthType();
    }

    private String hashWithSha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
