package ch.uzh.ifi.hase.soprafs25.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient googleMapsClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://maps.googleapis.com/maps/api")
                .build();
    }
}
