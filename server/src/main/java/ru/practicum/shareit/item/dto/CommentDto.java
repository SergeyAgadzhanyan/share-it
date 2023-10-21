package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class CommentDto {
    private long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}

