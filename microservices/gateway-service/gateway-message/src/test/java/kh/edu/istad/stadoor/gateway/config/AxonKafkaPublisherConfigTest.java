package kh.edu.istad.stadoor.gateway.config;

import kh.edu.istad.stadoor.gateway.event.gateway.GatewayCreatedEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AxonKafkaPublisherConfigTest {

    private final AxonKafkaPublisherConfig config = new AxonKafkaPublisherConfig();

    @Test
    void publishesGatewayCatalogEventsToTheConfiguredPublicTopic() {
        // Gateway catalog events should be visible to gateway-server.
        assertThat(config.resolveTopicName(GatewayCreatedEvent.class, "gateway.events.v1"))
                .contains("gateway.events.v1");
    }

    @Test
    void skipsEventsOutsideTheGatewayCatalogPackage() {
        // Non-catalog payloads should not leak into the public topic by accident.
        assertThat(config.resolveTopicName(String.class, "gateway.events.v1"))
                .isEmpty();
    }
}
