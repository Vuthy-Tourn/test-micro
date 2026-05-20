package kh.edu.istad.stadoor.frontbff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${stadoor.frontend.public-origin:http://localhost:3000}")
    private String publicOrigin;

    @Value("${stadoor.frontend.gateway-origin:http://localhost:3001}")
    private String gatewayOrigin;

    @Value("${stadoor.frontend.iam-origin:http://localhost:3002}")
    private String iamOrigin;

    @Value("${stadoor.security.logout-origin:${stadoor.security.issuer-uri:http://localhost:16801}}")
    private String logoutOrigin;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ReactiveClientRegistrationRepository clientRegistrationRepository)
    {

        http.authorizeExchange(exchange -> exchange
                .pathMatchers("/dashboard/**").authenticated()
                .pathMatchers("/api/front/**").authenticated()
                .anyExchange().permitAll());

        // Prevent 302 redirects for API calls (return 401 instead)
        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint((exchange, e) -> {
                    if (exchange.getRequest().getPath().value().startsWith("/api/")) {
                        return new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED).commence(exchange, e);
                    }
                    return new RedirectServerAuthenticationEntryPoint("/oauth2/authorization/stadoor").commence(exchange, e);
                })
        );

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.formLogin(ServerHttpSecurity.FormLoginSpec::disable);
        http.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);


        // for flow gateway to issuer token from oauth2-server
        http.oauth2Login(oauth2 -> oauth2
                .authenticationSuccessHandler(
                        new RedirectServerAuthenticationSuccessHandler(
                                normalizeOrigin(publicOrigin) + "/dashboard/overview"
                        )
                ));

        http.logout(logoutSpec -> logoutSpec
                .requiresLogout(ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, "/logout"))
                .logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))
        );
        return http.build();
    }

    // for Client Logout
    public ServerLogoutSuccessHandler serverLogoutSuccessHandler() {
        RedirectServerLogoutSuccessHandler handler = new RedirectServerLogoutSuccessHandler();
        handler.setLogoutSuccessUrl(URI.create("/"));
        return handler;
    }

    // for OIDC logout
    @Bean
    public ServerLogoutSuccessHandler oidcLogoutSuccessHandler(
            ReactiveClientRegistrationRepository clientRegistrationRepository) {

        RedirectServerLogoutSuccessHandler handler = new RedirectServerLogoutSuccessHandler();
        // Redirect to IAM manual logout page with confirmation
        handler.setLogoutSuccessUrl(URI.create(
                normalizeOrigin(logoutOrigin) + "/logout?continue=" + normalizeOrigin(publicOrigin)
        ));

        return handler;
    }

    @Bean
    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ServerOAuth2AuthorizedClientRepository authorizedClientRepository
    ) {
        ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider =
                ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()
                        .refreshToken()
                        .clientCredentials()
                        .build();

        DefaultReactiveOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultReactiveOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Stream.of(publicOrigin, gatewayOrigin, iamOrigin)
                .map(this::normalizeOrigin)
                .filter(origin -> !origin.isBlank())
                .distinct()
                .toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public WebSessionIdResolver webSessionIdResolver() {
        CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();
        resolver.setCookieName("AUTHGATE_SESSION");
        resolver.addCookieInitializer((builder) -> builder.path("/"));
        resolver.addCookieInitializer((builder -> builder.httpOnly(true)));
        resolver.addCookieInitializer((builder) -> builder.secure(true));
        resolver.addCookieInitializer((builder) -> builder.sameSite("Lax"));
        return resolver;
    }

    private String normalizeOrigin(String origin) {
        if (origin == null || origin.isBlank()) {
            return "";
        }
        return origin.endsWith("/") ? origin.substring(0, origin.length() - 1) : origin;
    }
}
