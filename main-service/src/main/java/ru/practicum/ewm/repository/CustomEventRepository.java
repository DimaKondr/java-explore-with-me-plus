package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.event.AdminEventParam;
import ru.practicum.ewm.model.event.Event;

import java.util.List;

public interface CustomEventRepository {

    List<Event> findByAdminParam(AdminEventParam param, Pageable pageable);

}