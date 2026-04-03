package ru.practicum.ewm.dto.event;

import lombok.*;

import java.util.List;

@Value
@Builder
/*@Getter
@Setter
@AllArgsConstructor*/
public class AdminEventRequestParam {
    List<Long> users;
    List<String> states;
    List<Long> categories;
    String rangeStart;
    String rangeEnd;
    Integer from;
    Integer size;
}