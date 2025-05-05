package ru.practicum.shareit.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException exception,
                                                 final HttpServletRequest request) {
        log.warn("Ресурс не найден: {}", exception.getMessage());
        return new ErrorResponse(
                "Ресурс не найден",
                exception.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicatedDataException(final DuplicatedDataException exception,
                                                       final HttpServletRequest request) {
        log.warn("Конфликт данных (обнаружено дублирование): {}", exception.getMessage());
        return new ErrorResponse(
                "Конфликт данных",
                exception.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCustomValidationException(final ValidationException exception,
                                                         final HttpServletRequest request) {
        log.warn("Ошибка бизнес-валидации {}", exception.getMessage());
        return new ErrorResponse(
                "Ошибка бизнес-валидации",
                exception.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception,
                                                               final HttpServletRequest request) {
        String errorMessage = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Ошибка валидации тела запроса: {}", errorMessage);
        return new ErrorResponse(
                "Ошибка валидации тела запроса",
                errorMessage,
                request.getRequestURI(),
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException exception,
                                                            final HttpServletRequest request) {
        String errorMessage = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));
        log.warn("Ошибка валидации параметров запроса: {}", errorMessage);
        return new ErrorResponse(
                "Ошибка валидации параметров запроса",
                errorMessage,
                request.getRequestURI(),
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value()
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeaderException(final MissingRequestHeaderException exception,
                                                             final HttpServletRequest request) {
        log.warn("Не передан обязательный заголовок: {}", exception.getHeaderName());
        return new ErrorResponse(
                "Ошибка запроса",
                "Не передан обязательный заголовок: " + exception.getHeaderName(),
                request.getRequestURI(),
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpectedException(final Exception exception,
                                                   final HttpServletRequest request) {
        log.error("Внутренняя ошибка сервера: {}", exception.getMessage(), exception);
        return new ErrorResponse(
                "Внутренняя ошибка сервера",
                "Произошла непредвиденная ошибка",
                request.getRequestURI(),
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
    }
}