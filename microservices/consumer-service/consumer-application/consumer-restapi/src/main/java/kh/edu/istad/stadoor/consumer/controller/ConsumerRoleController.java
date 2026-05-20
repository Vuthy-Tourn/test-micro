package kh.edu.istad.stadoor.consumer.controller;

import jakarta.validation.Valid;
import kh.edu.istad.stadoor.consumer.dto.request.AssignRoleRequest;
import kh.edu.istad.stadoor.consumer.dto.request.CreateRoleRequest;
import kh.edu.istad.stadoor.consumer.dto.request.UpdateRoleRequest;
import kh.edu.istad.stadoor.consumer.dto.response.ApiResponse;
import kh.edu.istad.stadoor.consumer.dto.response.ConsumerRoleResponse;
import kh.edu.istad.stadoor.consumer.mapper.ConsumerRequestMapper;
import kh.edu.istad.stadoor.consumer.port.input.ConsumerRoleCommandPort;
import kh.edu.istad.stadoor.consumer.port.input.ConsumerRoleQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class ConsumerRoleController {

    private final ConsumerRoleCommandPort commandPort;
    private final ConsumerRoleQueryPort queryPort;
    private final ConsumerRequestMapper mapper;

    @PostMapping
    public Mono<ApiResponse<ConsumerRoleResponse>> createRole(@Valid @RequestBody CreateRoleRequest request) {
        return commandPort.createRole(mapper.toInput(request))
                .map(r -> ApiResponse.ok("Role created", r));
    }

    @PatchMapping("/{id}")
    public Mono<ApiResponse<ConsumerRoleResponse>> updateRole(@PathVariable UUID id,
                                                              @Valid @RequestBody UpdateRoleRequest request) {
        return commandPort.updateRole(id, mapper.toInput(request))
                .map(r -> ApiResponse.ok("Role updated", r));
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<Void>> deleteRole(@PathVariable UUID id) {
        return commandPort.deleteRole(id)
                .thenReturn(ApiResponse.ok("Role deleted", null));
    }

    @PostMapping("/assign")
    public Mono<ApiResponse<Void>> assignRole(@Valid @RequestBody AssignRoleRequest request) {
        return commandPort.assignRole(mapper.toInput(request))
                .thenReturn(ApiResponse.ok("Role assigned", null));
    }

    @DeleteMapping("/consumers/{consumerId}/roles/{roleId}")
    public Mono<ApiResponse<Void>> unassignRole(@PathVariable UUID consumerId, @PathVariable UUID roleId) {
        return commandPort.unassignRole(consumerId, roleId)
                .thenReturn(ApiResponse.ok("Role unassigned", null));
    }

    @GetMapping("/tenant/{tenantId}")
    public Flux<ConsumerRoleResponse> findRolesByTenantId(@PathVariable UUID tenantId) {
        return queryPort.findRolesByTenantId(tenantId);
    }

    @GetMapping("/consumer/{consumerId}")
    public Flux<ConsumerRoleResponse> findRolesByConsumerId(@PathVariable UUID consumerId) {
        return queryPort.findRolesByConsumerId(consumerId);
    }
}
