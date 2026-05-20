package kh.edu.istad.stadoor.gateway.client.gateway;

import kh.edu.istad.stadoor.gateway.client.gateway.dto.GatewayMetadataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
public class GatewayServiceClient {

    private final WebClient webClient;

    public GatewayServiceClient(
            @Qualifier("defaultWebClientBuilder") WebClient.Builder webClientBuilder,
            @Value("${stadoor.clients.gateway-service.base-url:http://localhost:16803}") String baseUrl
    ) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

//    public Mono<GatewayMetadataResponse> getGatewayRuntime(String gatewayId) {
//        return webClient.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/api/runtime/gateways/{gatewayId}")
//                        .build(gatewayId))
//                .retrieve()
//                .bodyToMono(GatewayMetadataResponse.class);
//    }

    public Mono<GatewayMetadataResponse> getGatewayRuntimeByTenantAndName(UUID tenantId, String gatewayName) {
        log.info("Calling gateway-service runtime endpoint: tenantId={}, gatewayName={}", tenantId, gatewayName);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/runtime/tenants/{tenantId}/gateways/{gatewayName}")
                        .build(tenantId, gatewayName))
                .retrieve()
                .bodyToMono(GatewayMetadataResponse.class);
    }
}
