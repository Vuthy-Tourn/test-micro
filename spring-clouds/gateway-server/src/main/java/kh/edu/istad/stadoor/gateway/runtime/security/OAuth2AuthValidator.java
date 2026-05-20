package kh.edu.istad.stadoor.gateway.runtime.security;

import kh.edu.istad.stadoor.gateway.exception.UnauthorizedRequestException;
import kh.edu.istad.stadoor.gateway.runtime.model.ConsumerContext;
import kh.edu.istad.stadoor.gateway.runtime.model.MatchedRoute;
import kh.edu.istad.stadoor.gateway.util.HeaderUtils;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Component
public class OAuth2AuthValidator {

    private final ReactiveJwtDecoder reactiveJwtDecoder;

    public OAuth2AuthValidator(ReactiveJwtDecoder reactiveJwtDecoder) {
        this.reactiveJwtDecoder = reactiveJwtDecoder;
    }

    public Mono<ConsumerContext> validate(ServerRequest request, MatchedRoute matchedRoute) {
        String authorization = HeaderUtils.getHeader(request, "Authorization")
                .orElseThrow(() -> new UnauthorizedRequestException("Missing Authorization header"));

        if (!authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return Mono.error(new UnauthorizedRequestException("Authorization header must use Bearer token"));
        }

        String token = authorization.substring(7).trim();
        if (token.isEmpty()) {
            return Mono.error(new UnauthorizedRequestException("Missing Bearer token"));
        }

        return reactiveJwtDecoder.decode(token)
                .onErrorMap(ex -> new UnauthorizedRequestException("Invalid OAuth2 token"))
                .thenReturn(ConsumerContext.EMPTY);
    }
}
