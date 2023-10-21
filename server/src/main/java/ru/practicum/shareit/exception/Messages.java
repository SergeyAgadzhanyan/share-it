package ru.practicum.shareit.exception;

import org.springframework.util.StringUtils;

public enum Messages {
    USER_NOT_FOUND,
    NOT_UNIQUE_EMAIL,
    ITEM_NOT_FOUND,
    VALIDATION_EXCEPTION,
    RESOURCE_NOT_FOUND,
    WRONG_DATE,
    INVALID_ARGUMENTS,
    NOT_ITEM_OWNER,
    UNSUPPORTED_STATUS,
    BOOKING_STATUS_ALREADY_UPDATED,
    COMMENT_WITHOUT_BOOKING,
    ITEM_REQUEST_NOT_FOUND;

    public String getMessage() {
        return StringUtils.capitalize(this.name()
                .toLowerCase().replaceAll("_", " "));
    }
}
