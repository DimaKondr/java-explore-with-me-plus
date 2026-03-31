package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatClient;
import ru.practicum.ewm.StatRequestParamDto;
import ru.practicum.ewm.StatResponseDto;
import ru.practicum.ewm.constants.Constants;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.exception.CreationRulesException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.event.EventState;
import ru.practicum.ewm.model.event.EventStateAction;
import ru.practicum.ewm.model.event.Location;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.model.request.RequestStatus;
import ru.practicum.ewm.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Override
    public List<EventShortDto> getEventsOfUser(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiator_IdOrderByEventDateAsc(userId, pageable);
        if (events == null || events.isEmpty()) {
            log.info("Получение списка событий пользователя. " +
                    "По заданным параметрам (userId: {}, from: {}, size {}) события не найдены.", userId, from, size);
            return new ArrayList<>();
        }
        List<StatResponseDto> stats = getViewsStats(events);

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

    @Override
    public EventFullDto getEventById(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                "Получения данных о событии. Событие с ID: " + eventId + " не найдено."));
        List<StatResponseDto> stats = getViewsStats(List.of(event));

        return EventMapper.eventToFullDto(
                event,
                requestRepository.countByEvent_IdAndStatus(eventId,
                        RequestStatus.APPROVED.toString()),
                stats.getFirst().getHits()
        );
    }

    @Override
    public List<EventShortDto> getShortEventsInfoByIds(List<Long> eventIds) {
        List<Event> events = eventRepository.findAllByIdInOrderByIdAsc(eventIds);

        if (events == null || events.isEmpty()) {
            log.info("Получение событий по списку ID. По указанным в списке ID событий события не найдены.");
            return new ArrayList<>();
        }
        List<StatResponseDto> stats = getViewsStats(events);
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

    @Transactional
    @Override
    public EventFullDto patchEventById(Long userId, Long eventId, UpdateEventUserRequest dto) {
        Event oldEvent = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                "Обновление данных события. Событие с ID: " + eventId + " не найдено."));

        if (oldEvent.getState().equals(EventState.PUBLISHED)) {
            log.error("Обновление данных события. Изменить можно только отмененные события " +
                    "или события в состоянии ожидания модерации.");
            throw new CreationRulesException("Изменить можно только отмененные события " +
                    "или события в состоянии ожидания модерации.");
        }

        if (!userRepository.existsById(userId)) {
            log.error("Обновление данных события. Пользователь с ID: {} не найден.", userId);
            throw new NotFoundException("Обновление данных события. Пользователь с ID: " + userId + " не найден.");
        }

        if (dto.getEventDate() != null) {
            LocalDateTime eventDate = LocalDateTime.parse(dto.getEventDate(), Constants.FORMATTER);

            if (eventDate.isBefore(LocalDateTime.now()) && eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                log.error("Обновление данных события. Время начала события не может быть в прошлом " +
                        "и должно начинаться не ранее, чем через два часа от текущего момента.");
                throw new CreationRulesException("Время начала события не может быть в прошлом " +
                        "и должно начинаться не ранее, чем через два часа от текущего момента.");
            }

            oldEvent.setEventDate(eventDate);
        }

        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(() -> new NotFoundException(
                    "Обновление данных события. Категория с ID: " + dto.getCategory() + " не найдена."));

            oldEvent.setCategory(category);
        }

        if (dto.getLocation() != null) {
            oldEvent.getLocation().setLat(dto.getLocation().getLat());
            oldEvent.getLocation().setLon(dto.getLocation().getLon());
        }

        if (dto.getAnnotation() != null) {
            oldEvent.setAnnotation(dto.getAnnotation());
        }

        if (dto.getDescription() != null) {
            oldEvent.setDescription(dto.getDescription());
        }

        if (dto.getPaid() != null) {
            oldEvent.setPaid(dto.getPaid());
        }

        if (dto.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {
            oldEvent.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.getStateAction() != null) {
            if (dto.getStateAction().equals(EventStateAction.SEND_TO_REVIEW.toString())) {
                oldEvent.setState(EventState.PENDING);
            }
            if (dto.getStateAction().equals(EventStateAction.CANCEL_REVIEW.toString())) {
                oldEvent.setState(EventState.CANCELED);
            }
        }

        if (dto.getTitle() != null) {
            oldEvent.setTitle(dto.getTitle());
        }

        Event patchedEvent = eventRepository.save(oldEvent);
        List<StatResponseDto> stats = getViewsStats(List.of(patchedEvent));

        return EventMapper.eventToFullDto(
                patchedEvent,
                requestRepository.countByEvent_IdAndStatus(eventId,
                        RequestStatus.APPROVED.toString()),
                stats.getFirst().getHits()
        );

    }

    @Override
    public List<ParticipationRequestDto> getRequestsOfEvent(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            log.error("Получение запросов на участие в событии. Пользователь с ID: {} не найден.", userId);
            throw new NotFoundException("Получение запросов на участие в событии. " +
                    "Инициатор события (ID пользователя: " + userId + ") не найден.");
        }

        if (!eventRepository.existsById(eventId)) {
            log.error("Получение запросов на участие в событии. Событие с ID: {} не найдено.", userId);
            throw new NotFoundException("Получение запросов на участие в событии. " +
                    "Событие с ID: " + eventId + " не найдено.");
        }

        List<ParticipationRequest> requests = requestRepository.findAllWhereEvent_Id(eventId);
        if (requests == null || requests.isEmpty()) {
            log.info("Получение запросов на участие в событии. По событию с ID: {} запросов не найдено.", eventId);
            return new ArrayList<>();
        }
        List<ParticipationRequestDto> result = new ArrayList<>();
        for (ParticipationRequest request : requests) {
            result.add(RequestMapper.toParticipationRequestDto(request));
        }
        return result;
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult patchRequestsStatusOfEvent(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest dto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                "Обновление данных события. Событие с ID: " + eventId + " не найдено."));
        Long participantLimit = event.getParticipantLimit().longValue();
        Long approvedRequestsCount = requestRepository.countByEvent_IdAndStatus(eventId, RequestStatus.APPROVED.toString());

        if (participantLimit.equals(approvedRequestsCount)) {
            log.error("Обновление статусов заявок на участие в событии. " +
                    "Достигнут лимит одобренных заявок в событии с ID: {}.", eventId);
            throw new CreationRulesException("Достигнут лимит одобренных заявок в событии с ID: " + eventId + ".");
        }

        List<ParticipationRequest> requests = requestRepository.findAllByIdInAndStatusOrderByCreatedAsc(
                dto.getRequestIds(), RequestStatus.PENDING.toString());

        if (requests.isEmpty()) {
            log.error("Обновление статусов заявок на участие в событии. " +
                    "По списку ID не найдено запросов, ожидающих подтверждения.");
            throw new NotFoundException("По списку ID не найдено запросов, ожидающих подтверждения.");
        }

        if (dto.getRequestIds().size() != requests.size()) {
            log.error("Обновление статусов заявок на участие в событии. " +
                    "Статус можно изменить только у заявок, находящихся в состоянии ожидания.");
            throw new CreationRulesException("Статус можно изменить только у заявок, " +
                    "находящихся в состоянии ожидания: " + RequestStatus.PENDING + ". Проверьте список ID заявок");
        }

        List<ParticipationRequest> approvedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();

        for (ParticipationRequest request : requests) {
            if (approvedRequestsCount < participantLimit) {
                request.setStatus(RequestStatus.APPROVED);
                approvedRequests.add(request);
                approvedRequestsCount++;
            } else {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(request);
            }
        }

        requestRepository.saveAll(approvedRequests);
        requestRepository.saveAll(rejectedRequests);

        List<ParticipationRequestDto> resultApprovedRequestsDto = approvedRequests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
        List<ParticipationRequestDto> resultRejectedRequestsDto = rejectedRequests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();

        return new EventRequestStatusUpdateResult(resultApprovedRequestsDto, resultRejectedRequestsDto);
    }

    private List<StatResponseDto> getViewsStats(List<Event> events) {

        List<String> uris = new ArrayList<>();
        for (Event event : events) {
            String uri = "/events/" + event.getId();
            uris.add(uri);
        }

        StatRequestParamDto statRequestParamDto = new StatRequestParamDto(
                events.getFirst().getEventDate().minusHours(36L).format(Constants.FORMATTER),
                events.getLast().getEventDate().plusHours(36L).format(Constants.FORMATTER),
                uris,
                true
        );

        return statClient.getStats(statRequestParamDto);
    }

}