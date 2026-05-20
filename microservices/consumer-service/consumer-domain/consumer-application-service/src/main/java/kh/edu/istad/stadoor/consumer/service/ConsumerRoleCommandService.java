package kh.edu.istad.stadoor.consumer.service;

import kh.edu.istad.stadoor.consumer.dto.request.AssignRoleInput;
import kh.edu.istad.stadoor.consumer.dto.request.CreateRoleInput;
import kh.edu.istad.stadoor.consumer.dto.request.UpdateRoleInput;
import kh.edu.istad.stadoor.consumer.dto.response.ConsumerRoleResponse;
import kh.edu.istad.stadoor.consumer.port.input.ConsumerRoleCommandPort;
import kh.edu.istad.stadoor.consumer.port.output.ConsumerRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsumerRoleCommandService implements ConsumerRoleCommandPort {

    private final ConsumerRoleRepository roleRepository;

    @Override
    public Mono<ConsumerRoleResponse> createRole(CreateRoleInput input) {
        return roleRepository.createRole(input);
    }

    @Override
    public Mono<ConsumerRoleResponse> updateRole(UUID id, UpdateRoleInput input) {
        return roleRepository.updateRole(id, input);
    }

    @Override
    public Mono<Void> deleteRole(UUID id) {
        return roleRepository.deleteRole(id);
    }

    @Override
    public Mono<Void> assignRole(AssignRoleInput input) {
        return roleRepository.assignRole(input);
    }

    @Override
    public Mono<Void> unassignRole(UUID consumerId, UUID roleId) {
        return roleRepository.unassignRole(consumerId, roleId);
    }
}
