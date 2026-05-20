package kh.edu.istad.stadoor.gateway.runtime.security;

import kh.edu.istad.stadoor.gateway.exception.UnauthorizedRequestException;
import kh.edu.istad.stadoor.gateway.runtime.model.ConsumerContext;
import kh.edu.istad.stadoor.gateway.runtime.model.MatchedRoute;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Component
public class AuthTypeDispatcher {

    private final OAuth2AuthValidator oauth2AuthValidator;
    private final JwtAuthValidator jwtAuthValidator;
    private final ApiKeyAuthValidator apiKeyAuthValidator;
    private final BasicAuthValidator basicAuthValidator;

    public AuthTypeDispatcher(
            OAuth2AuthValidator oauth2AuthValidator,
            JwtAuthValidator jwtAuthValidator,
            ApiKeyAuthValidator apiKeyAuthValidator,
            BasicAuthValidator basicAuthValidator
    ) {
        this.oauth2AuthValidator = oauth2AuthValidator;
        this.jwtAuthValidator = jwtAuthValidator;
        this.apiKeyAuthValidator = apiKeyAuthValidator;
        this.basicAuthValidator = basicAuthValidator;
    }

    public Mono<ConsumerContext> validate(ServerRequest request, MatchedRoute matchedRoute) {
        String authType = matchedRoute.gateway().authType();
        if (authType == null || authType.isBlank()) {
            return Mono.just(ConsumerContext.EMPTY);
        }

        return switch (authType.toUpperCase()) {
            case "OAUTH2" -> oauth2AuthValidator.validate(request, matchedRoute);
            case "JWT" -> jwtAuthValidator.validate(request, matchedRoute);
            case "API_KEY" -> apiKeyAuthValidator.validate(request, matchedRoute);
            case "BASIC_AUTH" -> basicAuthValidator.validate(request, matchedRoute);
            default -> Mono.error(new UnauthorizedRequestException("Unsupported authType: " + authType));
        };
    }
}
