package ru.practicum.ewm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.practicum.StatClient;
import ru.practicum.StatClientImpl;

@Configuration
public class StatClientConfig {

    @Bean
    public StatClient statClient(RestClient.Builder builder,
            @Value("${client.url:http://localhost:9090}") String baseUrl) {

        //return new StatClientImpl(RestClient.builder());
        return new StatClientImpl(builder, baseUrl);
    }
}