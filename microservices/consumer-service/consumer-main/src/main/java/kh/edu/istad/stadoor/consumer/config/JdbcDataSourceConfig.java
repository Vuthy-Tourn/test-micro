package kh.edu.istad.stadoor.consumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JdbcDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties consumerDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource consumerDataSource(DataSourceProperties consumerDataSourceProperties) {
        return consumerDataSourceProperties.initializeDataSourceBuilder().build();
    }
}
