package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(
            @PathVariable Long userId,
            @RequestBody
                @NotNull(message = "Добавляемое событие не может быть null")
                @Valid NewEventDto dto
    ) {
        log.info("Создание нового события {} пользователем с ID: {}", dto, userId);
        return eventService.addEvent(userId, dto);
    }

    @GetMapping
    public List<EventShortDto> getEventsOfUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("Получение списка из {} событий пользователя с ID: {}. Пропускаем {} элементов. ", size, userId, from);
        return eventService.getEventsOfUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        log.info("Получение пользователем с ID: {} созданного им события с ID: {}. ", userId, eventId);
        return eventService.getEventById(userId, eventId);
    }

}