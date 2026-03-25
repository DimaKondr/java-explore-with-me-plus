package ru.practicum.ewm.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import ru.practicum.ewm.model.Event;

import java.util.List;

@Value
@Builder
@Getter
public class UpdateCompilationDto {

    @NotNull
    Long id;

    List<Event> events;

    @NotNull
    Boolean pinned;

    @NotNull
    @NotBlank
    String title;

}
