package ru.practicum.ewm.dto.event;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.user.UserShortDto;

@Value
@Builder
@Getter
@AllArgsConstructor
public class EventShortDto {
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    String eventDate;
    Long id;
    UserShortDto initiator;
    Boolean paid;
    String title;
    Long views;
}