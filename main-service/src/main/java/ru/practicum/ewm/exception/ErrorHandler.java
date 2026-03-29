package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return "Field: " + fieldError.getField() + ". Error: " + error.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.toList());

        log.error("Ошибка валидации: {}", errors);
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                "Validation failed",
                errors
        );
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(NotFoundException ex) {
        log.error("Объект не найден: {}", ex.getMessage());
        return new ApiError(
                HttpStatus.NOT_FOUND,
                "The required object was not found.",
                ex.getMessage()
        );
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(ValidationException ex) {
        log.error("Нарушение валидации объекта: {}", ex.getMessage());
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                ex.getMessage()
        );
    }

    @ExceptionHandler(CreationRulesException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleCreationRules(CreationRulesException ex) {
        log.error("Нарушение правил создания объекта: {}", ex.getMessage());
        return new ApiError(
                HttpStatus.CONFLICT,
                "For the requested operation the conditions are not met.",
                ex.getMessage()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Нарушение целостности данных: {}", ex.getMessage());
        return new ApiError(
                HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                ex.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Некорректный аргумент: {}", ex.getMessage());
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                ex.getMessage()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleJsonParseException(HttpMessageNotReadableException ex) {
        log.error("Ошибка парсинга JSON: {}", ex.getMessage());
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                "Invalid JSON format"
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingParams(MissingServletRequestParameterException ex) {
        log.error("Отсутствует параметр: {}", ex.getParameterName());
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                "Missing parameter: " + ex.getParameterName()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleAllExceptions(Exception ex) {
        log.error("Внутренняя ошибка сервера: {}", ex.getMessage(), ex);
        return new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                ex.getMessage()
        );
    }
}