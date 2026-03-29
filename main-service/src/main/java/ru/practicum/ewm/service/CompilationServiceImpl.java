package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.CreateCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
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

    @Override
    public CompilationDto createCompilation(CreateCompilationDto dto) {
        List<Event> events = eventRep.findAllById(dto.getEvents());
        return CompilationMapper.toCompilationDto(
                compRep.save(CompilationMapper.toEntity(dto, events)),
                eventService.getShortEventsInfoByIds(dto.getEvents())
        );
    }

    @Override
    public CompilationDto updateCompilation(UpdateCompilationDto dto) {
        if(!compRep.existsById(dto.getId()))
            throw new NotFoundException("Compilation not found");
        List<Event> events = eventRep.findAllById(dto.getEvents());
        if(events.size() != dto.getEvents().size())
            throw new NotFoundException("Часть переданных событий не существует");
        return CompilationMapper.toCompilationDto(
                compRep.save(CompilationMapper.toEntity(dto, events)),
                eventService.getShortEventsInfoByIds(dto.getEvents())
        );
    }

    @Override
    public void removeCompilation(Long compId) {
        if(!compRep.existsById(compId))
            throw new NotFoundException("Compilation not found");
        compRep.deleteById(compId);
    }

}
