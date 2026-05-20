package kh.edu.istad.stadoor.consumer.listener;

import kh.edu.istad.stadoor.consumer.event.ConsumerActivatedEvent;
import kh.edu.istad.stadoor.consumer.event.ConsumerDeactivatedEvent;
import kh.edu.istad.stadoor.consumer.event.ConsumerRegisteredEvent;
import kh.edu.istad.stadoor.consumer.port.input.message.listener.ConsumerMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ProcessingGroup("consumer-projection-group")
public class ConsumerAxonEventListener {

    private final ConsumerMessageListener consumerMessageListener;

    @EventHandler
    public void on(ConsumerRegisteredEvent event) {
        log.info("on ConsumerRegisteredEvent: consumerId={}", event.consumerId());
        consumerMessageListener.onConsumerRegisteredEvent(event).block();
    }

    @EventHandler
    public void on(ConsumerActivatedEvent event) {
        log.info("on ConsumerActivatedEvent: consumerId={}", event.consumerId());
        consumerMessageListener.onConsumerActivatedEvent(event).block();
    }

    @EventHandler
    public void on(ConsumerDeactivatedEvent event) {
        log.info("on ConsumerDeactivatedEvent: consumerId={}", event.consumerId());
        consumerMessageListener.onConsumerDeactivatedEvent(event).block();
    }
}
