package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.InvalidArguments;
import ru.practicum.shareit.exception.ResourceNotFoundException;
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
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = {"spring.datasource.url=jdbc:h2:mem:shareitTest",
                "spring.datasource.driverClassName=org.h2.Driver"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BookingServiceImplITest {

    private final EntityManager em;
    private final BookingServiceImpl bookingService;
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;
    private final ItemService itemService;
    User user;
    User user2;
    User user3;
    Item item;
    Item item2;
    LocalDateTime start;
    LocalDateTime end;
    Booking booking;
    BookingDto testBookingDto;

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
        testBookingDto = new BookingDto(1L, start, end, item.getId());
    }

    @Test
    void addBooking() {
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking expectedBooking = query.setParameter("id", booking.getId()).getSingleResult();
        assertThat(expectedBooking.getId(), notNullValue());
        assertThat(expectedBooking.getItem().getDescription(), equalTo(item.getDescription()));
        assertThrows(ResourceNotFoundException.class, () -> {
            BookingDto bookingDto = new BookingDto(1L, start, end, item.getId());
            bookingService.addBooking(99L, bookingDto);
        });
    }

    @Test
    void updateStatus() {
        bookingService.updateStatus(user.getId(), booking.getId(), true);
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking expectedBooking = query.setParameter("id", booking.getId()).getSingleResult();

        assertThat(expectedBooking.getId(), notNullValue());
        assertThat(expectedBooking.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void getBookingById() {
        BookingDtoGet bookingDtoGetFromService = bookingService.getBookingById(user.getId(), booking.getId());
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking expectedBooking = query.setParameter("id", booking.getId()).getSingleResult();

        assertThat(expectedBooking.getId(), notNullValue());
        assertThat(expectedBooking.getBooker().getId(), equalTo(user2.getId()));
        assertThat(booking.getItem().getName(), equalTo(expectedBooking.getItem().getName()));
        assertThat(booking.getItem().getName(), equalTo(bookingDtoGetFromService.getItem().getName()));
        assertThrows(ResourceNotFoundException.class, () ->
                bookingService.getBookingById(999L, booking.getId()));
        assertThrows(ResourceNotFoundException.class, () ->
                bookingService.getBookingById(user.getId(), 999L));
        assertThrows(ResourceNotFoundException.class, () ->
                bookingService.getBookingById(user3.getId(), booking.getId()));

    }

    @Test
    void getBookings() {
        List<BookingDtoGet> bookingDtoGetList = bookingService.getBookings(user2.getId(), "WAITING", true, 0, 1);
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b " +
                "where b.booker.id = :id " +
                "AND b.status = :status", Booking.class);
        List<Booking> expectedBookings = query.setParameter("id", user2.getId())
                .setParameter("status", Status.WAITING).getResultList();
        assertThat(expectedBookings.get(0).getId(), notNullValue());
        assertThat(expectedBookings.get(0).getBooker().getName(), equalTo(user2.getName()));
        assertThat(bookingDtoGetList.get(0).getBooker().getId(), equalTo(user2.getId()));
        bookingService.updateStatus(user.getId(), booking.getId(), false);
        List<BookingDtoGet> bookingDtoGetsRejected = bookingService.getBookings(user2.getId(),
                "REJECTED", true, 0, 1);
        assertThat(bookingDtoGetsRejected.get(0).getStatus(), equalTo(Status.REJECTED));
        assertThrows(InvalidArguments.class, () -> bookingService.getBookings(user2.getId(), "wrong", true, 0, 1));

    }

    @Test
    void getBookingsCurrent() {
        LocalDateTime now = LocalDateTime.now();
        testBookingDto.setStart(now.minusMinutes(30));
        testBookingDto.setEnd(now.plusMinutes(30));

        bookingService.addBooking(user2.getId(), testBookingDto);
        List<BookingDtoGet> bookingDtoGetsCurrent = bookingService.getBookings(user2.getId(),
                State.CURRENT.toString(), true, 0, 1);
        assertThat(bookingDtoGetsCurrent.get(0).getStart(), equalTo(now.minusMinutes(30)));
    }

    @Test
    void getBookingsPast() {
        LocalDateTime now = LocalDateTime.now();
        testBookingDto.setStart(now.minusMinutes(90));
        testBookingDto.setEnd(now.minusMinutes(60));

        bookingService.addBooking(user2.getId(), testBookingDto);
        List<BookingDtoGet> bookingDtoGetsPast = bookingService.getBookings(user2.getId(), State.PAST.toString(), true, 0, 1);
        assertThat(bookingDtoGetsPast.get(0).getEnd(), equalTo(now.minusMinutes(60)));
    }

    @Test
    void getBookingsFuture() {
        LocalDateTime now = LocalDateTime.now();
        testBookingDto.setStart(now.plusMinutes(30));
        testBookingDto.setEnd(now.plusMinutes(90));

        bookingService.addBooking(user2.getId(), testBookingDto);
        List<BookingDtoGet> bookingDtoGetsFuture = bookingService.getBookings(user2.getId(),
                State.FUTURE.toString(), true, 0, 1);
        assertThat(bookingDtoGetsFuture.get(0).getStart(), equalTo(now.plusMinutes(30)));
    }

}
