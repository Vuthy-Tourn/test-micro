package kh.edu.istad.stadoor.consumer.port.input;

import kh.edu.istad.stadoor.consumer.dto.response.ConsumerRoleResponse;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ConsumerRoleQueryPort {

    Flux<ConsumerRoleResponse> findRolesByTenantId(UUID tenantId);

    Flux<ConsumerRoleResponse> findRolesByConsumerId(UUID consumerId);
}
