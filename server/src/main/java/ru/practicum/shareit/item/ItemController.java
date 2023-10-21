package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @GetMapping
    public List<ItemDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                       @RequestParam int from,
                                       @RequestParam int size) {
        return itemService.getOwnerItems(ownerId, from / size, size);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestParam int from,
                                    @RequestParam int size) {
        return itemService.searchItems(text, userId, from / size, size);
    }

    @PostMapping()
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDtoCreate itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id")
    long ownerId, @RequestBody ItemDtoCreate itemDtoCreate) {
        return itemService.updateItem(itemId, ownerId, itemDtoCreate);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody CommentDtoCreate commentDto) {
        return commentService.addComment(userId, itemId, commentDto);
    }
}
