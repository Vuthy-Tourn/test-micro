package kh.edu.istad.stadoor.consumer.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public record DeactivateConsumerCommand(

        @TargetAggregateIdentifier
        UUID consumerId
) {
}
