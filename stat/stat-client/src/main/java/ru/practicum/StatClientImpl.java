package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.ewm.HitDto;
import ru.practicum.ewm.StatRequestParamDto;
import ru.practicum.ewm.StatResponseDto;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
public class StatClientImpl implements StatClient {

    private final RestClient restClient;

    public StatClientImpl(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:9090")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public HitDto postHit(HitDto dto) {
        try {
            return restClient.post()
                    .uri("/hit")
                    .contentType(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .body(dto)
                    .retrieve()
                    .body(HitDto.class);
        } catch (Exception e) {
            log.error("Неудачная попытка добавления записи в сервис статистики. Запись: {}", dto);
            return new HitDto();
        }
    }

    @Override
    public List<StatResponseDto> getStats(StatRequestParamDto dto) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/stats")
                            .queryParam("start", dto.getStart())
                            .queryParam("end", dto.getEnd())
                            .queryParam("uris", dto.getUris())
                            .queryParam("unique", dto.getUnique())
                            .build())
                    .accept(APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<StatResponseDto>>() {});
        } catch (Exception e) {
            log.error("Неудачная попытка получения данных статистики из сервиса статистики. " +
                    "Параметры запроса: {}", dto);
            return new ArrayList<>();
        }
    }

}