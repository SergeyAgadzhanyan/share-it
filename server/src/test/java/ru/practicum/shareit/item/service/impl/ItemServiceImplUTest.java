package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoCreate;
import ru.practicum.shareit.item.mapper.CommentDtoMapper;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUTest {
    private final ItemDtoMapper itemDtoMapper = new ItemDtoMapper();
    private final BookingMapper bookingMapper = new BookingMapper();
    private final CommentDtoMapper commentDtoMapper = new CommentDtoMapper();
    User user;
    User user2;
    Item item;
    Item item2;
    LocalDateTime start;
    LocalDateTime end;
    Booking booking;
    BookingDto bookingDto;
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void init() {
        itemService.setItemDtoMapper(itemDtoMapper);
        itemService.setBookingMapper(bookingMapper);
        itemService.setCommentDtoMapper(commentDtoMapper);
        user = new User(1L, "tUserName", "mail@mail.ru");
        user2 = new User(2L, "tUserName2", "mail2@mail.ru");
        item = new Item(1L, "tName", "tDescription", true, user, null);
        item2 = new Item(2L, "tName2", "tDescription2", true, user2, null);
        item2 = new Item(2L, "tName2", "tDescription2", true, user2, null);
        start = LocalDateTime.now().plusMinutes(30);
        end = LocalDateTime.now().plusMinutes(90);
        booking = new Booking(1L, start, end, item2, user, Status.WAITING);
        bookingDto = new BookingDto(1L, start, end, item2.getId());
    }

    @Test
    void getItemById() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findByItemId(Mockito.anyLong(), Mockito.any())).thenReturn(List.of());
        Mockito.when(bookingRepository.findByItemId(Mockito.anyLong(), Mockito.any())).thenReturn(List.of());
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user));
        ItemDto expectedItemDto = itemService.getItemById(item.getId(), user.getId());
        assertEquals(expectedItemDto.getId(), item.getId());
        assertEquals(expectedItemDto.getName(), item.getName());
    }

    @Test
    void addItem() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);
        ItemDtoCreate itemDtoCreate = new ItemDtoCreate(item.getName(), item.getDescription(), item.getAvailable(), null);
        ItemDto expectedItemDto = itemService.addItem(user.getId(), itemDtoCreate);
        assertEquals(expectedItemDto.getId(), item.getId());
        assertEquals(expectedItemDto.getName(), item.getName());
    }

    @Test
    void getOwnerItems() {
        Mockito.when(commentRepository.findByItemOwnerId(Mockito.anyLong(), Mockito.any())).thenReturn(List.of());
        Mockito.when(bookingRepository.findByItemOwnerId(Mockito.anyLong(), Mockito.any())).thenReturn(List.of());
        Mockito.when(itemRepository.findByOwnerId(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(item));
        List<ItemDto> expectedItemDto = itemService.getOwnerItems(user.getId(), 1, 1);
        assertFalse(expectedItemDto.isEmpty());
        assertEquals(expectedItemDto.get(0).getId(), item.getId());

    }

    @Test
    void updateItem() {
        Mockito.when(bookingRepository.findByItemId(Mockito.anyLong(), Mockito.any())).thenReturn(List.of());
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);
        ItemDto itemDto = itemDtoMapper.mapToDto(item, null, null, List.of());
        ItemDtoCreate itemDtoCreate = new ItemDtoCreate(item.getName(), item.getDescription(), item.getAvailable(),
                null);
        itemDtoCreate.setName("newName");
        ItemDto expectedItemDto = itemService.updateItem(item.getId(), user.getId(), itemDtoCreate);
        assertEquals(expectedItemDto.getId(), item.getId());
        assertEquals(expectedItemDto.getName(), "newName");

    }

    @Test
    void searchItems() {
        Mockito.when(itemRepository
                        .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(Mockito.anyString(),
                                Mockito.anyString(), Mockito.anyBoolean(), Mockito.any()))
                .thenReturn(List.of(item));
        List<ItemDto> itemDtoList = itemService.searchItems("some", user.getId(), 1, 1);
        assertEquals(1, itemDtoList.size());
        assertEquals(item.getName(), itemDtoList.get(0).getName());
    }
}
