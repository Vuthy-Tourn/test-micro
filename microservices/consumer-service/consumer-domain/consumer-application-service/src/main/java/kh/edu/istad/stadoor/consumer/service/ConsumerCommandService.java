package kh.edu.istad.stadoor.consumer.service;

import kh.edu.istad.stadoor.consumer.command.ActivateConsumerCommand;
import kh.edu.istad.stadoor.consumer.command.DeactivateConsumerCommand;
import kh.edu.istad.stadoor.consumer.command.RegisterConsumerCommand;
import kh.edu.istad.stadoor.consumer.dto.command.RegisterConsumerInput;
import kh.edu.istad.stadoor.consumer.dto.response.ConsumerCredentialResponse;
import kh.edu.istad.stadoor.consumer.dto.response.ConsumerResponse;
import kh.edu.istad.stadoor.consumer.dto.response.RegisterConsumerResponse;
import kh.edu.istad.stadoor.consumer.exception.DomainConflictException;
import kh.edu.istad.stadoor.consumer.exception.DomainForbiddenException;
import kh.edu.istad.stadoor.consumer.exception.DomainNotFoundException;
import kh.edu.istad.stadoor.consumer.port.input.ConsumerCommandPort;
import kh.edu.istad.stadoor.consumer.port.output.ConsumerQueryRepository;
import kh.edu.istad.stadoor.consumer.port.output.SyncRepository;
import kh.edu.istad.stadoor.consumer.valueobject.auth.AuthType;
import kh.edu.istad.stadoor.consumer.valueobject.consumer.ConsumerStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerCommandService implements ConsumerCommandPort {

    private final CommandGateway commandGateway;
    private final ConsumerQueryRepository queryRepository;
    private final SyncRepository syncRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Mono<RegisterConsumerResponse> registerConsumer(RegisterConsumerInput input) {

        if (input.authType() == AuthType.BASIC_AUTH || input.authType() == AuthType.JWT) {
            if (input.username() == null || input.username().isBlank()) {
                return Mono.error(new IllegalArgumentException("username is required for " + input.authType() + " auth"));
            }
            if (input.password() == null || input.password().isBlank()) {
                return Mono.error(new IllegalArgumentException("password is required for " + input.authType() + " auth"));
            }
        }

        UUID consumerId = UUID.randomUUID();
        UUID credentialId = UUID.randomUUID();
        Instant now = Instant.now();

        RegisterConsumerCommand command = buildCommand(consumerId, credentialId, now, input);

        Mono<Void> gatewayCheck = syncRepository.findGatewayAuthType(input.gatewayId(), input.tenantId())
                .switchIfEmpty(Mono.error(new DomainNotFoundException("Gateway not found or does not belong to this tenant")))
                .flatMap(gatewayAuthType -> {
                    if (!gatewayAuthType.equalsIgnoreCase(input.authType().name())) {
                        return Mono.error(new DomainForbiddenException(
                                "Gateway auth type is " + gatewayAuthType + " but consumer auth type is " + input.authType()));
                    }
                    return Mono.empty();
                });

        Mono<Void> duplicateCheck = switch (input.authType()) {
            case BASIC_AUTH -> queryRepository.findBasicAuthByUsername(input.username(), input.gatewayId())
                    .flatMap(existing -> Mono.<Void>error(new DomainConflictException(
                            "Username '" + input.username() + "' already exists for this gateway")))
                    .then();
            case JWT -> queryRepository.findJwtByUsername(input.username(), input.gatewayId())
                    .flatMap(existing -> Mono.<Void>error(new DomainConflictException(
                            "Username '" + input.username() + "' already exists for this gateway")))
                    .then();
            default -> Mono.empty();
        };

        return gatewayCheck.then(duplicateCheck).then(Mono.defer(() -> Mono.fromFuture(commandGateway.send(command))))
                .thenReturn(buildResponse(consumerId, command, now))
                .doOnSuccess(r -> log.info("Consumer registered: consumerId={}, authType={}", consumerId, input.authType()));
    }

    @Override
    public Mono<ConsumerResponse> activate(UUID consumerId) {
        return queryRepository.findById(consumerId)
                .switchIfEmpty(Mono.error(new DomainNotFoundException("Consumer not found: " + consumerId)))
                .flatMap(consumer -> Mono.fromFuture(commandGateway.send(new ActivateConsumerCommand(consumerId)))
                        .thenReturn(new ConsumerResponse(
                                consumer.consumerId(),
                                consumer.tenantId(),
                                consumer.gatewayId(),
                                consumer.name(),
                                consumer.description(),
                                consumer.consumerType(),
                                consumer.authType(),
                                ConsumerStatus.ACTIVE.name(),
                                consumer.createdAt()
                        )))
                .doOnSuccess(r -> log.info("Consumer activated: consumerId={}", consumerId));

    }

    @Override
    public Mono<ConsumerResponse> deactivate(UUID consumerId) {
        return queryRepository.findById(consumerId)
                .switchIfEmpty(Mono.error(new DomainNotFoundException("Consumer not found: " + consumerId)))
                .flatMap(consumer -> Mono.fromFuture(commandGateway.send(new DeactivateConsumerCommand(consumerId)))
                        .thenReturn(new ConsumerResponse(
                                consumer.consumerId(),
                                consumer.tenantId(),
                                consumer.gatewayId(),
                                consumer.name(),
                                consumer.description(),
                                consumer.consumerType(),
                                consumer.authType(),
                                ConsumerStatus.INACTIVE.name(),
                                consumer.createdAt()
                        )))
                .doOnSuccess(r -> log.info("Consumer deactivated: consumerId={}", consumerId));
    }

    private RegisterConsumerCommand buildCommand(UUID consumerId, UUID credentialId,
            Instant now, RegisterConsumerInput input) {

        return switch (input.authType()) {
            case BASIC_AUTH -> RegisterConsumerCommand.builder()
                    .consumerId(consumerId).tenantId(input.tenantId()).gatewayId(input.gatewayId())
                    .name(input.name()).description(input.description())
                    .consumerType(input.consumerType()).authType(input.authType()).createdAt(now)
                    .credentialId(credentialId).credentialType("BASIC_AUTH")
                    .username(input.username()).credentialValue(input.username())
                    .credentialHash(passwordEncoder.encode(input.password()))
                    .build();

            case API_KEY -> {
                String rawKey = "sk_" + UUID.randomUUID().toString().replace("-", "");
                yield RegisterConsumerCommand.builder()
                        .consumerId(consumerId).tenantId(input.tenantId()).gatewayId(input.gatewayId())
                        .name(input.name()).description(input.description())
                        .consumerType(input.consumerType()).authType(input.authType()).createdAt(now)
                        .credentialId(credentialId).credentialType("API_KEY")
                        .credentialValue(rawKey).credentialHash(hashApiKey(rawKey))
                        .build();
            }

            case JWT -> {
                yield RegisterConsumerCommand.builder()
                        .consumerId(consumerId).tenantId(input.tenantId()).gatewayId(input.gatewayId())
                        .name(input.name()).description(input.description())
                        .consumerType(input.consumerType()).authType(input.authType()).createdAt(now)
                        .credentialId(credentialId).credentialType("JWT")
                        .username(input.username())
                        .credentialHash(passwordEncoder.encode(input.password()))
                        .build();
            }
        };
    }

    private RegisterConsumerResponse buildResponse(UUID consumerId, RegisterConsumerCommand command, Instant now) {
        return RegisterConsumerResponse.builder()
                .consumerId(consumerId)
                .gatewayId(command.gatewayId())
                .tenantId(command.tenantId())
                .name(command.name())
                .status(true)
                .createdAt(now)
                .credential(ConsumerCredentialResponse.builder()
                        .credentialId(command.credentialId())
                        .credentialType(command.credentialType())
                        .value(command.credentialValue())
                        .algorithm(command.algorithm())
                        .createdAt(now)
                        .build())
                .build();
    }

    private String hashApiKey(String apiKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(apiKey.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
