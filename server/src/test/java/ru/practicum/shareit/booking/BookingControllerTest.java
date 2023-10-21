package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDtoWithRequestId;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
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


@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    private final ObjectMapper mapper;
    private final MockMvc mvc;
    private final BookingMapper bookingMapper = new BookingMapper();
    private final ItemDtoMapper itemDtoMapper = new ItemDtoMapper();
    private final UserDtoMapper userDtoMapper = new UserDtoMapper();
    @MockBean
    BookingService bookingService;
    private BookingDto bookingDto;
    private BookingDtoGet bookingDtoGet;


    @BeforeEach
    void init() {
        User user = new User(1L, "tUserName", "mail@mail.ru");
        Item item = new Item(1L, "tName", "tDescription", true, user, null);
        LocalDateTime start = LocalDateTime.now().plusMinutes(30);
        Item itemR = new Item(1L, "tName", "tDescription", true, user, new ItemRequest(1L, "d", user, start));
        LocalDateTime end = LocalDateTime.now().plusMinutes(90);
        Booking booking = new Booking(1L, start, end, item, user, Status.WAITING);
        BookingDtoWithBookerId bookingDtoWithBookerId = bookingMapper.mapToBookingDtoWithBookerId(booking);
        ItemDtoWithRequestId itemDtoWithRequestId = itemDtoMapper.mapToItemDtoWithRequestId(itemR);
        bookingDto = bookingMapper.mapToDto(booking);
        bookingDtoGet = bookingMapper.mapToBookingDtoGet(booking, itemDtoMapper.mapToItemDtoGet(item), userDtoMapper.mapToUserDtoGet(user));
    }

    @Test
    void addBooking() throws Exception {

        when(bookingService.addBooking(anyLong(), any())).thenReturn(bookingDtoGet);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoGet.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoGet.getItem().getName())));

        bookingDto.setItemId(null);


    }

    @Test
    void updateStatus() throws Exception {
        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoGet);
        mvc.perform(patch("/bookings/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoGet.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoGet.getItem().getName())));

        mvc.perform(patch("/bookings/s?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingDtoGet);
        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoGet.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoGet.getItem().getName())));

        mvc.perform(get("/bookings/s")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsByUserId() throws Exception {
        when(bookingService.getBookings(anyLong(), anyString(), anyBoolean(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoGet));
        mvc.perform(get("/bookings?state=All&from=0&size=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDtoGet.getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingDtoGet.getItem().getName())));

    }

    @Test
    void getBookingsByItemOwner() throws Exception {
        when(bookingService.getBookings(anyLong(), anyString(), anyBoolean(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoGet));
        mvc.perform(get("/bookings/owner?state=All&from=0&size=1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDtoGet.getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingDtoGet.getItem().getName())));

    }
}
