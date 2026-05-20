package kh.edu.istad.stadoor.consumer.port.input;

import kh.edu.istad.stadoor.consumer.dto.request.AssignRoleInput;
import kh.edu.istad.stadoor.consumer.dto.request.CreateRoleInput;
import kh.edu.istad.stadoor.consumer.dto.request.UpdateRoleInput;
import kh.edu.istad.stadoor.consumer.dto.response.ConsumerRoleResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ConsumerRoleCommandPort {

    Mono<ConsumerRoleResponse> createRole(CreateRoleInput input);

    Mono<ConsumerRoleResponse> updateRole(UUID id, UpdateRoleInput input);

    Mono<Void> deleteRole(UUID id);

    Mono<Void> assignRole(AssignRoleInput input);

    Mono<Void> unassignRole(UUID consumerId, UUID roleId);
}
