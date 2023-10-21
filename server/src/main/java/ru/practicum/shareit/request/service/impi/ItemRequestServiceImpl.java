package ru.practicum.shareit.request.service.impi;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.Messages;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoWithRequestId;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoGet;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithListItem;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Setter
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private ItemRequestMapper itemRequestMapper;
    private ItemDtoMapper itemDtoMapper;


    @Override
    public ItemRequestDtoGet addRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = getUserOrThrowError(userId);
        return itemRequestMapper.mapToDtoGet(itemRequestRepository.save(itemRequestMapper.mapFromDto(itemRequestDto, user)));

    }

    @Override
    public List<ItemRequestDtoWithListItem> getRequestsByRequestorId(Long userId) {
        getUserOrThrowError(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorId(userId, Sort.by("created").descending());
        return getRequestWithItemList(itemRequests);
    }

    @Override
    public List<ItemRequestDtoWithListItem> getAllRequests(Long userId, int from, int size) {
        getUserOrThrowError(userId);
        Pageable pageable = PageRequest.of(from, size, Sort.by("created").descending());
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdNot(userId, pageable);
        return getRequestWithItemList(itemRequests);
    }


    @Override
    public ItemRequestDtoWithListItem getRequestById(Long userId, Long requestId) {
        getUserOrThrowError(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new ResourceNotFoundException(Messages.ITEM_REQUEST_NOT_FOUND.getMessage()));
        return getRequestWithItemList(List.of(itemRequest)).get(0);
    }

    private User getUserOrThrowError(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(Messages.USER_NOT_FOUND.getMessage()));
    }

    private List<ItemRequestDtoWithListItem> getRequestWithItemList(List<ItemRequest> itemRequests) {

        List<Long> ids = itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        Map<Long, List<ItemDtoWithRequestId>> map = new HashMap<>();
        itemRepository.findByRequestIdIn(ids)
                .forEach(i -> {
                    ItemDtoWithRequestId r = itemDtoMapper.mapToItemDtoWithRequestId(i);
                    map.computeIfAbsent(r.getRequestId(), k -> new ArrayList<>()).add(r);
                });

        return itemRequests.stream().map(ir -> itemRequestMapper.mapToDtoWithListItem(ir,
                map.getOrDefault(ir.getId(), List.of()))).collect(Collectors.toList());
    }
}
