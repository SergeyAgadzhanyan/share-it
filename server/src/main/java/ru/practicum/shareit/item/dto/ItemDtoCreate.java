package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ItemDtoCreate {
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
