package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;

import java.util.List;

public interface BookingService {

    BookingDtoGet addBooking(long bookerId, BookingDto bookingDto);

    BookingDtoGet updateStatus(long userId, long bookingId, boolean approved);

    BookingDtoGet getBookingById(long userId, long bookingId);

    List<BookingDtoGet> getBookings(Long userId, String state, boolean areFindById, int from, int size);

}
