package kh.edu.istad.stadoor.consumer.config;

import org.axonframework.common.jdbc.ConnectionProvider;
import org.axonframework.common.jdbc.DataSourceConnectionProvider;
import org.axonframework.common.jdbc.UnitOfWorkAwareConnectionProviderWrapper;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.tokenstore.jdbc.JdbcTokenStore;
import org.axonframework.eventhandling.tokenstore.jdbc.TokenSchema;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.jdbc.EventSchema;
import org.axonframework.eventsourcing.eventstore.jdbc.JdbcEventStorageEngine;
import org.axonframework.serialization.Serializer;
import org.axonframework.spring.messaging.unitofwork.SpringTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class AxonJdbcConfig {

    @Bean
    public EventSchema eventSchema() {
        return EventSchema.builder()
                .eventTable("domain_event_entry")
                .snapshotTable("snapshot_event_entry")
                .globalIndexColumn("global_index")
                .timestampColumn("time_stamp")
                .eventIdentifierColumn("event_identifier")
                .aggregateIdentifierColumn("aggregate_identifier")
                .sequenceNumberColumn("sequence_number")
                .typeColumn("type")
                .payloadTypeColumn("payload_type")
                .payloadRevisionColumn("payload_revision")
                .payloadColumn("payload")
                .metaDataColumn("meta_data")
                .build();
    }

    @Bean
    public TokenSchema tokenSchema() {
        return TokenSchema.builder()
                .setTokenTable("token_entry")
                .setProcessorNameColumn("processor_name")
                .setSegmentColumn("segment")
                .setTokenColumn("token")
                .setTokenTypeColumn("token_type")
                .setTimestampColumn("timestamp")
                .setOwnerColumn("owner")
                .build();
    }

    @Bean
    public ConnectionProvider connectionProvider(DataSource dataSource) {
        return new UnitOfWorkAwareConnectionProviderWrapper(
                new DataSourceConnectionProvider(dataSource));
    }

    @Bean
    public TransactionManager axonTransactionManager(DataSource dataSource) {
        return new SpringTransactionManager(new DataSourceTransactionManager(dataSource));
    }

    @Bean
    public JdbcEventStorageEngine eventStorageEngine(Serializer serializer,
            EventSchema eventSchema,
            ConnectionProvider connectionProvider,
            TransactionManager axonTransactionManager) {
        return JdbcEventStorageEngine.builder()
                .schema(eventSchema)
                .eventSerializer(serializer)
                .snapshotSerializer(serializer)
                .dataType(byte[].class)
                .connectionProvider(connectionProvider)
                .transactionManager(axonTransactionManager)
                .build();
    }

    @Bean
    @Primary
    public EventStore eventStore(JdbcEventStorageEngine storageEngine) {
        return EmbeddedEventStore.builder()
                .storageEngine(storageEngine)
                .build();
    }

    @Bean
    public JdbcTokenStore tokenStore(ConnectionProvider connectionProvider,
            TokenSchema tokenSchema,
            Serializer serializer) {
        return JdbcTokenStore.builder()
                .schema(tokenSchema)
                .connectionProvider(connectionProvider)
                .contentType(byte[].class)
                .serializer(serializer)
                .build();
    }
}
