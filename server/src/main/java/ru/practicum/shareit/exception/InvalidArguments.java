package ru.practicum.shareit.exception;

public class InvalidArguments extends RuntimeException {
    public InvalidArguments() {
    }

    public InvalidArguments(String message) {
        super(message);
    }
}
