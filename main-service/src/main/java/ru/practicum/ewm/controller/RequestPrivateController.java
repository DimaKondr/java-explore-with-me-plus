package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.CreateUpdateRequestDto;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestPrivateController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(
            @PositiveOrZero @PathVariable("userId") Long userId,
            @Valid @RequestBody CreateUpdateRequestDto request
    ) {
        log.info("POST /users/{}/requests - создание запроса на участие:", userId);
        return requestService.createRequest(userId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestByUserId(
            @PositiveOrZero @PathVariable("userId") Long userId
    ) {
        log.info("GET /users/{}/requests - получение запросов по пользователю", userId);
        return requestService.getRequestByUserId(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto canceledRequest(
            @PositiveOrZero @PathVariable("userId") Long userId,
            @PositiveOrZero @PathVariable("requestId") Long requestId,
            @Valid @RequestBody CreateUpdateRequestDto request
    ) {
        log.info("PATCH /users/{}/requests/cancel - отмена запроса на участие в событии", userId);
        return requestService.canceledRequest(userId, requestId, request);
    }
}