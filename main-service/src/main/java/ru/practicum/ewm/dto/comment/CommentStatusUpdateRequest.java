package ru.practicum.ewm.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentStatusUpdateRequest {
    @NotBlank(message = "Статус обязателен")
    private String status;
}