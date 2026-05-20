package kh.edu.istad.stadoor.consumer.listener;

import kh.edu.istad.stadoor.consumer.entity.*;
import kh.edu.istad.stadoor.consumer.event.ConsumerActivatedEvent;
import kh.edu.istad.stadoor.consumer.event.ConsumerDeactivatedEvent;
import kh.edu.istad.stadoor.consumer.event.ConsumerRegisteredEvent;
import kh.edu.istad.stadoor.consumer.port.input.message.listener.ConsumerMessageListener;
import kh.edu.istad.stadoor.consumer.repository.ConsumerReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConsumerMessageListenerImpl implements ConsumerMessageListener {

    private final R2dbcEntityTemplate entityTemplate;
    private final ConsumerReactiveRepository consumerRepository;

    @Override
    public Mono<Void> onConsumerRegisteredEvent(ConsumerRegisteredEvent event) {
        log.info("onConsumerRegisteredEvent: consumerId={}, authType={}", event.consumerId(), event.authType());

        ConsumerEntity consumer = new ConsumerEntity();
        consumer.setId(event.consumerId());
        consumer.setTenantId(event.tenantId());
        consumer.setGatewayId(event.gatewayId());
        consumer.setName(event.name());
        consumer.setDescription(event.description());
        consumer.setType(event.consumerType().name());
        consumer.setAuthType(event.authType().name());
        consumer.setStatus(event.status().name());
        consumer.setCreatedAt(event.createdAt());
        consumer.setUpdatedAt(event.createdAt());

        ConsumerCredentialEntity parent = new ConsumerCredentialEntity();
        parent.setId(event.credentialId());
        parent.setConsumerId(event.consumerId());
        parent.setTenantId(event.tenantId());
        parent.setCredentialType(event.credentialType());
        parent.setStatus(true);
        parent.setCreatedAt(event.createdAt());
        parent.setUpdatedAt(event.createdAt());

        Instant now = event.createdAt();
        Mono<Void> credentialInsert = switch (event.credentialType()) {
            case "BASIC_AUTH" -> {
                BasicAuthCredentialEntity basic = new BasicAuthCredentialEntity();
                basic.setId(UUID.randomUUID());
                basic.setConsumerCredentialId(parent.getId());
                basic.setUsername(event.credentialValue());
                basic.setPasswordHash(event.credentialHash());
                basic.setCreatedAt(now);
                yield entityTemplate.insert(BasicAuthCredentialEntity.class).using(basic).then();
            }
            case "API_KEY" -> {
                ApiKeyCredentialEntity apiKey = new ApiKeyCredentialEntity();
                apiKey.setId(UUID.randomUUID());
                apiKey.setConsumerCredentialId(parent.getId());
                apiKey.setApiKey(event.credentialValue());
                apiKey.setKeyHash(event.credentialHash());
                apiKey.setCreatedAt(now);
                yield entityTemplate.insert(ApiKeyCredentialEntity.class).using(apiKey).then();
            }
            case "JWT" -> {
                JwtCredentialEntity jwt = new JwtCredentialEntity();
                jwt.setId(UUID.randomUUID());
                jwt.setConsumerCredentialId(parent.getId());
                jwt.setGatewayId(event.gatewayId());
                jwt.setUsername(event.username());
                jwt.setPasswordHash(event.credentialHash());
                jwt.setCreatedAt(now);
                jwt.setUpdatedAt(now);
                yield entityTemplate.insert(JwtCredentialEntity.class).using(jwt).then();
            }
            default -> Mono.error(new IllegalStateException("Unknown credential type: " + event.credentialType()));
        };

        return entityTemplate.insert(ConsumerEntity.class).using(consumer)
                .then(entityTemplate.insert(ConsumerCredentialEntity.class).using(parent))
                .then(credentialInsert)
                .doOnSuccess(v -> log.info("Persisted consumer and credentials: consumerId={}", event.consumerId()));
    }

    @Override
    public Mono<Void> onConsumerActivatedEvent(ConsumerActivatedEvent event) {
        log.info("onConsumerActivatedEvent: consumerId={}", event.consumerId());

        return consumerRepository.findById(event.consumerId())
                .flatMap(consumer -> {
                    consumer.setStatus(event.status().name());
                    consumer.setUpdatedAt(event.updatedAt());
                    return consumerRepository.save(consumer);
                })
                .doOnSuccess(v -> log.info("Consumer activated: consumerId={}", event.consumerId()))
                .then();
    }

    @Override
    public Mono<Void> onConsumerDeactivatedEvent(ConsumerDeactivatedEvent event) {
        log.info("onConsumerDeactivatedEvent: consumerId={}", event.consumerId());

        return consumerRepository.findById(event.consumerId())
                .flatMap(consumer -> {
                    consumer.setStatus(event.status().name());
                    consumer.setUpdatedAt(event.updatedAt());
                    return consumerRepository.save(consumer);
                })
                .doOnSuccess(v -> log.info("Consumer deactivated: consumerId={}", event.consumerId()))
                .then();
    }
}
