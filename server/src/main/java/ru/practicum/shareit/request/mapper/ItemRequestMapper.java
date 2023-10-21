package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDtoWithRequestId;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoGet;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithListItem;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ItemRequestMapper {
    public ItemRequest mapFromDto(ItemRequestDto itemRequestDto, User user) {
        return new ItemRequest(null, itemRequestDto.getDescription(), user, LocalDateTime.now());
    }

    public ItemRequestDtoGet mapToDtoGet(ItemRequest itemRequest) {
        return new ItemRequestDtoGet(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated());
    }

    public ItemRequestDtoWithListItem mapToDtoWithListItem(ItemRequest itemRequest, List<ItemDtoWithRequestId> list) {
        return new ItemRequestDtoWithListItem(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated(), list);
    }
}
