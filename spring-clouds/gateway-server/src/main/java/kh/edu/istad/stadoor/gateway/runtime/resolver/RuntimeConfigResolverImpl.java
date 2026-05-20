package kh.edu.istad.stadoor.gateway.runtime.resolver;

import kh.edu.istad.stadoor.gateway.client.gateway.GatewayServiceClient;
import kh.edu.istad.stadoor.gateway.client.gateway.mapper.GatewayRuntimeMapper;
import lombok.extern.slf4j.Slf4j;
import kh.edu.istad.stadoor.gateway.runtime.model.ResolvedRuntimeConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class RuntimeConfigResolverImpl implements RuntimeConfigResolver {

    private final GatewayServiceClient gatewayServiceClient;
    private final GatewayRuntimeMapper gatewayRuntimeMapper;

    public RuntimeConfigResolverImpl(
            GatewayServiceClient gatewayServiceClient,
            GatewayRuntimeMapper gatewayRuntimeMapper
    ) {
        this.gatewayServiceClient = gatewayServiceClient;
        this.gatewayRuntimeMapper = gatewayRuntimeMapper;
    }

    @Override
    public Mono<ResolvedRuntimeConfig> resolve(ServerRequest request) {
        List<String> segments = Arrays.stream(request.path().split("/"))
                .filter(segment -> !segment.isBlank())
                .toList();

        if (segments.size() < 2) {
            return Mono.error(new IllegalArgumentException("Invalid request path: expected /{tenantId}/{gatewayName}/..."));
        }

        UUID tenantId;
        try {
            tenantId = UUID.fromString(segments.get(0));
        } catch (IllegalArgumentException ex) {
            return Mono.error(new IllegalArgumentException("Invalid tenantId path segment"));
        }

        String gatewayName = segments.get(1);
        String remainingPath = "/" + String.join("/", segments.subList(2, segments.size()));
        log.info("Resolved runtime request path: tenantId={}, gatewayName={}, runtimePath={}",
                tenantId, gatewayName, remainingPath);

        return gatewayServiceClient.getGatewayRuntimeByTenantAndName(tenantId, gatewayName)
                .doOnNext(metadata -> log.info(
                        "Runtime metadata received: gatewayId={}, servicesCount={}, routesCount={}",
                        metadata.gatewayId(),
                        metadata.services() == null ? 0 : metadata.services().size(),
                        metadata.routes() == null ? 0 : metadata.routes().size()
                ))
                .map(gatewayRuntimeMapper::toRuntimeConfig)
                .map(runtimeConfig -> new ResolvedRuntimeConfig(runtimeConfig, remainingPath));
    }
}
