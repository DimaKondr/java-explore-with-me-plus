package ru.practicum.ewm.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Getter
@Jacksonized
public class LocationDto {

    @NotNull(message = "Координаты локации. Широта не должна быть null")
    Double lat;

    @NotNull(message = "Координаты локации. Долгота не должна быть null")
    Double lon;
}