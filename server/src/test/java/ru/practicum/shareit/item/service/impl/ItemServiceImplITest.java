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
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.model.Item;
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
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = {"spring.datasource.url=jdbc:h2:mem:shareitTest",
                "spring.datasource.driverClassName=org.h2.Driver"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemServiceImplITest {
    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final UserDtoMapper userDtoMapper;

    User user;
    User user2;
    User user3;
    Item item;
    Item item2;
    LocalDateTime start;
    LocalDateTime end;
    Booking booking;


    @BeforeEach
    void init() {
        user = new User(1L, "tUserName", "mail@mail.ru");
        user2 = new User(2L, "tUserName2", "mail2@mail.ru");
        user3 = new User(3L, "tUserName3", "mail3@mail.ru");
        item = new Item(1L, "tName", "tDescription", true, user, null);
        item2 = new Item(2L, "tName2", "tDescription2", true, user2, null);
        item2 = new Item(2L, "tName2", "tDescription2", true, user2, null);
        start = LocalDateTime.now().plusMinutes(30);
        end = LocalDateTime.now().plusMinutes(90);
        booking = new Booking(1L, start, end, item, user, Status.WAITING);
        user.setId(userService.addUser(userDtoMapper.mapToDto(user)).getId());
        user2.setId(userService.addUser(userDtoMapper.mapToDto(user2)).getId());
        user3.setId(userService.addUser(userDtoMapper.mapToDto(user3)).getId());
        item.setId(itemService.addItem(user.getId(), new ItemDtoCreate(item.getName(),
                item.getDescription(), item.getAvailable(), null)).getId());
        BookingDto bookingDto = new BookingDto(1L, start, end, item.getId());
        booking.setId(bookingService.addBooking(user2.getId(), bookingDto).getId());
    }

    @Test
    void getItemById() {
        ItemDto itemFromService = itemService.getItemById(item.getId(), user.getId());
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item expectedItem = query.setParameter("id", item.getId()).getSingleResult();
        assertThat(expectedItem.getDescription(), equalTo(itemFromService.getDescription()));
        assertThat(expectedItem.getName(), equalTo(itemFromService.getName()));
        assertThrows(ResourceNotFoundException.class, () -> itemService.getItemById(item.getId(), 99L));
        assertThrows(ResourceNotFoundException.class, () -> itemService.getItemById(99L, user.getId()));
    }

    @Test
    void addItem() {
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item expectedItem = query.setParameter("id", item.getId()).getSingleResult();
        assertThat(expectedItem.getDescription(), equalTo(item.getDescription()));
        assertThrows(ResourceNotFoundException.class, () -> itemService.getItemById(item.getId(), 99L));
    }

    @Test
    void getOwnerItems() {
        List<ItemDto> ownerItemsFromService = itemService.getOwnerItems(user.getId(), 0, 1);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.owner.id= :id", Item.class);
        List<Item> expectedItem = query.setParameter("id", user.getId()).getResultList();
        assertThat(ownerItemsFromService.get(0).getName(), equalTo(expectedItem.get(0).getName()));
    }

    @Test
    void updateItem() {
        ItemDto itemDto = itemService.getItemById(item.getId(), user.getId());
        ItemDtoCreate itemDtoCreate = new ItemDtoCreate(item.getName(), item.getDescription(), item.getAvailable(),
                null);
        itemDtoCreate.setName("newName");
        itemService.updateItem(item.getId(), user.getId(), itemDtoCreate);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id= :id", Item.class);
        Item expectedItem = query.setParameter("id", item.getId()).getSingleResult();
        assertThat(expectedItem.getName(), equalTo("newName"));
        assertThrows(ResourceNotFoundException.class, () ->
                itemService.updateItem(item.getId(), null, itemDtoCreate));
        assertThrows(ResourceNotFoundException.class, () ->
                itemService.updateItem(item.getId(), user2.getId(), itemDtoCreate));
    }

    @Test
    void searchItems() {
        List<ItemDto> items = itemService.searchItems(item.getName(), user.getId(), 0, 1);
        assertNotEquals(items.size(), 0);
        assertEquals(0, itemService.searchItems("wrong", user.getId(), 0, 1).size());
    }
}
