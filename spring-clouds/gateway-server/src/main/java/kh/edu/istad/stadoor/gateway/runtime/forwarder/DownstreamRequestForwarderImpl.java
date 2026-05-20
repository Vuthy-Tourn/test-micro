package kh.edu.istad.stadoor.gateway.runtime.forwarder;

import kh.edu.istad.stadoor.gateway.runtime.model.ConsumerContext;
import kh.edu.istad.stadoor.gateway.runtime.model.MatchedRoute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

@Component
@Slf4j
public class DownstreamRequestForwarderImpl implements DownstreamRequestForwarder {

    private final WebClient.Builder defaultWebClientBuilder;
    private final WebClient.Builder loadBalancedWebClientBuilder;
    private final ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public DownstreamRequestForwarderImpl(
            @Qualifier("defaultWebClientBuilder") WebClient.Builder defaultWebClientBuilder,
            @Qualifier("loadBalancedWebClientBuilder") WebClient.Builder loadBalancedWebClientBuilder,
            ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory
    ) {
        this.defaultWebClientBuilder = defaultWebClientBuilder;
        this.loadBalancedWebClientBuilder = loadBalancedWebClientBuilder;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Override
    public Mono<ServerResponse> forward(ServerRequest request, MatchedRoute matchedRoute, ConsumerContext consumerContext) {
        URI upstreamUri = resolveUpstreamUri(matchedRoute, request.uri().getQuery());

        log.info("Forwarding request method={}, upstreamUri={}", request.method(), upstreamUri);

        String serviceName = matchedRoute.service().name();
        ReactiveCircuitBreaker circuitBreaker = circuitBreakerFactory.create(
                serviceName != null ? serviceName : matchedRoute.service().serviceId().toString()
        );

        WebClient.Builder selectedWebClientBuilder = "lb".equalsIgnoreCase(upstreamUri.getScheme())
                ? loadBalancedWebClientBuilder
                : defaultWebClientBuilder;

        Mono<ServerResponse> upstream = selectedWebClientBuilder.build()
                .method(request.method())
                .uri(upstreamUri)
                .headers(headers -> {
                    headers.addAll(request.headers().asHttpHeaders());
                    headers.remove(HttpHeaders.HOST);
                    if (consumerContext != null && consumerContext.consumerId() != null) {
                        headers.set("X-Consumer-Id", consumerContext.consumerId().toString());
                        headers.set("X-Tenant-Id", consumerContext.tenantId().toString());
                        headers.set("X-Gateway-Id", consumerContext.gatewayId().toString());
                        if (consumerContext.roles() != null && !consumerContext.roles().isEmpty()) {
                            headers.set("X-Consumer-Roles", String.join(",", consumerContext.roles()));
                        }
                    }
                })
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(byte[].class)
                        .defaultIfEmpty(new byte[0])
                        .flatMap(bodyBytes -> {
                            log.info("Downstream response received: status={}, bytes={}",
                                    clientResponse.statusCode().value(), bodyBytes.length);
                            return ServerResponse.status(clientResponse.statusCode())
                                    .headers(headers -> {
                                        headers.addAll(clientResponse.headers().asHttpHeaders());
                                        headers.remove(HttpHeaders.TRANSFER_ENCODING);
                                        headers.remove(HttpHeaders.CONTENT_LENGTH);
                                    })
                                    .bodyValue(bodyBytes);
                        }))
                .doOnError(ex -> log.error(
                        "Downstream forwarding failed: serviceName={}, routeId={}, targetPath={}, upstreamUri={}, errorClass={}, errorMessage={}",
                        matchedRoute.service().name(),
                        matchedRoute.route().routeId(),
                        matchedRoute.route().targetPath(),
                        upstreamUri,
                        ex.getClass().getSimpleName(),
                        ex.getMessage(),
                        ex
                ))
                .timeout(Duration.ofSeconds(30))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(200))
                        .filter(ex -> ex instanceof WebClientRequestException));

        return circuitBreaker.run(upstream, throwable -> ServerResponse
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("code", "CIRCUIT_OPEN", "message", "Upstream service is currently unavailable")));
    }

    private URI resolveUpstreamUri(MatchedRoute matchedRoute, String query) {
        String targetPath = normalizePath(matchedRoute.route().targetPath());
        String serviceName = matchedRoute.service().name();
        String baseUrl = normalizeBaseUrl(matchedRoute.service().baseUrl());

        if (StringUtils.hasText(baseUrl)) {
            UriComponentsBuilder baseBuilder = UriComponentsBuilder.fromUriString(baseUrl);
            String combinedPath = joinPaths(baseBuilder.build().getPath(), targetPath);
            URI uri = baseBuilder
                    .replacePath(combinedPath)
                    .replaceQuery(query)
                    .build(true)
                    .toUri();
            log.info("Resolved downstream URI using baseUrl: {}", uri);
            return uri;
        }

        if (StringUtils.hasText(serviceName)) {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme("lb")
                    .host(serviceName)
                    .path(targetPath)
                    .query(query)
                    .build(true)
                    .toUri();
            log.info("Resolved downstream URI using load-balancer: {}", uri);
            return uri;
        }

        throw new IllegalStateException("Service has neither discovery name nor resolvable baseUrl");
    }

    private String normalizeBaseUrl(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        if (hasScheme(value)) {
            return value;
        }
        if (value.contains(":")
                || "localhost".equalsIgnoreCase(value)
                || value.startsWith("127.")) {
            return "http://" + value;
        }
        return null;
    }

    private boolean hasScheme(String value) {
        return value.startsWith("http://")
                || value.startsWith("https://")
                || value.startsWith("lb://");
    }

    private String normalizePath(String path) {
        if (!StringUtils.hasText(path)) {
            return "/";
        }
        return path.startsWith("/") ? path : "/" + path;
    }

    private String joinPaths(String basePath, String tailPath) {
        String left = StringUtils.hasText(basePath) ? basePath : "";
        String right = StringUtils.hasText(tailPath) ? tailPath : "";

        if (left.endsWith("/") && right.startsWith("/")) {
            return left.substring(0, left.length() - 1) + right;
        }
        if (!left.endsWith("/") && !right.startsWith("/") && StringUtils.hasText(left) && StringUtils.hasText(right)) {
            return left + "/" + right;
        }
        return left + right;
    }
}
