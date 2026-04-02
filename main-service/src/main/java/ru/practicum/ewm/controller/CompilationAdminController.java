package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.CreateCompilationDto;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.service.CompilationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationAdminController {

    private final CompilationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(
            @Valid @RequestBody CreateCompilationDto dto) {
        if(dto.getPinned() == null)
            dto.setPinned(false);
        return service.createCompilation(dto);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(
            @PositiveOrZero @PathVariable Long compId,
            @Valid @RequestBody CreateCompilationDto dto) {
        if(dto.getPinned() == null)
            dto.setPinned(false);
        return service.updateCompilation(CompilationMapper.toUpdateCompilationDto(compId, dto));
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCompilation(
            @PositiveOrZero @PathVariable Long compId) {
        service.removeCompilation(compId);
    }

}