package kh.edu.istad.stadoor.consumer.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

public record ActivateConsumerCommand(

        @TargetAggregateIdentifier
        UUID consumerId
) {
}
