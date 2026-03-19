package ru.practicum.ewm;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HitDto {
    private Long id;  // id не валидируем - он генерируется

    @NotBlank(message = "app не может быть пустым")
    @Size(max = 255, message = "app не может быть длиннее 255 символов")
    private String app;

    @NotBlank(message = "uri не может быть пустым")
    @Size(max = 512, message = "uri не может быть длиннее 512 символов")
    private String uri;

    @NotBlank(message = "ip не может быть пустым")
    @Pattern(
            regexp = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$",
            message = "Неверный формат IP адреса"
    )
    private String ip;

    @NotBlank(message = "timestamp не может быть пустым")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$",
            message = "Неверный формат даты. Используйте yyyy-MM-dd HH:mm:ss"
    )
    private String timestamp;
}