package ru.practicum.shareit.request.service.impi;

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
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.CommentDtoCreate;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoGet;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithListItem;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
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

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = {"spring.datasource.url=jdbc:h2:mem:shareitTest",
                "spring.datasource.driverClassName=org.h2.Driver"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemRequestServiceImplITest {
    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final UserDtoMapper userDtoMapper;
    private final CommentService commentService;
    private final ItemRequestService itemRequestService;
    User user;
    User user2;
    User user3;
    Item item;
    Item item2;
    LocalDateTime start;
    LocalDateTime end;
    Booking booking;
    CommentDtoCreate commentDtoCreate;
    ItemRequestDto itemRequestDto;
    ItemRequestDtoGet itemRequestDtoGet;


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
        commentDtoCreate = new CommentDtoCreate("testComment");
        commentService.addComment(user2.getId(), item.getId(), commentDtoCreate);
        itemRequestDto = new ItemRequestDto("testDesc");
        itemRequestDtoGet = itemRequestService.addRequest(user.getId(), itemRequestDto);
    }

    @Test
    void addRequest() {
        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir", ItemRequest.class);
        List<ItemRequest> itemRequestList = query.getResultList();
        assertThat(itemRequestList.get(0).getDescription(), equalTo(itemRequestDtoGet.getDescription()));
        assertThat(itemRequestDtoGet.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThrows(ResourceNotFoundException.class, () -> itemRequestService.addRequest(99L, itemRequestDto));
    }

    @Test
    void getRequestsByRequestorId() {
        List<ItemRequestDtoWithListItem> requestDto = itemRequestService.getRequestsByRequestorId(user.getId());
        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir " +
                "where ir.requestor.id = :id", ItemRequest.class);
        List<ItemRequest> itemRequestList = query.setParameter("id", user.getId()).getResultList();
        assertThat(requestDto.get(0).getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestList.get(0).getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThrows(ResourceNotFoundException.class, () -> itemRequestService.getRequestsByRequestorId(99L));
    }

    @Test
    void getAllRequests() {
        List<ItemRequestDtoWithListItem> requestDtoList = itemRequestService
                .getAllRequests(user2.getId(), 0, 1);
        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir", ItemRequest.class);
        List<ItemRequest> itemRequestList = query.getResultList();
        assertThat(itemRequestList.get(0).getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(requestDtoList.get(0).getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThrows(ResourceNotFoundException.class, () -> itemRequestService.getRequestsByRequestorId(99L));

    }

    @Test
    void getRequestById() {
        ItemRequestDtoWithListItem requestDtoWithListItem = itemRequestService.getRequestById(user.getId(),
                itemRequestDtoGet.getId());
        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir" +
                " where ir.id = :id", ItemRequest.class);
        List<ItemRequest> itemRequestList = query.setParameter("id", itemRequestDtoGet.getId()).getResultList();
        assertThat(requestDtoWithListItem.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestList.get(0).getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThrows(ResourceNotFoundException.class, () -> itemRequestService.getRequestsByRequestorId(99L));
    }
}
