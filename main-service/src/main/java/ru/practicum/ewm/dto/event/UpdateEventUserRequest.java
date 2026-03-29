package ru.practicum.ewm.dto.event;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import ru.practicum.ewm.annotation.NotBlankOrNull;

@Value
@Builder
@Getter
@AllArgsConstructor
public class UpdateEventUserRequest {

    @NotBlankOrNull
    @Size(min = 20, max = 2000, message = "Длина аннотации должна быть не менее 20 и не более 2000 символов")
    String annotation;
    Long categoryId;

    @NotBlankOrNull
    @Size(min = 20, max = 7000, message = "Длина описания события должна быть не менее 20 и не более 7000 символов")
    String description;
    String eventDate;
    LocationDto location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    String stateAction;

    @NotBlankOrNull
    @Size(min = 3, max = 120, message = "Длина заголовка события должна быть не менее 3 и не более 120 символов")
    String title;
}