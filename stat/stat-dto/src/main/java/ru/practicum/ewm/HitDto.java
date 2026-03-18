package ru.practicum.ewm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HitDto {
    private Long id;
    private String app;
    private String uri;
    private String ip;
    private String timeStamp;
}