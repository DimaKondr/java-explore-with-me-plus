package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.RequestStatus;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class RequestMapper {

//    Преобразование в сущность

    ParticipationRequest toEntity(LocalDateTime nowData, Event event, User requester, RequestStatus status) {
        return ParticipationRequest.builder()
                .created(nowData)
                .event(event)
                .requester(requester)
                .status(status)
                .build();
    }

//    Преобразование в dto

    ParticipationRequestDto toParticipationRequestDto(ParticipationRequest req) {
        return ParticipationRequestDto.builder()
                .id(req.getId())
                .created(req.getCreated().toString())
                .event(req.getId())
                .requester(req.getRequester().getId())
                .status(req.getStatus().toString())
                .build();
    }

}
