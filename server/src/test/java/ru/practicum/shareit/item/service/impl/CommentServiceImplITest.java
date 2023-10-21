package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.InvalidArguments;
import ru.practicum.shareit.item.dto.CommentDtoCreate;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = {"spring.datasource.url=jdbc:h2:mem:shareitTest",
                "spring.datasource.driverClassName=org.h2.Driver"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CommentServiceImplITest {

    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final UserDtoMapper userDtoMapper;
    private final CommentService commentService;
    User user;
    User user2;
    User user3;
    Item item;
    Item item2;
    LocalDateTime start;
    LocalDateTime end;
    Booking booking;
    CommentDtoCreate commentDtoCreate = new CommentDtoCreate("testComment");

    @BeforeEach
    void init() {
        user = new User(1L, "tUserName", "mail@mail.ru");
        user2 = new User(2L, "tUserName2", "mail2@mail.ru");
        user3 = new User(3L, "tUserName3", "mail3@mail.ru");
        item = new Item(1L, "tName", "tDescription", true, user, null);
        item2 = new Item(2L, "tName2", "tDescription2", true, user2, null);
        item2 = new Item(2L, "tName2", "tDescription2", true, user2, null);
        start = LocalDateTime.now().minusMinutes(60);
        end = LocalDateTime.now().minusMinutes(10);
        booking = new Booking(1L, start, end, item, user, Status.WAITING);
        user.setId(userService.addUser(userDtoMapper.mapToDto(user)).getId());
        user2.setId(userService.addUser(userDtoMapper.mapToDto(user2)).getId());
        user3.setId(userService.addUser(userDtoMapper.mapToDto(user3)).getId());
        item.setId(itemService.addItem(user.getId(), new ItemDtoCreate(item.getName(),
                item.getDescription(), item.getAvailable(), null)).getId());
        BookingDto bookingDto = new BookingDto(1L, start, end, item.getId());
        booking.setId(bookingService.addBooking(user2.getId(), bookingDto).getId());
        bookingService.updateStatus(user.getId(), booking.getId(), true);
        CommentDtoCreate commentDtoCreate = new CommentDtoCreate("testComment");
        commentService.addComment(user2.getId(), item.getId(), commentDtoCreate);
    }

    @Test
    void addComment() {
        TypedQuery<Comment> query = em.createQuery("Select c from Comment c", Comment.class);
        List<Comment> comments = query.getResultList();
        assertThat(comments.get(0).getText(), equalTo(commentDtoCreate.getText()));
        assertThrows(InvalidArguments.class, () -> commentService.addComment(user.getId(), item2.getId(), commentDtoCreate));
    }

    @Test
    void getCommentsByItemId() {
        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.item.id = :id", Comment.class);
        List<Comment> comments = query.setParameter("id", item.getId()).getResultList();
        assertThat(comments.get(0).getText(), equalTo(commentDtoCreate.getText()));
        List<Comment> emptyComments = query.setParameter("id", item2.getId()).getResultList();
        assertTrue(emptyComments.isEmpty());
    }
}
