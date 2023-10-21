package ru.practicum.shareit.item.mapper;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public class ItemDtoMapper {

    public ItemDto mapToDto(Item model, BookingDtoWithBookerId lastBooking, BookingDtoWithBookerId nextBooking, List<CommentDto> comments) {
        return new ItemDto(model.getId(), model.getName(), model.getDescription(),
                model.getAvailable(), lastBooking, nextBooking, comments, model.getRequest() == null ? null : model.getRequest().getId());
    }

    public Item mapFromDtoCreate(ItemDtoCreate model, User owner, ItemRequest itemRequest) {
        return new Item(null, model.getName(), model.getDescription(), model.getAvailable(), owner, itemRequest);
    }

    public ItemDtoWithRequestId mapToItemDtoWithRequestId(Item i) {
        return new ItemDtoWithRequestId(i.getId(), i.getName(), i.getDescription(), i.getRequest().getId(), i.getAvailable());
    }

    public ItemDtoGet mapToItemDtoGet(Item item) {
        return new ItemDtoGet(item.getId(), item.getName());
    }
}

