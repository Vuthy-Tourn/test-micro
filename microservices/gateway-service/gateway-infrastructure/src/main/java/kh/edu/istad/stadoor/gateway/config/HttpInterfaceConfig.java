package kh.edu.istad.stadoor.gateway.config;

import kh.edu.istad.stadoor.gateway.client.UserProfileClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpInterfaceConfig {

    @Bean
    public UserProfileClient customerClient(HttpInterfaceFactory factory) {
        return factory.createLoadBalanceClient(
                "http://USER-PROFILE",
                UserProfileClient.class
        );
    }
}
