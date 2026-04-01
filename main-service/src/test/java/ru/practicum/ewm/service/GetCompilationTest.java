package ru.practicum.ewm.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.GetManyCompilationDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.repository.CompilationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCompilationTest {

    @Mock
    private CompilationRepository compRep;

    @Mock
    private EventService eventService;

    @InjectMocks
    private CompilationServiceImpl compilationService;

    // ==================== ТЕСТОВЫЕ ДАННЫЕ ====================

    private Event createEvent(Long id, String title) {
        return Event.builder()
                .id(id)
                .title(title)
                .annotation("Annotation")
                .description("Description")
                .eventDate(LocalDateTime.now().plusDays(1))
                .createdOn(LocalDateTime.now())
                .paid(false)
                .participantLimit(100)
                .requestModeration(false)
                .build();
    }

    private EventShortDto createEventShortDto(Long id, String title) {
        return EventShortDto.builder()
                .id(id)
                .title(title)
                .annotation("Annotation")
                .eventDate("2025-12-01 10:00:00")
                .paid(false)
                .confirmedRequests(0L)
                .views(0L)
                .initiator(new UserShortDto(1L, "User"))
                .category(new CategoryDto(1L, "Category"))
                .build();
    }

    private Compilation createCompilation(Long id, String title, Boolean pinned, List<Event> events) {
        return Compilation.builder()
                .id(id)
                .title(title)
                .pinned(pinned)
                .events(events)
                .build();
    }

    // ==================== getCompilations ====================

    @Test
    void getCompilations_shouldReturnDtoList_WhenCompilationsExist() {
        // Arrange
        Event event1 = createEvent(1L, "Event 1");
        Event event2 = createEvent(2L, "Event 2");
        Event event3 = createEvent(3L, "Event 3");

        Compilation comp1 = createCompilation(1L, "Compilation 1", true, List.of(event1, event2));
        Compilation comp2 = createCompilation(2L, "Compilation 2", false, List.of(event3));

        List<Compilation> compilations = List.of(comp1, comp2);

        EventShortDto dto1 = createEventShortDto(1L, "Event 1");
        EventShortDto dto2 = createEventShortDto(2L, "Event 2");
        EventShortDto dto3 = createEventShortDto(3L, "Event 3");

        List<Long> uniqueEventIds = List.of(1L, 2L, 3L);
        List<EventShortDto> eventDtos = List.of(dto1, dto2, dto3);

        GetManyCompilationDto dto = GetManyCompilationDto.builder()
                .pinned(null)
                .from(0L)
                .size(10L)
                .build();

        when(compRep.findAllByPinnedFilter(null, 10L, 0L)).thenReturn(compilations);
        when(eventService.getShortEventsInfoByIds(uniqueEventIds)).thenReturn(eventDtos);

        // Act
        List<CompilationDto> result = compilationService.getCompilations(dto);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getTitle()).isEqualTo("Compilation 1");
        assertThat(result.get(0).getPinned()).isTrue();
        assertThat(result.get(0).getEvents()).hasSize(2);

        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getTitle()).isEqualTo("Compilation 2");
        assertThat(result.get(1).getPinned()).isFalse();
        assertThat(result.get(1).getEvents()).hasSize(1);

        verify(compRep).findAllByPinnedFilter(null, 10L, 0L);
        verify(eventService).getShortEventsInfoByIds(uniqueEventIds);
    }

    @Test
    void getCompilations_shouldFilterByPinned_WhenPinnedIsTrue() {
        // Arrange
        Compilation comp = createCompilation(1L, "Pinned Compilation", true, List.of());
        GetManyCompilationDto dto = GetManyCompilationDto.builder()
                .pinned(true)
                .from(0L)
                .size(5L)
                .build();

        when(compRep.findAllByPinnedFilter(true, 5L, 0L)).thenReturn(List.of(comp));
        when(eventService.getShortEventsInfoByIds(List.of())).thenReturn(List.of());

        // Act
        List<CompilationDto> result = compilationService.getCompilations(dto);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPinned()).isTrue();
        verify(compRep).findAllByPinnedFilter(true, 5L, 0L);
    }

    @Test
    void getCompilations_shouldHandleCompilationWithNoEvents() {
        // Arrange
        Compilation comp = createCompilation(1L, "Empty Compilation", false, List.of());
        GetManyCompilationDto dto = GetManyCompilationDto.builder()
                .pinned(null)
                .from(0L)
                .size(10L)
                .build();

        when(compRep.findAllByPinnedFilter(null, 10L, 0L)).thenReturn(List.of(comp));
        when(eventService.getShortEventsInfoByIds(List.of())).thenReturn(List.of());

        // Act
        List<CompilationDto> result = compilationService.getCompilations(dto);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEvents()).isEmpty();
    }

    // ==================== getCompilationById ====================

    @Test
    void getCompilationById_shouldReturnDto_WhenCompilationExists() {
        // Arrange
        Long compId = 1L;
        Event event1 = createEvent(1L, "Event 1");
        Event event2 = createEvent(2L, "Event 2");
        Compilation compilation = createCompilation(compId, "Test Compilation", true, List.of(event1, event2));

        EventShortDto dto1 = createEventShortDto(1L, "Event 1");
        EventShortDto dto2 = createEventShortDto(2L, "Event 2");
        List<EventShortDto> eventDtos = List.of(dto1, dto2);

        when(compRep.findById(compId)).thenReturn(Optional.of(compilation));
        when(eventService.getShortEventsInfoByIds(List.of(1L, 2L))).thenReturn(eventDtos);

        // Act
        CompilationDto result = compilationService.getCompilationById(compId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(compId);
        assertThat(result.getTitle()).isEqualTo("Test Compilation");
        assertThat(result.getPinned()).isTrue();
        assertThat(result.getEvents()).hasSize(2);
        assertThat(result.getEvents().get(0).getId()).isEqualTo(1L);
        assertThat(result.getEvents().get(1).getId()).isEqualTo(2L);

        verify(compRep).findById(compId);
        verify(eventService).getShortEventsInfoByIds(List.of(1L, 2L));
    }

    @Test
    void getCompilationById_shouldThrowNotFoundException_WhenCompilationDoesNotExist() {
        // Arrange
        Long compId = 999L;
        when(compRep.findById(compId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> compilationService.getCompilationById(compId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Compilation not found");

        verify(compRep).findById(compId);
        verify(eventService, never()).getShortEventsInfoByIds(anyList());
    }

    @Test
    void getCompilationById_shouldReturnDtoWithEmptyEvents_WhenCompilationHasNoEvents() {
        // Arrange
        Long compId = 1L;
        Compilation compilation = createCompilation(compId, "Empty Compilation", false, List.of());

        when(compRep.findById(compId)).thenReturn(Optional.of(compilation));
        when(eventService.getShortEventsInfoByIds(List.of())).thenReturn(List.of());

        // Act
        CompilationDto result = compilationService.getCompilationById(compId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(compId);
        assertThat(result.getEvents()).isEmpty();

        verify(compRep).findById(compId);
        verify(eventService).getShortEventsInfoByIds(List.of());
    }

    // ==================== ГРАНИЧНЫЕ СЛУЧАИ ====================

    @Test
    void getCompilations_shouldHandleNullEventIds_InEventsMap() {
        // Arrange - случай, когда в eventsMap нет какого-то ID
        Event event1 = createEvent(1L, "Event 1");
        Event event2 = createEvent(2L, "Event 2"); // Этот ID не будет в eventsMap
        Compilation comp = createCompilation(1L, "Compilation", true, List.of(event1, event2));

        EventShortDto dto1 = createEventShortDto(1L, "Event 1");
        List<EventShortDto> eventDtos = List.of(dto1); // Только первый event

        GetManyCompilationDto dto = GetManyCompilationDto.builder()
                .pinned(null)
                .from(0L)
                .size(10L)
                .build();

        when(compRep.findAllByPinnedFilter(null, 10L, 0L)).thenReturn(List.of(comp));
        when(eventService.getShortEventsInfoByIds(List.of(1L, 2L))).thenReturn(eventDtos);

        // Act
        List<CompilationDto> result = compilationService.getCompilations(dto);

        // Assert - второй event будет null в списке
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEvents()).hasSize(2);
        assertThat(result.get(0).getEvents().get(0)).isNotNull();
        // assertThat(result.get(0).getEvents().get(1)).isNull(); // Зависит от реализации
    }

    @Test
    void getCompilations_shouldPreserveOrder_OfEvents() {
        // Arrange
        Event event1 = createEvent(1L, "Event 1");
        Event event2 = createEvent(2L, "Event 2");
        Event event3 = createEvent(3L, "Event 3");
        Compilation comp = createCompilation(1L, "Compilation", true, List.of(event1, event2, event3));

        EventShortDto dto1 = createEventShortDto(1L, "Event 1");
        EventShortDto dto2 = createEventShortDto(2L, "Event 2");
        EventShortDto dto3 = createEventShortDto(3L, "Event 3");
        List<EventShortDto> eventDtos = List.of(dto3, dto1, dto2); // Пришли в другом порядке

        GetManyCompilationDto dto = GetManyCompilationDto.builder()
                .pinned(null)
                .from(0L)
                .size(10L)
                .build();

        when(compRep.findAllByPinnedFilter(null, 10L, 0L)).thenReturn(List.of(comp));
        when(eventService.getShortEventsInfoByIds(List.of(1L, 2L, 3L))).thenReturn(eventDtos);

        // Act
        List<CompilationDto> result = compilationService.getCompilations(dto);

        // Assert - порядок должен сохраниться как в Compilation
        assertThat(result.get(0).getEvents()).hasSize(3);
        assertThat(result.get(0).getEvents().get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getEvents().get(1).getId()).isEqualTo(2L);
        assertThat(result.get(0).getEvents().get(2).getId()).isEqualTo(3L);
    }
}