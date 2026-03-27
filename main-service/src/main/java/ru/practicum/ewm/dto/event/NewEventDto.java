package ru.practicum.ewm.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Getter
@Jacksonized
public class NewEventDto {

    @NotBlank(message = "Аннотация события не может быть null или пустой")
    @Size(min = 20, max = 2000, message = "Длина аннотации должна быть не менее 20 и не более 2000 символов")
    String annotation;

    @NotNull(message = "ID категории не должен быть null")
    Long category;

    @NotBlank(message = "Описание события не может быть null или пустым")
    @Size(min = 20, max = 7000, message = "Длина описания события должна быть не менее 20 и не более 7000 символов")
    String description;

    @NotNull(message = "Дата события не должна быть null")
    String eventDate;

    @NotNull(message = "Координаты локации события не должны быть null")
    LocationDto location;

    @NotNull(message = "Условие платно ли участие в событии не должно быть null")
    Boolean paid;

    @NotNull(message = "Количество участников в событии не должно быть null")
    Integer participantLimit;

    @NotNull(message = "Условие необходимости пре-модерации не должно быть null")
    Boolean requestModeration;

    @NotBlank(message = "Заголовок события не может быть null или пустым")
    @Size(min = 3, max = 120, message = "Длина заголовка события должна быть не менее 3 и не более 120 символов")
    String title;
}