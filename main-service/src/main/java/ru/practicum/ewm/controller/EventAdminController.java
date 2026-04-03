package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.AdminEventRequestParam;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEventsByAdminRequest(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0")
                @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10")
                @Positive Integer size
    ) {
        log.info("Уровень Admin. Получение списка из {} событий по необходимым параметрам. " +
                "Пропускаем {} элементов. ", size, from);
        AdminEventRequestParam param = AdminEventRequestParam.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        return eventService.getEventsByAdminRequest(param);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEventByIdByAdmin(
            @PathVariable
                @Positive Long eventId,
            @RequestBody
                @NotNull(message = "Уровень Admin. Данные для обновления события не могут быть null")
                @Valid UpdateEventAdminRequest dto
    ) {
        log.info("Уровень Admin. Обновление администратором данных события с ID: {}. ",eventId);
        return eventService.patchEventByIdByAdmin(eventId, dto);
    }

}