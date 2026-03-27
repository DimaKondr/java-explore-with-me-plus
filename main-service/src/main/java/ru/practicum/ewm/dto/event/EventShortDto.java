package ru.practicum.ewm.dto.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.user.UserShortDto;

@Value
@Builder
@Getter
@Jacksonized
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