package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoGet;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithListItem;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDtoGet addRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoWithListItem> getRequestsByRequestorId(Long userId);

    List<ItemRequestDtoWithListItem> getAllRequests(Long userId, int from, int size);

    ItemRequestDtoWithListItem getRequestById(Long userId, Long requestId);
}
