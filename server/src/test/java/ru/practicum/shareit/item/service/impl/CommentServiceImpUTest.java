package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoCreate;
import ru.practicum.shareit.item.mapper.CommentDtoMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;

@ExtendWith(MockitoExtension.class)
class CommentServiceImpUTest {
    private final CommentDtoMapper commentDtoMapper = new CommentDtoMapper();
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CommentRepository commentRepository;
    @InjectMocks
    CommentServiceImpl commentService;
    User user;
    User user2;
    Item item;
    Item item2;
    LocalDateTime start;
    LocalDateTime end;
    Comment comment;


    @BeforeEach
    void init() {
        commentService.setCommentDtoMapper(commentDtoMapper);
        user = new User(1L, "tUserName", "mail@mail.ru");
        user2 = new User(2L, "tUserName2", "mail2@mail.ru");
        item = new Item(1L, "tName", "tDescription", true, user, null);
        item2 = new Item(2L, "tName2", "tDescription2", true, user2, null);
        item2 = new Item(2L, "tName2", "tDescription2", true, user2, null);
        start = LocalDateTime.now().plusMinutes(30);
        end = LocalDateTime.now().plusMinutes(90);
        comment = new Comment(1L, "text", item, user, start);
    }

    @Test
    void addComment() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item));
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        Mockito.when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of(new Booking()));
        Mockito.when(commentRepository.save(any())).thenReturn(comment);
        CommentDtoCreate commentDtoCreate = new CommentDtoCreate("text");
        CommentDto commentDto = commentService.addComment(user.getId(), item.getId(), commentDtoCreate);
        assertEquals(commentDto.getText(), comment.getText());
    }

    @Test
    void getCommentsByItemId() {
        Mockito.when(commentRepository.findByItemId(anyLong(), any())).thenReturn(List.of(comment));
        List<CommentDto> commentDtos = commentService.getCommentsByItemId(1L);
        assertEquals(1, commentDtos.size());
    }
}
