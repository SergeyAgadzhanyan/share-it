package ru.practicum.shareit.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private final String error;
    private final String description;

}
