package ru.practicum.ewm.dto.compilation;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import ru.practicum.ewm.model.Event;

import java.util.List;

@Value
@Builder
@Getter
public class CompilationDto {

    Long id;
    List<Event> events;
    Boolean pinned;
    String title;

}
