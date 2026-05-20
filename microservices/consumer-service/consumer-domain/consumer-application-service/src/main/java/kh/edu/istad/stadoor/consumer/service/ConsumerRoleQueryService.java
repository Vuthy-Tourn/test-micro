package kh.edu.istad.stadoor.consumer.service;

import kh.edu.istad.stadoor.consumer.dto.response.ConsumerRoleResponse;
import kh.edu.istad.stadoor.consumer.port.input.ConsumerRoleQueryPort;
import kh.edu.istad.stadoor.consumer.port.output.ConsumerRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsumerRoleQueryService implements ConsumerRoleQueryPort {

    private final ConsumerRoleRepository roleRepository;

    @Override
    public Flux<ConsumerRoleResponse> findRolesByTenantId(UUID tenantId) {
        return roleRepository.findRolesByTenantId(tenantId);
    }

    @Override
    public Flux<ConsumerRoleResponse> findRolesByConsumerId(UUID consumerId) {
        return roleRepository.findRolesByConsumerId(consumerId);
    }
}
