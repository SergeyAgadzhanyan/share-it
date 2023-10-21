package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.utils.HeaderUtil;

import javax.validation.constraints.Min;

@Controller
@AllArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(HeaderUtil.USER_HEADER) long userId, @Validated @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader(HeaderUtil.USER_HEADER) long userId) {
        return itemRequestClient.getRequestsByRequestorId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(HeaderUtil.USER_HEADER) long userId,
                                                 @RequestParam(defaultValue = "0") @Min(0) int from,
                                                 @RequestParam(defaultValue = "10") @Min(1) int size) {
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(HeaderUtil.USER_HEADER) long userId, @PathVariable Long requestId) {
        return itemRequestClient.getRequestById(userId, requestId);
    }

}
