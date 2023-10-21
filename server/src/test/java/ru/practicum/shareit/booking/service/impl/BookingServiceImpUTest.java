package ru.practicum.shareit.booking.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.InvalidArguments;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImpUTest {
    private final BookingMapper bookingMapper = new BookingMapper();
    private final ItemDtoMapper itemDtoMapper = new ItemDtoMapper();
    private final UserDtoMapper userDtoMapper = new UserDtoMapper();
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    User user;
    User user2;
    Item item;
    Item item2;
    LocalDateTime start;
    LocalDateTime end;
    Booking booking;
    BookingDto bookingDto;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void init() {
        bookingService.setBookingMapper(bookingMapper);
        bookingService.setItemDtoMapper(itemDtoMapper);
        bookingService.setUserDtoMapper(userDtoMapper);
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
    void addBooking() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item2));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.save(any()))
                .thenReturn(Optional.of(booking).get());

        BookingDtoGet expectedBookingDtoGet = bookingService.addBooking(user.getId(), bookingDto);
        assertEquals(expectedBookingDtoGet.getId(), bookingDto.getId());
        assertEquals(expectedBookingDtoGet.getItem().getId(), bookingDto.getItemId());
        assertEquals(expectedBookingDtoGet.getStart(), bookingDto.getStart());
        assertEquals(expectedBookingDtoGet.getEnd(), bookingDto.getEnd());

    }

    @Test
    void updateStatus() {

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BookingDtoGet expectedBookingDtoGet = bookingService.updateStatus(user2.getId(),
                booking.getId(), true);
        assertEquals(expectedBookingDtoGet.getId(), booking.getId());
        assertEquals(expectedBookingDtoGet.getStatus(), Status.APPROVED);

        BookingDtoGet expectedBookingDtoGet2 = bookingService.updateStatus(user2.getId(), booking.getId(), false);
        assertEquals(expectedBookingDtoGet2.getId(), booking.getId());
        assertEquals(expectedBookingDtoGet2.getStatus(), Status.REJECTED);

        assertThrows(ResourceNotFoundException.class, () -> bookingService.updateStatus(user.getId(),
                booking.getId(), true));
        assertThrows(InvalidArguments.class, () ->
                bookingService.updateStatus(user2.getId(),
                        booking.getId(), false));
    }

    @Test
    void getBookingById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));


        BookingDtoGet expectedBookingDtoGet = bookingService.getBookingById(user2.getId(), booking.getId());
        assertEquals(expectedBookingDtoGet.getId(), bookingDto.getId());
        assertThrows(ResourceNotFoundException.class, () -> bookingService
                .getBookingById(10L, booking.getId()));

    }

    @Test
    void getBookings() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        bookingService.getBookings(user.getId(), "CURRENT",
                true, 2, 1);
        bookingService.getBookings(user.getId(), "CURRENT",
                false, 2, 1);
        verify(bookingRepository, times(1))
                .findByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any());
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndEndAfterAndStartBeforeOrStart(anyLong(), any(), any(), any(), any());

        bookingService.getBookings(user.getId(), "PAST", true, 2, 1);
        bookingService.getBookings(user.getId(), "PAST", false, 2, 1);
        verify(bookingRepository, times(1))
                .findByBookerIdAndEndBefore(anyLong(), any(), any());
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndEndBefore(anyLong(), any(), any());

        bookingService.getBookings(user.getId(), "FUTURE", true, 2, 1);
        bookingService.getBookings(user.getId(), "FUTURE", false, 2, 1);
        verify(bookingRepository, times(1))
                .findByBookerIdAndStartAfter(anyLong(), any(), any());
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStartAfter(anyLong(), any(), any());

        bookingService.getBookings(user.getId(), "WAITING", false, 2, 1);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStatus(anyLong(), any(), any());

        bookingService.getBookings(user.getId(), "REJECTED", true, 2, 1);
        bookingService.getBookings(user.getId(), "REJECTED", false, 2, 1);
        verify(bookingRepository, times(1))
                .findByBookerIdAndStatus(anyLong(), any(), any());
        verify(bookingRepository, times(2))
                .findAllByItemOwnerIdAndStatus(anyLong(), any(), any());

        bookingService.getBookings(user.getId(), "ALL", true, 2, 1);
        bookingService.getBookings(user.getId(), "ALL", false, 2, 1);
        verify(bookingRepository, times(1))
                .findByBookerId(anyLong(), any());
        verify(bookingRepository, times(1))
                .findAllByItemOwnerId(anyLong(), any());

        assertThrows(InvalidArguments.class, () -> bookingService.getBookings(user.getId(), "Invalid", false, 2, 1));
    }
}

