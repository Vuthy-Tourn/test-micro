package kh.edu.istad.stadoor.gateway.gateway.adapter;

import kh.edu.istad.stadoor.gateway.gateway.dto.query.UserProfileResponse;
import kh.edu.istad.stadoor.gateway.gateway.ports.output.GatewayRepositoryOutputPort;
import kh.edu.istad.stadoor.gateway.gateway.repository.GatewayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GatewayRepositoryOutputPortAdapter implements GatewayRepositoryOutputPort {


    private final GatewayRepository gatewayRepository;

    @Override
    public Mono<Boolean> existByGatewayId(UUID gatewayId) {
        return gatewayRepository.existsById(gatewayId);
    }

    @Override
    public Mono<String> findGatewayNameById(UUID gatewayId) {
        return gatewayRepository.findGatewayNameById(gatewayId)
                .switchIfEmpty(Mono.just("Unknown"));
    }
}
