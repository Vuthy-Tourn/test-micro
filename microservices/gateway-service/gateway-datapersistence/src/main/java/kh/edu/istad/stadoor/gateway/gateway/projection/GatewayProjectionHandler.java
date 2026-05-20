package kh.edu.istad.stadoor.gateway.gateway.projection;

import kh.edu.istad.stadoor.common.valueobject.TenantId;
import kh.edu.istad.stadoor.gateway.event.gateway.GatewayActivatedEvent;
import kh.edu.istad.stadoor.gateway.event.gateway.GatewayCreatedEvent;
import kh.edu.istad.stadoor.common.event.gateway.GatewayDeactivatedEvent;
import kh.edu.istad.stadoor.gateway.event.gateway.GatewayUpdatedEvent;
import kh.edu.istad.stadoor.gateway.gateway.entity.GatewayEntity;
import kh.edu.istad.stadoor.gateway.gateway.repository.GatewayRepository;
import kh.edu.istad.stadoor.common.valueobject.gateway.GatewayId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GatewayProjectionHandler {

    private final GatewayRepository gatewayRepository;

    @EventHandler
    public void on(GatewayCreatedEvent event) {
        GatewayEntity gatewayEntity = new GatewayEntity();
        gatewayEntity.setGatewayId(gatewayUuid(event.gatewayId()));
        gatewayEntity.setTenantId(tenantUuid(event.tenantId()));
        gatewayEntity.setGatewayName(event.name().name());
        gatewayEntity.setDescription(event.description().description());
        gatewayEntity.setGatewayType(event.gatewayType().name());
        gatewayEntity.setAuthType(event.authType().name());
        gatewayEntity.setStatus(event.status().name());
        gatewayEntity.setCreatedAt(event.createdAt().toInstant());
        gatewayEntity.setUpdatedAt(event.updatedAt().toInstant());
        gatewayEntity.markNew();

        try {
            GatewayEntity saved = gatewayRepository.save(gatewayEntity).block();
            GatewayEntity found = gatewayRepository.findById(gatewayEntity.getGatewayId()).block();
            Long count = gatewayRepository.count().block();

            log.info("Saved gateway projection: {}", saved);
            log.info("Found gateway projection by id: {}", found);
            log.info("Gateway projection table count: {}", count);
        } catch (Exception e) {
            log.error("Failed to save gateway projection", e);
            throw e;
        }
    }
    @EventHandler
    public void on(GatewayUpdatedEvent event) {
        gatewayRepository.findById(gatewayUuid(event.gatewayId()))
                .map(entity -> {
                    entity.setGatewayName(event.name().name());
                    entity.setDescription(event.description().description());
                    entity.setUpdatedAt(event.updatedAt().toInstant());
                    entity.markNotNew();
                    return entity;
                })
                .flatMap(gatewayRepository::save)
                .block();
        log.info("Projected gateway updated event for gateway {}", gatewayUuid(event.gatewayId()));
    }

    @EventHandler
    public void on(GatewayActivatedEvent event) {
        updateGatewayStatus(gatewayUuid(event.gatewayId()), event.status().name(), event.updatedAt().toInstant());
        log.info("Projected gateway activated event for gateway {}", gatewayUuid(event.gatewayId()));
    }

    @EventHandler
    public void on(GatewayDeactivatedEvent event) {
        updateGatewayStatus(gatewayUuid(event.gatewayId()), event.status().name(), event.updatedAt().toInstant());
        log.info("Projected gateway deactivated event for gateway {}", gatewayUuid(event.gatewayId()));
    }

    private void updateGatewayStatus(UUID gatewayId, String status, Instant updatedAt) {
        gatewayRepository.findById(gatewayId)
                .map(entity -> {
                    entity.setStatus(status);
                    entity.setUpdatedAt(updatedAt);
                    return entity;
                })
                .flatMap(gatewayRepository::save)
                .block();
    }

    private UUID gatewayUuid(GatewayId gatewayId) {
        return gatewayId.gatewayId();
    }

    private UUID tenantUuid(TenantId tenantId) {
        return tenantId.id();
    }
}
