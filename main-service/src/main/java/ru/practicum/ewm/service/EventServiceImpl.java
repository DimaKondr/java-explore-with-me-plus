package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatClient;
import ru.practicum.ewm.StatRequestParamDto;
import ru.practicum.ewm.StatResponseDto;
import ru.practicum.ewm.constants.Constants;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.exception.CreationRulesException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.EventState;
import ru.practicum.ewm.model.event.Location;
import ru.practicum.ewm.model.request.RequestStatus;
import ru.practicum.ewm.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final StatClient statClient;

    @Transactional
    @Override
    public EventFullDto addEvent(Long userId, NewEventDto dto) {
        Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(() -> new NotFoundException(
                "Добавление события. Категория с ID: " + dto.getCategory() + " не найдена."));

        User initiator = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "Добавление события. Пользователь с ID: " + userId + " не найден."));

        Location location = locationRepository.save(LocationMapper.dtoToLocation(dto.getLocation()));

        LocalDateTime eventDate = LocalDateTime.parse(dto.getEventDate(), Constants.FORMATTER);

        if (eventDate.isBefore(LocalDateTime.now()) && eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            log.error("Добавление события. Время начала события не может быть в прошлом " +
                    "и должно начинаться не ранее, чем через два часа от текущего момента.");
            throw new CreationRulesException("Время начала события не может быть в прошлом " +
                    "и должно начинаться не ранее, чем через два часа от текущего момента.");
        }

        if (dto.getPaid() == null) {
            dto.setPaid(false);
        }

        if (dto.getParticipantLimit() == null) {
            dto.setParticipantLimit(0);
        }

        if (dto.getRequestModeration() == null) {
            dto.setRequestModeration(true);
        }

        Event newEvent = EventMapper.dtoToEvent(
                dto,
                category,
                LocalDateTime.now(),
                initiator,
                location,
                null,
                EventState.PENDING
        );

        Event addedEvent = eventRepository.save(newEvent);
        return EventMapper.eventToFullDto(addedEvent, 0L, 0L);
    }

    public List<EventShortDto> getEventsOfUser(Long userId, Integer from, Integer size) {
        List<Event> events = eventRepository.findAllByInitiator_Id(userId)
                .stream()
                .sorted(Comparator.comparing(Event::getEventDate))
                .skip(from)
                .limit(size)
                .toList();
        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        LocalDateTime firstEventDate = events.getFirst().getEventDate();
        LocalDateTime lastEventDate = events.getLast().getEventDate();

        List<String> uris = new ArrayList<>();
        for (Event event : events) {
            String uri = "/events/" + event.getId();
            uris.add(uri);
        }

        StatRequestParamDto statRequestParamDto = new StatRequestParamDto(
                firstEventDate.minusHours(36L).format(Constants.FORMATTER),
                lastEventDate.plusHours(36L).format(Constants.FORMATTER),
                uris,
                true
        );

        List<StatResponseDto> stats = statClient.getStats(statRequestParamDto);

        List<EventShortDto> result = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {
            Long confirmedRequests = requestRepository.countByEvent_IdAndStatus(events.get(i).getId(),
                    RequestStatus.APPROVED.toString());
            EventShortDto eventShortDto = EventMapper.eventToShortDto(events.get(i),
                    stats.get(i).getHits(), confirmedRequests);
            result.add(eventShortDto);
        }
        return result;
    }

    public EventFullDto getEventById(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                "Получения данных о событии. Событие с ID: " + eventId + " не найдено."));

        StatRequestParamDto statRequestParamDto = new StatRequestParamDto(
                event.getEventDate().minusHours(36L).format(Constants.FORMATTER),
                event.getEventDate().plusHours(36L).format(Constants.FORMATTER),
                List.of("/events/" + event.getId()),
                true
        );

        List<StatResponseDto> stats = statClient.getStats(statRequestParamDto);

        return EventMapper.eventToFullDto(
                event,
                requestRepository.countByEvent_IdAndStatus(eventId,
                        RequestStatus.APPROVED.toString()),
                stats.getFirst().getHits()
        );
    }

//    Заглушка
    @Override
    public List<EventShortDto> getShortEventsInfoByIds(List<Long> eventIds) {
        return null;
    }

}