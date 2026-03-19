package ru.practicum;

import ru.practicum.ewm.HitDto;
import ru.practicum.ewm.StatRequestParamDto;
import ru.practicum.ewm.StatResponseDto;

public interface StatClient {

//    Добавит запись в статистику
    HitDto postHit(HitDto dto);

//    Получить статистику
    StatResponseDto getStats(StatRequestParamDto dto);

}
