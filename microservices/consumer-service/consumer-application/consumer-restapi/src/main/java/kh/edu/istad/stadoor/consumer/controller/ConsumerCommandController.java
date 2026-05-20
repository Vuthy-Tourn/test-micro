package kh.edu.istad.stadoor.consumer.controller;

import jakarta.validation.Valid;
import kh.edu.istad.stadoor.consumer.dto.request.RegisterConsumerRequest;
import kh.edu.istad.stadoor.consumer.dto.response.ApiResponse;
import kh.edu.istad.stadoor.consumer.dto.response.ConsumerResponse;
import kh.edu.istad.stadoor.consumer.dto.response.RegisterConsumerResponse;
import kh.edu.istad.stadoor.consumer.mapper.ConsumerRequestMapper;
import kh.edu.istad.stadoor.consumer.port.input.ConsumerCommandPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consumers")
@RequiredArgsConstructor
@Slf4j
public class ConsumerCommandController {

    private final ConsumerCommandPort consumerCommandPort;
    private final ConsumerRequestMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<RegisterConsumerResponse>> registerConsumer(
            @Valid @RequestBody RegisterConsumerRequest request) {

        log.info("Register consumer request: name={}, authType={}", request.name(), request.authType());

        return consumerCommandPort.registerConsumer(mapper.toInput(request))
                .map(r -> ApiResponse.ok("Consumer registered successfully", r));
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{consumerId}/activate")
    public Mono<ApiResponse<ConsumerResponse>> activateConsumer(@PathVariable UUID consumerId) {
        log.info("activateConsumer: {}", consumerId);
        return consumerCommandPort.activate(consumerId)
                .map(r -> ApiResponse.ok("Consumer activated successfully", r));
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{consumerId}/deactivate")
    public Mono<ApiResponse<ConsumerResponse>> deactivateConsumer(@PathVariable UUID consumerId) {
        log.info("deactivateConsumer: {}", consumerId);
        return consumerCommandPort.deactivate(consumerId)
                .map(r -> ApiResponse.ok("Consumer deactivated successfully", r));
    }
}
