package kh.edu.istad.stadoor.consumer.config;

import org.axonframework.config.ConfigurerModule;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {

    public static final String CONSUMER_PROJECTION_GROUP = "consumer-projection-group";

    @Bean
    public SnapshotTriggerDefinition consumerSnapshotTriggerDefinition(Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, 5);
    }

    @Bean
    public ConfigurerModule consumerProjectionProcessorConfigurerModule() {
        return configurer -> configurer.eventProcessing()
                .registerTrackingEventProcessor(CONSUMER_PROJECTION_GROUP)
                .registerTrackingEventProcessorConfiguration(
                        CONSUMER_PROJECTION_GROUP,
                        configuration -> TrackingEventProcessorConfiguration
                                .forSingleThreadedProcessing()
                                .andInitialTrackingToken(source -> source.createHeadToken())
                );
    }

    @Bean
    public ConfigurerModule enqueuePolicyConfigurerModule() {
        return configurer -> configurer.eventProcessing().registerDeadLetterPolicy(
                CONSUMER_PROJECTION_GROUP, config -> new RetryConstrainedEnqueuePolicy(5)
        );
    }
}
