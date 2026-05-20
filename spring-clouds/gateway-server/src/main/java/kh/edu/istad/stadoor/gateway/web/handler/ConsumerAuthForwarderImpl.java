package kh.edu.istad.stadoor.gateway.web.handler;

import kh.edu.istad.stadoor.gateway.runtime.model.ResolvedRuntimeConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ConsumerAuthForwarderImpl implements ConsumerAuthForwarder {

    private final WebClient webClient;
    private static final ParameterizedTypeReference<Map<String, Object>> JSON_OBJECT =
            new ParameterizedTypeReference<>() {
            };

    public ConsumerAuthForwarderImpl(
            @Qualifier("defaultWebClientBuilder") WebClient.Builder webClientBuilder,
            @Value("${stadoor.clients.consumer.base-url:http://localhost:16804}") String baseUrl
    ) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    public Mono<ServerResponse> forward(ServerRequest request, ResolvedRuntimeConfig config) {
        String remainingPath = config.remainingPath();
        String action = remainingPath.substring("/auth/".length());

        UUID tenantId = config.runtimeConfig().tenantId();
        UUID gatewayId = config.runtimeConfig().gatewayId();

        return switch (action) {
            case "register" -> proxyWithInjection(request, "/api/v1/consumers", tenantId, gatewayId);
            case "login" -> proxyWithGatewayId(request, "/api/v1/consumers/login", gatewayId);
            case "refresh" -> proxyDirect(request, "/api/v1/consumers/refresh");
            case "logout" -> proxyDirect(request, "/api/v1/consumers/logout");
            default -> ServerResponse.status(HttpStatus.NOT_FOUND)
                    .bodyValue(Map.of("code", "NOT_FOUND", "message", "Unknown auth endpoint: /auth/" + action));
        };
    }

    private Mono<ServerResponse> proxyWithInjection(ServerRequest request, String path, UUID tenantId, UUID gatewayId) {
        return request.bodyToMono(JSON_OBJECT)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Request body is required")))
                .flatMap(body -> {
                    Map<String, Object> enrichedBody = new LinkedHashMap<>(body);
                    enrichedBody.put("tenantId", tenantId.toString());
                    enrichedBody.put("gatewayId", gatewayId.toString());
                    return proxyToConsumer(path, enrichedBody);
                });
    }

    private Mono<ServerResponse> proxyWithGatewayId(ServerRequest request, String path, UUID gatewayId) {
        return request.bodyToMono(JSON_OBJECT)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Request body is required")))
                .flatMap(body -> {
                    Map<String, Object> enrichedBody = new LinkedHashMap<>(body);
                    enrichedBody.put("gatewayId", gatewayId.toString());
                    return proxyToConsumer(path, enrichedBody);
                });
    }

    private Mono<ServerResponse> proxyDirect(ServerRequest request, String path) {
        return request.bodyToMono(JSON_OBJECT)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Request body is required")))
                .flatMap(body -> proxyToConsumer(path, body));
    }

    private Mono<ServerResponse> proxyToConsumer(String path, Map<String, Object> body) {
        return webClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchangeToMono(clientResponse ->
                        clientResponse.bodyToMono(byte[].class)
                                .defaultIfEmpty(new byte[0])
                                .flatMap(bytes -> ServerResponse.status(clientResponse.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(bytes)));
    }
}
