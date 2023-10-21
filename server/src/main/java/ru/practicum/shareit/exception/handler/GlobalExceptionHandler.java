package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.InvalidArguments;
import ru.practicum.shareit.exception.NotUniqueEmail;
import ru.practicum.shareit.exception.ResourceNotFoundException;

import java.net.BindException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Получен статус 404 Not found {}", ex.getMessage(), ex);
        return new ErrorResponse(ex.toString(), ex.getMessage());
    }

    @ExceptionHandler(NotUniqueEmail.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleNotUniqueEmail(NotUniqueEmail ex) {
        log.warn("Получен статус 409 Conflict {}", ex.getMessage(), ex);
        return new ErrorResponse(ex.toString(), ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, InvalidArguments.class, BindException.class,
            MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(RuntimeException ex) {
        log.warn("Получен статус 400 Bad request {}", ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage(), ex.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleMethodArgumentNotValidException(Throwable ex) {
        log.error("Получен статус 500 Internal server error {}", ex.getMessage(), ex);
        return new ErrorResponse(ex.toString(), ex.getMessage());
    }
}
