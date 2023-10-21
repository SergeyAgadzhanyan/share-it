package ru.practicum.shareit.exception;

public class NotUniqueEmail extends RuntimeException {
    public NotUniqueEmail() {
    }

    public NotUniqueEmail(String message) {
        super(message);
    }
}
