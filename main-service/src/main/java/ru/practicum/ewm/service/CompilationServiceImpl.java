package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.CreateCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compRep;
    private final EventRepository eventRep;
    private final EventService eventService;

    @Transactional
    @Override
    public CompilationDto createCompilation(CreateCompilationDto dto) {
        List<Event> events = eventRep.findAllById(dto.getEvents());
        return CompilationMapper.toCompilationDto(
                compRep.save(CompilationMapper.toEntity(dto, events)),
                eventService.getShortEventsInfoByIds(dto.getEvents())
        );
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(UpdateCompilationDto dto) {
        Compilation old = compRep.findById(dto.getId()).orElseThrow(
                () -> new NotFoundException("Compilation not found")
        );
        List<Event> events = eventRep.findAllById(dto.getEvents());
        if (events.size() != dto.getEvents().size())
            throw new NotFoundException("Часть переданных событий не существует");
        Compilation comp = update(old, dto, events);
        return CompilationMapper.toCompilationDto(
                compRep.save(comp),
                eventService.getShortEventsInfoByIds(dto.getEvents())
        );
    }

    @Transactional
    @Override
    public void removeCompilation(Long compId) {
        if (!compRep.existsById(compId))
            throw new NotFoundException("Compilation not found");
        compRep.deleteById(compId);
    }

    private Compilation update(Compilation old, UpdateCompilationDto newDto, List<Event> events) {
        return Compilation.builder()
                .id(old.getId())
                .events(events != null
                        ? events
                        : old.getEvents())
                .title(newDto.getTitle() != null
                        ? newDto.getTitle()
                        : old.getTitle())
                .pinned(newDto.getPinned() != null
                        ? newDto.getPinned()
                        : old.getPinned())
                .build();
    }

}
