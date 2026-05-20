package kh.edu.istad.stadoor.eureka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

        @Bean
        public WebClient webClient(@Value("${eureka-api.base-url:http://localhost:8761/eureka/v2}") String eurekaApiBaseUrl) {
            return WebClient.builder()
                    .baseUrl(eurekaApiBaseUrl)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();
        }

}
