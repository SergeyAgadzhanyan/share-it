package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoCreate;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.user.dto.Create;
import ru.practicum.shareit.user.dto.Update;
import ru.practicum.shareit.utils.HeaderUtil;

import javax.validation.constraints.Min;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;


    @GetMapping
    public ResponseEntity<Object> getOwnerItems(@RequestHeader(HeaderUtil.USER_HEADER) long ownerId,
                                                @RequestParam(defaultValue = "0") @Min(0) int from,
                                                @RequestParam(defaultValue = "10") @Min(1) int size) {
        return itemClient.getOwnerItems(ownerId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable long itemId, @RequestHeader(HeaderUtil.USER_HEADER) long userId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text,
                                             @RequestHeader(HeaderUtil.USER_HEADER) long userId,
                                             @RequestParam(defaultValue = "0") @Min(0) int from,
                                             @RequestParam(defaultValue = "10") @Min(1) int size) {
        return text.isBlank() ? ResponseEntity.ok(List.of()) :
                itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping()
    public ResponseEntity<Object> addItem(@RequestHeader(HeaderUtil.USER_HEADER) long userId,
                                          @Validated(Create.class) @RequestBody ItemDtoCreate itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable long itemId, @RequestHeader(HeaderUtil.USER_HEADER)
    long ownerId, @Validated(Update.class) @RequestBody ItemDtoCreate itemDtoCreate) {
        return itemClient.updateItem(ownerId, itemId, itemDtoCreate);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable long itemId, @RequestHeader(HeaderUtil.USER_HEADER) long userId,
                                             @Validated @RequestBody CommentDtoCreate commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
