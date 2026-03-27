package ru.practicum.ewm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Getter
@Builder
public class CreateUpdateRequestDto {

    @NotNull
    Long userId;

    @NotNull
    Long eventId;

}
