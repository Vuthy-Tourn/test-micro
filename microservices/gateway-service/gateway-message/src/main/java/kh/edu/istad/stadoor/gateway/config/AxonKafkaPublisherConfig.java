package kh.edu.istad.stadoor.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ConfigurerModule;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.extensions.kafka.eventhandling.DefaultKafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.producer.KafkaEventPublisher;
import org.axonframework.extensions.kafka.eventhandling.producer.KafkaPublisher;
import org.axonframework.extensions.kafka.eventhandling.producer.ProducerFactory;
import org.axonframework.serialization.Serializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * Configuration for Axon Kafka Event Publisher.
 * This concept handles dynamic topic resolution based on event type.
 */
@Slf4j
@Configuration
public class AxonKafkaPublisherConfig {

    private static final String TOPIC_PREFIX = "Stadoor";

    @Bean
    public KafkaMessageConverter<String, byte[]> kafkaMessageConverter(
            @Qualifier("eventSerializer") Serializer eventSerializer) {

        return DefaultKafkaMessageConverter.builder()
                .serializer(eventSerializer)
                .build();
    }

    @Bean
    public KafkaPublisher<String, byte[]> kafkaPublisher(
            ProducerFactory<String, byte[]> producerFactory,
            KafkaMessageConverter<String, byte[]> messageConverter,
            @Qualifier("eventSerializer") Serializer serializer) {

        return KafkaPublisher.<String, byte[]>builder()
                .producerFactory(producerFactory)
                .messageConverter(messageConverter)
                .serializer(serializer)
                .topicResolver(eventMessage -> {
                    String aggregateName = "System"; // Default for generic events

                    if (eventMessage instanceof DomainEventMessage) {
                        aggregateName = ((DomainEventMessage<?>) eventMessage)
                                .getType()
                                .replace("Aggregate", "");
                    } else {
                        // Fallback: guess from payload name if not a domain event
                        String payloadName = eventMessage.getPayloadType().getSimpleName();
                        aggregateName = payloadName.replace("Event", "");
                    }

                    return Optional.of(TOPIC_PREFIX + "." + aggregateName + ".Event");
                })
                .build();
    }

    @Bean
    public KafkaEventPublisher<String, byte[]> kafkaEventPublisher(KafkaPublisher<String, byte[]> kafkaPublisher) {
        return KafkaEventPublisher.<String, byte[]>builder()
                .kafkaPublisher(kafkaPublisher)
                .build();
    }

    @Bean
    public ConfigurerModule kafkaPublisherConfigurer(KafkaEventPublisher<String, byte[]> kafkaEventPublisher) {
        return configurer -> {
            String processingGroup = KafkaEventPublisher.DEFAULT_PROCESSING_GROUP;

            configurer.eventProcessing()
                    .registerEventHandler(conf -> kafkaEventPublisher)
                    .assignHandlerTypesMatching(processingGroup,
                            clazz -> clazz.isAssignableFrom(KafkaEventPublisher.class))
                    .registerTrackingEventProcessor(processingGroup);
        };
    }
}
