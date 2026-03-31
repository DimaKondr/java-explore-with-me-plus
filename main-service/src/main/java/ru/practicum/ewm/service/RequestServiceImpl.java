package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.request.CreateUpdateRequestDto;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.request.RequestStatus;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(CreateUpdateRequestDto dto) {
//        Дата создания
        LocalDateTime nowDate = LocalDateTime.now();
//        Получение сущностей для создания связей через JPA
        Event event = findEvent(dto.getEventId());
        User requester = findUser(dto.getUserId());
//        Получение готовой сущности
        ParticipationRequest req = RequestMapper.toEntity(
                nowDate,
                event,
                requester,
                RequestStatus.PENDING
        );
//        Сохранение
        return RequestMapper.toParticipationRequestDto(
                requestRepository.save(req)
        );
    }

    @Override
    public List<ParticipationRequestDto> getRequestByUserId(Long userId) {
        return requestRepository.findAllByUserId(userId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
    }

    @Transactional
    @Override
    public ParticipationRequestDto canceledRequest(Long userId, Long requestId) {
//        Проверка на существование сущностей
        User requester = findUser(userId);

//        Меняем статус
        if (requestRepository.changeState(requestId, RequestStatus.CANCELED) > 0)
            log.info("Статус запроса с id:{}, успешно изменён на {}}", requestId, RequestStatus.CANCELED);

        return RequestMapper.toParticipationRequestDto(
                findParticipationRequest(requestId)
        );
    }

//    Получение пользователя
    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id " + userId + " not found")
        );
    }

//    Получение события
    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id " + eventId + " not found")
        );
    }

//    Получение запроса
    private ParticipationRequest findParticipationRequest(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Request with id " + requestId + " not found")
        );
    }
}
