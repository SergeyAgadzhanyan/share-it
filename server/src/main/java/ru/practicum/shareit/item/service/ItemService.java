package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreate;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(long id, long userId);

    ItemDto addItem(long ownerId, ItemDtoCreate itemDto);

    List<ItemDto> getOwnerItems(long ownerId, int from, int size);

    ItemDto updateItem(long id, Long ownerId, ItemDtoCreate itemDto);

    List<ItemDto> searchItems(String text, long userId, int from, int size);
}
