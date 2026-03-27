package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.EventState;
import ru.practicum.ewm.model.event.Location;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class EventMapper {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    Event dtoToEvent(
            NewEventDto eventDto,
            Category category,
            LocalDateTime createdOn,
            User initiator,
            Location location,
            LocalDateTime publishedOn,
            EventState state
    ) {
        return Event.builder()
                .annotation(eventDto.getAnnotation())
                .category(category)
                .createdOn(createdOn)
                .description(eventDto.getDescription())
                .eventDate(LocalDateTime.parse(eventDto.getEventDate(), formatter))
                .initiator(initiator)
                .location(location)
                .paid(eventDto.getPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .publishedOn(publishedOn)
                .requestModeration(eventDto.getRequestModeration())
                .state(state)
                .title(eventDto.getTitle())
                .build();
    }

    EventFullDto eventToFullDto(
            Event event,
            Long confirmedRequests,
            Long views
    ) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn().format(formatter))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(formatter))
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.locationToDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn().format(formatter))
                .requestModeration(event.getRequestModeration())
                .state(event.getState().toString())
                .title(event.getTitle())
                .views(views)
                .build();
    }

    EventShortDto eventToShortDto(
            Event event,
            Long confirmedRequests,
            Long views
    ) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate().format(formatter))
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views)
                .build();
    }

}