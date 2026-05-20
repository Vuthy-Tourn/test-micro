package kh.edu.istad.stadoor.gateway.runtime.port.input;

import kh.edu.istad.stadoor.gateway.runtime.dto.GatewayRuntimeResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RuntimeQueryInputPort {

    Mono<GatewayRuntimeResponse> getGatewayRuntime(UUID gatewayId);

    Mono<GatewayRuntimeResponse> getGatewayRuntimeByTenantAndName(UUID tenantId, String gatewayName);
}
