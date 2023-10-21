package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mvc;
    @MockBean
    private ItemService itemService;
    @MockBean
    private CommentService commentService;
    private Item item;
    private ItemDto itemDto;
    private ItemDtoCreate itemDtoCreate;
    private CommentDto commentDto;
    private CommentDtoCreate commentDtoCreate;
    private LocalDateTime start;

    @BeforeEach
    void init() {
        final ItemDtoMapper itemDtoMapper = new ItemDtoMapper();
        item = new Item(1L, "tName", "tDescription", true, new User(1L, "tUserName", "mail@mail.ru"), null);
        itemDto = itemDtoMapper.mapToDto(item, null, null, List.of());
        itemDtoCreate = new ItemDtoCreate(item.getName(), item.getDescription(), item.getAvailable(), null);
        commentDto = new CommentDto(1L, "c", "a", start);
        commentDtoCreate = new CommentDtoCreate(commentDto.getText());
        start = LocalDateTime.now().plusMinutes(30);

    }

    @Test
    void getOwnerItems() throws Exception {
        when(itemService.getOwnerItems(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mvc.perform(get("/items?from=0&size=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(item.getName())));

    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDto);

        mvc.perform(get("/items/" + item.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())));

        mvc.perform(get("/items/s")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchItem() throws Exception {
        when(itemService.searchItems(anyString(), anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=text&from=0&size=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(item.getName())));

    }

    @Test
    void addItem() throws Exception {
        when(itemService.addItem(anyLong(), any())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())));

        itemDtoCreate.setName("someLongNameSomeLongName");

    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())));

        mvc.perform(patch("/items/s")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment() throws Exception {
        when(commentService.addComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));

        mvc.perform(post("/items/s/comment")
                        .content(mapper.writeValueAsString(commentDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
