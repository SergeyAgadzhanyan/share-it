package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.annotation.BookDate;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@BookDate
public class BookingDto {
    private long id;
    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
    @NotNull
    private Long itemId;
}
