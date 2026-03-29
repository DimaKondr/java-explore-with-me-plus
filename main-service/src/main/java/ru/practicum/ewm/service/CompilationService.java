package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.CreateCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationDto;

public interface CompilationService {
    CompilationDto createCompilation(CreateCompilationDto dto);

    CompilationDto updateCompilation(UpdateCompilationDto dto);

    void removeCompilation(Long compId);
}
