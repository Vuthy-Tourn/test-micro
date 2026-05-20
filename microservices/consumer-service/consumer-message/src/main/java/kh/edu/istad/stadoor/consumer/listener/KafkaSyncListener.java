package kh.edu.istad.stadoor.consumer.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.edu.istad.stadoor.consumer.port.output.SyncRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaSyncListener {

    private final SyncRepository syncRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "IAM.Tenant.Event", groupId = "consumer-service-sync-group")
    public void onTenantEvent(ConsumerRecord<String, byte[]> record) {
        try {
            String messageType = getHeader(record, "axon-message-type");
            JsonNode node = objectMapper.readTree(record.value());

            if (messageType != null && messageType.endsWith("TenantCreatedEvent")) {
                UUID tenantId = UUID.fromString(node.at("/tenantId/id").asText());
                String name = node.get("name").asText();
                syncRepository.upsertTenant(tenantId, name)
                        .doOnSuccess(v -> log.info("Synced tenant: {}", tenantId))
                        .doOnError(e -> log.error("Failed to sync tenant: {}", tenantId, e))
                        .block();
            }
        } catch (Exception e) {
            log.error("Failed to process tenant event from offset {}", record.offset(), e);
        }
    }

    @KafkaListener(topics = "Stadoor.Gateway.Event", groupId = "consumer-service-sync-group")
    public void onGatewayEvent(ConsumerRecord<String, byte[]> record) {
        try {
            String messageType = getHeader(record, "axon-message-type");
            if (messageType == null) return;

            JsonNode node = objectMapper.readTree(record.value());
            UUID gatewayId = UUID.fromString(node.at("/gatewayId/gatewayId").asText());

            if (messageType.endsWith("GatewayCreatedEvent")) {
                UUID tenantId = UUID.fromString(node.at("/tenantId/id").asText());
                String name = node.at("/name/name").asText();
                String status = node.get("status").asText();
                String authType = node.get("authType").asText();
                syncRepository.upsertGateway(gatewayId, tenantId, name, status, authType)
                        .doOnSuccess(v -> log.info("Synced gateway created: {}", gatewayId))
                        .doOnError(e -> log.error("Failed to sync gateway created: {}", gatewayId, e))
                        .block();

            } else if (messageType.endsWith("GatewayUpdatedEvent")) {
                String name = node.at("/name/name").asText();
                syncRepository.updateGatewayName(gatewayId, name)
                        .doOnSuccess(v -> log.info("Synced gateway updated: {}", gatewayId))
                        .doOnError(e -> log.error("Failed to sync gateway updated: {}", gatewayId, e))
                        .block();

            } else if (messageType.endsWith("GatewayActivatedEvent") || messageType.endsWith("GatewayDeactivatedEvent")) {
                String status = node.get("status").asText();
                syncRepository.updateGatewayStatus(gatewayId, status)
                        .doOnSuccess(v -> log.info("Synced gateway status {}: {}", status, gatewayId))
                        .doOnError(e -> log.error("Failed to sync gateway status: {}", gatewayId, e))
                        .block();
            }
        } catch (Exception e) {
            log.error("Failed to process gateway event from offset {}", record.offset(), e);
        }
    }

    private String getHeader(ConsumerRecord<String, byte[]> record, String key) {
        var header = record.headers().lastHeader(key);
        return header != null ? new String(header.value()) : null;
    }
}
