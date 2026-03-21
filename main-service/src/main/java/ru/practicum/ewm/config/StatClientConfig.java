package ru.practicum.ewm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import ru.practicum.StatClient;
import ru.practicum.StatClientImpl;

@Configuration
public class StatClientConfig {

    @Bean
    public StatClient statClient() {
        return new StatClientImpl(RestClient.builder());
    }
}