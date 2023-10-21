package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoGet addBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                    @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoGet updateStatus(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                      @PathVariable long bookingId, @RequestParam boolean approved) {
        return bookingService.updateStatus(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoGet getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoGet> getBookingsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam String state,
                                                   @RequestParam int from,
                                                   @RequestParam int size) {

        return bookingService.getBookings(userId, state, true, from / size, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoGet> getBookingsByItemOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam String state,
                                                      @RequestParam int from,
                                                      @RequestParam int size) {
        return bookingService.getBookings(userId, state, false, from / size, size);
    }

}
