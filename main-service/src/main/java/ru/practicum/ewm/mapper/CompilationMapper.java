package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.CreateCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.event.Event;

import java.util.List;

@UtilityClass
public class CompilationMapper {

//    Преобразование в сущность

    Compilation toEntity(CreateCompilationDto dto, List<Event> events) {
        return Compilation.builder()
                .events(events)
                .pinned(dto.getPinned())
                .title(dto.getTitle())
                .build();
    }

    Compilation toEntity(UpdateCompilationDto dto, List<Event> events) {
        return Compilation.builder()
                .id(dto.getId())
                .events(events)
                .pinned(dto.getPinned())
                .title(dto.getTitle())
                .build();
    }

//    Преобразование в dto

    CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(events)
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}
