package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.dto.Create;
import ru.practicum.shareit.user.dto.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
@Setter
public class ItemDtoCreate {
    @NotBlank(groups = {Create.class})
    @Size(max = 20, groups = {Create.class, Update.class})
    private String name;
    @NotBlank(groups = {Create.class})
    @Size(max = 500, groups = {Create.class, Update.class})
    private String description;
    @NotNull(groups = {Create.class})
    private Boolean available;
    private Long requestId;
}
