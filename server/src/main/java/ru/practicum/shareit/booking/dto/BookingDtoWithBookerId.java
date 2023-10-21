package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BookingDtoWithBookerId {
    private long id;
    private long bookerId;
}
