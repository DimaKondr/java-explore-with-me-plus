package ru.practicum.ewm.dto.compilation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.util.List;

@Value
@Builder
@Getter
public class UpdateCompilationDto {

    @NotNull
    Long id;

    List<Long> events;

    Boolean pinned;

    String title;

}
