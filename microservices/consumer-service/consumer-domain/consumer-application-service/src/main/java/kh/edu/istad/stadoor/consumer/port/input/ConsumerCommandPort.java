package kh.edu.istad.stadoor.consumer.port.input;

import kh.edu.istad.stadoor.consumer.dto.command.RegisterConsumerInput;
import kh.edu.istad.stadoor.consumer.dto.response.ConsumerResponse;
import kh.edu.istad.stadoor.consumer.dto.response.RegisterConsumerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ConsumerCommandPort {

    Mono<RegisterConsumerResponse> registerConsumer(RegisterConsumerInput input);

    Mono<ConsumerResponse> activate(UUID consumerId);

    Mono<ConsumerResponse> deactivate(UUID consumerId);
}
