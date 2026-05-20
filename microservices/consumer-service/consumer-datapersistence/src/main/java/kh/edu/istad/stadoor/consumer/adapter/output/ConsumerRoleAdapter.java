package kh.edu.istad.stadoor.consumer.adapter.output;

import kh.edu.istad.stadoor.consumer.dto.request.AssignRoleInput;
import kh.edu.istad.stadoor.consumer.dto.request.CreateRoleInput;
import kh.edu.istad.stadoor.consumer.dto.request.UpdateRoleInput;
import kh.edu.istad.stadoor.consumer.dto.response.ConsumerRoleResponse;
import kh.edu.istad.stadoor.consumer.exception.DomainForbiddenException;
import kh.edu.istad.stadoor.consumer.exception.DomainNotFoundException;
import kh.edu.istad.stadoor.consumer.port.output.ConsumerRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConsumerRoleAdapter implements ConsumerRoleRepository {

    private final DatabaseClient databaseClient;

    @Override
    public Mono<ConsumerRoleResponse> createRole(CreateRoleInput input) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        return databaseClient.sql("""
                        INSERT INTO consumer_roles (id, tenant_id, name, description, status, created_at)
                        VALUES (:id, :tenantId, :name, :description, true, :createdAt)
                        """)
                .bind("id", id)
                .bind("tenantId", input.tenantId())
                .bind("name", input.name())
                .bind("description", input.description() != null ? input.description() : "")
                .bind("createdAt", now)
                .then()
                .thenReturn(new ConsumerRoleResponse(id, input.tenantId(), input.name(),
                        input.description(), true, now));
    }

    @Override
    public Mono<ConsumerRoleResponse> updateRole(UUID id, UpdateRoleInput input) {
        return databaseClient.sql("""
                        UPDATE consumer_roles
                        SET name = :name, description = :description
                        WHERE id = :id
                        """)
                .bind("id", id)
                .bind("name", input.name())
                .bind("description", input.description() != null ? input.description() : "")
                .fetch()
                .rowsUpdated()
                .flatMap(rows -> {
                    if (rows == 0) {
                        return Mono.error(new DomainNotFoundException("Role not found: " + id));
                    }
                    return databaseClient.sql("""
                                    SELECT id, tenant_id, name, description, status, created_at
                                    FROM consumer_roles WHERE id = :id
                                    """)
                            .bind("id", id)
                            .map((row, meta) -> new ConsumerRoleResponse(
                                    row.get("id", UUID.class),
                                    row.get("tenant_id", UUID.class),
                                    row.get("name", String.class),
                                    row.get("description", String.class),
                                    row.get("status", Boolean.class),
                                    row.get("created_at", Instant.class)
                            ))
                            .one();
                });
    }

    @Override
    public Mono<Void> deleteRole(UUID id) {
        return databaseClient.sql("DELETE FROM consumer_roles WHERE id = :id")
                .bind("id", id)
                .fetch()
                .rowsUpdated()
                .flatMap(rows -> {
                    if (rows == 0) {
                        return Mono.error(new DomainNotFoundException("Role not found: " + id));
                    }
                    return Mono.empty();
                })
                .then();
    }

    @Override
    public Flux<ConsumerRoleResponse> findRolesByTenantId(UUID tenantId) {
        return databaseClient.sql("""
                        SELECT id, tenant_id, name, description, status, created_at
                        FROM consumer_roles
                        WHERE tenant_id = :tenantId AND status = true
                        """)
                .bind("tenantId", tenantId)
                .map((row, meta) -> new ConsumerRoleResponse(
                        row.get("id", UUID.class),
                        row.get("tenant_id", UUID.class),
                        row.get("name", String.class),
                        row.get("description", String.class),
                        row.get("status", Boolean.class),
                        row.get("created_at", Instant.class)
                ))
                .all();
    }

    @Override
    public Flux<ConsumerRoleResponse> findRolesByConsumerId(UUID consumerId) {
        return databaseClient.sql("""
                        SELECT cr.id, cr.tenant_id, cr.name, cr.description, cr.status, cr.created_at
                        FROM consumer_roles cr
                        JOIN consumer_role_mappings crm ON crm.role_id = cr.id
                        WHERE crm.consumer_id = :consumerId AND cr.status = true
                        """)
                .bind("consumerId", consumerId)
                .map((row, meta) -> new ConsumerRoleResponse(
                        row.get("id", UUID.class),
                        row.get("tenant_id", UUID.class),
                        row.get("name", String.class),
                        row.get("description", String.class),
                        row.get("status", Boolean.class),
                        row.get("created_at", Instant.class)
                ))
                .all();
    }

    @Override
    public Mono<Void> assignRole(AssignRoleInput input) {
        return databaseClient.sql("""
                        SELECT cr.tenant_id AS role_tenant_id, c.tenant_id AS consumer_tenant_id
                        FROM consumer_roles cr, consumers c
                        WHERE cr.id = :roleId AND c.id = :consumerId
                        """)
                .bind("roleId", input.roleId())
                .bind("consumerId", input.consumerId())
                .map((row, meta) -> {
                    java.util.UUID roleTenantId = row.get("role_tenant_id", UUID.class);
                    java.util.UUID consumerTenantId = row.get("consumer_tenant_id", UUID.class);
                    return java.util.Objects.equals(roleTenantId, consumerTenantId);
                })
                .one()
                .switchIfEmpty(Mono.error(new DomainNotFoundException("Role or consumer not found")))
                .flatMap(tenantsMatch -> {
                    if (!tenantsMatch) {
                        return Mono.error(new DomainForbiddenException("Role and consumer belong to different tenants"));
                    }
                    return databaseClient.sql("""
                                    INSERT INTO consumer_role_mappings (id, consumer_id, role_id, assigned_at, assigned_by_user_id)
                                    VALUES (:id, :consumerId, :roleId, :assignedAt, :assignedByUserId)
                                    """)
                            .bind("id", UUID.randomUUID())
                            .bind("consumerId", input.consumerId())
                            .bind("roleId", input.roleId())
                            .bind("assignedAt", Instant.now())
                            .bind("assignedByUserId", input.assignedByUserId())
                            .then();
                });
    }

    @Override
    public Mono<Void> unassignRole(UUID consumerId, UUID roleId) {
        return databaseClient.sql("""
                        DELETE FROM consumer_role_mappings
                        WHERE consumer_id = :consumerId AND role_id = :roleId
                        """)
                .bind("consumerId", consumerId)
                .bind("roleId", roleId)
                .fetch()
                .rowsUpdated()
                .flatMap(rows -> {
                    if (rows == 0) {
                        return Mono.error(new DomainNotFoundException("Role assignment not found"));
                    }
                    return Mono.empty();
                })
                .then();
    }
}
