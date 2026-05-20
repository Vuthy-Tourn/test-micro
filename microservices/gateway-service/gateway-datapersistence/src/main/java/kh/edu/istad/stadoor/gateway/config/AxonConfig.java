package kh.edu.istad.stadoor.gateway.config;

import org.axonframework.common.jdbc.ConnectionProvider;
import org.axonframework.common.jdbc.DataSourceConnectionProvider;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.jdbc.JdbcTokenStore;
import org.axonframework.eventhandling.tokenstore.jdbc.TokenSchema;
import org.axonframework.serialization.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class AxonConfig {

    @Bean
    public TokenStore tokenStore(ConnectionProvider connectionProvider, Serializer serializer) {

        // Define the schema to match your snake_case table
        TokenSchema tokenSchema = TokenSchema.builder()
                .setTokenTable("token_entry") // Match your DB table name exactly
                // If your columns also use underscores, define them here:
                 .setProcessorNameColumn("processor_name")
                 .setTokenTypeColumn("token_type")
                .setTokenColumn("token")
                .setSegmentColumn("segment")
                .setOwnerColumn("owner")
                .setTimestampColumn("timestamp")
                .build();

        return JdbcTokenStore.builder()
                .schema(tokenSchema)
                .connectionProvider(connectionProvider)
                .contentType(byte[].class)
                .serializer(serializer)
                .build();
    }

    @Bean
    public ConnectionProvider connectionProvider(DataSource dataSource) {
        return new DataSourceConnectionProvider(dataSource);
    }

}
