package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoCreate;

import java.util.List;

public interface CommentService {
    CommentDto addComment(Long userId, Long itemId, CommentDtoCreate commentDto);

    List<CommentDto> getCommentsByItemId(Long itemId);
}
