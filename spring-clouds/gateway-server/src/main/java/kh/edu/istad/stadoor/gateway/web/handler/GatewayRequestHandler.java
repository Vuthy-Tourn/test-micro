package kh.edu.istad.stadoor.gateway.web.handler;

import kh.edu.istad.stadoor.gateway.exception.RouteNotFoundException;
import kh.edu.istad.stadoor.gateway.runtime.forwarder.DownstreamRequestForwarder;
import kh.edu.istad.stadoor.gateway.runtime.matcher.RouteMatcher;
import kh.edu.istad.stadoor.gateway.runtime.model.ConsumerContext;
import kh.edu.istad.stadoor.gateway.runtime.model.GatewayRuntimeConfig;
import kh.edu.istad.stadoor.gateway.runtime.model.MatchedRoute;
import kh.edu.istad.stadoor.gateway.runtime.model.ResolvedRuntimeConfig;
import kh.edu.istad.stadoor.gateway.runtime.resolver.RuntimeConfigResolver;
import kh.edu.istad.stadoor.gateway.runtime.security.AuthTypeDispatcher;
import kh.edu.istad.stadoor.gateway.runtime.security.RateLimiter;
import kh.edu.istad.stadoor.gateway.runtime.validator.RouteSecurityValidator;
import kh.edu.istad.stadoor.gateway.runtime.validator.StatusValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class GatewayRequestHandler {

    private final RuntimeConfigResolver runtimeConfigResolver;
    private final RouteMatcher routeMatcher;
    private final StatusValidator statusValidator;
    private final RouteSecurityValidator routeSecurityValidator;
    private final AuthTypeDispatcher authTypeDispatcher;
    private final DownstreamRequestForwarder downstreamRequestForwarder;
    private final ConsumerAuthForwarder consumerAuthForwarder;
    private final RateLimiter rateLimiter;

    public GatewayRequestHandler(
            RuntimeConfigResolver runtimeConfigResolver,
            RouteMatcher routeMatcher,
            StatusValidator statusValidator,
            RouteSecurityValidator routeSecurityValidator,
            AuthTypeDispatcher authTypeDispatcher,
            DownstreamRequestForwarder downstreamRequestForwarder,
            ConsumerAuthForwarder consumerAuthForwarder,
            RateLimiter rateLimiter) {
        this.runtimeConfigResolver = runtimeConfigResolver;
        this.routeMatcher = routeMatcher;
        this.statusValidator = statusValidator;
        this.routeSecurityValidator = routeSecurityValidator;
        this.authTypeDispatcher = authTypeDispatcher;
        this.downstreamRequestForwarder = downstreamRequestForwarder;
        this.consumerAuthForwarder = consumerAuthForwarder;
        this.rateLimiter = rateLimiter;
    }

    public Mono<ServerResponse> handle(ServerRequest request) {
        return runtimeConfigResolver.resolve(request)
                .flatMap(resolvedConfig -> {
                    if (resolvedConfig.remainingPath().startsWith("/auth/")) {
                        return consumerAuthForwarder.forward(request, resolvedConfig);
                    }
                    return process(resolvedConfig, request);
                });
    }

    private Mono<ServerResponse> process(ResolvedRuntimeConfig resolvedConfig, ServerRequest request) {
        GatewayRuntimeConfig runtimeConfig = resolvedConfig.runtimeConfig();
        Optional<MatchedRoute> matchedRouteOptional = routeMatcher.match(
                runtimeConfig,
                resolvedConfig.remainingPath(),
                request.method()
        );
        MatchedRoute matchedRoute = matchedRouteOptional
                .orElseThrow(() -> new RouteNotFoundException("No route matched for path and method"));

        statusValidator.validate(matchedRoute);

        boolean requiresValidation = routeSecurityValidator.requiresValidation(matchedRoute);
        log.info("Route security decision: routeId={}, routeSecurity={}, requiresValidation={}",
                matchedRoute.route().routeId(), matchedRoute.route().routeSecurity(), requiresValidation);

        Mono<ConsumerContext> authValidation = requiresValidation
                ? authTypeDispatcher.validate(request, matchedRoute)
                : Mono.just(ConsumerContext.EMPTY);

        return authValidation.flatMap(ctx -> {
            if (ctx.consumerId() == null) {
                return downstreamRequestForwarder.forward(request, matchedRoute, ctx);
            }
            return rateLimiter.isAllowed(ctx.consumerId())
                    .flatMap(allowed -> {
                        if (!allowed) {
                            return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(Map.of("code", "RATE_LIMIT_EXCEEDED", "message", "Too many requests, please slow down"));
                        }
                        return downstreamRequestForwarder.forward(request, matchedRoute, ctx);
                    });
        });
    }
}
