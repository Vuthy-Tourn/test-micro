package kh.edu.istad.stadoor.gateway.gateway.ports.output;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GatewayRepositoryOutputPort {
    Mono<Boolean> existByGatewayId(UUID gatewayId);
    Mono<String> findGatewayNameById(UUID gatewayId);
}
