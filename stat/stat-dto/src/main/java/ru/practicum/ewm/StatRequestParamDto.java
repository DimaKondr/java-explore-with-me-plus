package ru.practicum.ewm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatRequestParamDto {
    private String start;
    private String end;
    private List<String> uris;
    private Boolean unique;
}