package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.CreateCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationDto;
import ru.practicum.ewm.model.Compilation;

@UtilityClass
public class CompilationMapper {

//    Преобразование в сущность

    Compilation toEntity(CreateCompilationDto dto) {
        return Compilation.builder()
                .events(dto.getEvents())
                .pinned(dto.getPinned())
                .title(dto.getTitle())
                .build();
    }

    Compilation toEntity(UpdateCompilationDto dto) {
        return Compilation.builder()
                .id(dto.getId())
                .events(dto.getEvents())
                .pinned(dto.getPinned())
                .title(dto.getTitle())
                .build();
    }

//    Преобразование в dto

    CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}
