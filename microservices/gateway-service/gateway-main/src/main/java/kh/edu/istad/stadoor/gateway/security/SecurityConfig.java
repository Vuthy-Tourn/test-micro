package kh.edu.istad.stadoor.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain webSecurity(
            ServerHttpSecurity http
    ) {
        http.authorizeExchange(exchange -> exchange
                // Public endpoints (no authentication required)
                .pathMatchers(
                        "/",
                        "/_next/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/webjars/**",
                        "/favicon.ico",
                        "/oauth2/**",
                        "/public/**",
                        "/login/**",
                        "/oauth2/authenticated/me"  // Allow checking auth status
                ).permitAll()

                // Protected endpoints (require authentication)
                .pathMatchers(
                        "/profile/**",
                        "/auth/profile"  // Profile endpoint requires authentication
                ).authenticated()

                // TODO: protect this with ADMIN role
                .pathMatchers("/api/v1/admin/overview").permitAll()

                .pathMatchers("/api/runtime/**").permitAll()

                // All other endpoints are permitted by default
                .anyExchange().authenticated()
        );

        // CORS configuration for Next.js frontend
        http.cors(cors -> cors.disable());

        // Disable CSRF (BFF handles this differently with same-site cookies)
        http.csrf(csrfSpec -> csrfSpec.disable());

        // Disable form login (using OAuth2 login instead)
        http.formLogin(formLoginSpec -> formLoginSpec.disable());

        // Disable HTTP Basic (using OAuth2 login instead)
        http.httpBasic(httpBasicSpec -> httpBasicSpec.disable());

        // Configure OAuth2 Resource Server to use JWTs with custom Role Mapping
        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
        );

        return http.build();
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        
        // Wrap the standard converter in a Reactive adapter for WebFlux
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }



}
