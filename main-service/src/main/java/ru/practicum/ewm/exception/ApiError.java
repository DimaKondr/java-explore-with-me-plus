package ru.practicum.ewm.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ApiError {
    private final List<String> errors;
    private final String message;
    private final String reason;
    private final HttpStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    public ApiError(HttpStatus status, String reason, String message, List<String> errors) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(HttpStatus status, String reason, String message) {
        this(status, reason, message, List.of());
    }
}