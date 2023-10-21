package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoWithBookerId lastBooking;
    private BookingDtoWithBookerId nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
}
