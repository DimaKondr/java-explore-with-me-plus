package ru.practicum.ewm.dto.event;

import lombok.*;

import java.util.List;

@Value
@Builder
/*@Getter
@Setter
@AllArgsConstructor*/
public class PublicEventRequestParam {
    String text;
    List<Long> categories;
    Boolean paid;
    String rangeStart;
    String rangeEnd;
    Boolean onlyAvailable;
    String sort;
    Integer from;
    Integer size;
}