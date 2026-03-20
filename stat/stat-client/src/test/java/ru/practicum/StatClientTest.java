package ru.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.ewm.HitDto;
import ru.practicum.ewm.StatRequestParamDto;
import ru.practicum.ewm.StatResponseDto;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class StatClientTest {

    private HitDto hitDto;
    private StatRequestParamDto requestParamDto;

    @BeforeEach
    void setUp() {
        hitDto = new HitDto();
        hitDto.setId(1L);
        hitDto.setApp("test-app");
        hitDto.setUri("/test");
        hitDto.setIp("127.0.0.1");
        hitDto.setTimestamp("2024-01-01 12:00:00");

        requestParamDto = new StatRequestParamDto();
        requestParamDto.setStart("2024-01-01 00:00:00");
        requestParamDto.setEnd("2024-01-01 23:59:59");
        requestParamDto.setUris(Arrays.asList("/test"));
        requestParamDto.setUnique(false);
    }

    @Test
    void testPostHitShouldCreateValidHitDto() {
        HitDto dto = new HitDto();
        dto.setApp("test-app");
        dto.setUri("/test");
        dto.setIp("127.0.0.1");
        dto.setTimestamp("2024-01-01 12:00:00");

        assertNotNull(dto);
        assertEquals("test-app", dto.getApp());
        assertEquals("/test", dto.getUri());
        assertEquals("127.0.0.1", dto.getIp());
        assertEquals("2024-01-01 12:00:00", dto.getTimestamp());
    }

    @Test
    void testPostHitWithInvalidDataShouldCreateEmptyHitDto() {
        HitDto emptyDto = new HitDto();

        assertNotNull(emptyDto);
        assertNull(emptyDto.getId());
        assertNull(emptyDto.getApp());
        assertNull(emptyDto.getUri());
        assertNull(emptyDto.getIp());
    }

    @Test
    void testGetStatsShouldCreateValidRequestParam() {
        StatRequestParamDto dto = new StatRequestParamDto();
        dto.setStart("2024-01-01 00:00:00");
        dto.setEnd("2024-01-01 23:59:59");
        dto.setUris(Arrays.asList("/test1", "/test2"));
        dto.setUnique(true);

        assertNotNull(dto);
        assertEquals("2024-01-01 00:00:00", dto.getStart());
        assertEquals("2024-01-01 23:59:59", dto.getEnd());
        assertEquals(2, dto.getUris().size());
        assertEquals("/test1", dto.getUris().get(0));
        assertEquals("/test2", dto.getUris().get(1));
        assertTrue(dto.getUnique());
    }

    @Test
    void testGetStatsWithNullUrisShouldHandleNull() {
        // Проверяем обработку null в uris
        StatRequestParamDto dto = new StatRequestParamDto();
        dto.setStart("2024-01-01 00:00:00");
        dto.setEnd("2024-01-01 23:59:59");
        dto.setUris(null);
        dto.setUnique(false);

        assertNotNull(dto);
        assertNull(dto.getUris());
    }

    @Test
    void testGetStatsWithEmptyUrisShouldHandleEmptyList() {
        StatRequestParamDto dto = new StatRequestParamDto();
        dto.setStart("2024-01-01 00:00:00");
        dto.setEnd("2024-01-01 23:59:59");
        dto.setUris(Arrays.asList());
        dto.setUnique(false);

        assertNotNull(dto);
        assertTrue(dto.getUris().isEmpty());
    }

    @Test
    void testStatResponseDtoShouldStoreDataCorrectly() {
        StatResponseDto response = new StatResponseDto("app1", "/test1", 5L);

        assertNotNull(response);
        assertEquals("app1", response.getApp());
        assertEquals("/test1", response.getUri());
        assertEquals(5L, response.getHits());
    }

    @Test
    void testStatResponseDtoShouldHandleZeroHits() {
        // Проверяем обработку нулевого количества хитов
        StatResponseDto response = new StatResponseDto("app1", "/test1", 0L);

        assertNotNull(response);
        assertEquals(0L, response.getHits());
    }
}