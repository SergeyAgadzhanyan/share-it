package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class CommentDtoCreate {
    @NotBlank
    @Size(max = 500)
    private String text;
}
