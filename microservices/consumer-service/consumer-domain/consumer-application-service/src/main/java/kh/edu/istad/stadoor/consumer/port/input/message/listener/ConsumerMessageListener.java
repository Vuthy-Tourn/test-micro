package kh.edu.istad.stadoor.consumer.port.input.message.listener;

import kh.edu.istad.stadoor.consumer.event.ConsumerActivatedEvent;
import kh.edu.istad.stadoor.consumer.event.ConsumerDeactivatedEvent;
import kh.edu.istad.stadoor.consumer.event.ConsumerRegisteredEvent;
import reactor.core.publisher.Mono;

public interface ConsumerMessageListener {
    Mono<Void> onConsumerRegisteredEvent(ConsumerRegisteredEvent event);
    Mono<Void> onConsumerActivatedEvent(ConsumerActivatedEvent event);
    Mono<Void> onConsumerDeactivatedEvent(ConsumerDeactivatedEvent event);
}
