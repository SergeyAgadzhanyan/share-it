package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.exception.InvalidArguments;
import ru.practicum.shareit.utils.HeaderUtil;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(HeaderUtil.USER_HEADER) long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String stringState,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        State state = getStateFromString(stringState);
        log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByItemOwner(@RequestHeader(HeaderUtil.USER_HEADER) Long userId,
                                                         @RequestParam(name = "state", defaultValue = "ALL") String stringState,
                                                         @RequestParam(defaultValue = "0") @Min(0) int from,
                                                         @RequestParam(defaultValue = "10") @Min(1) int size) {
        State state = getStateFromString(stringState);
        return bookingClient.getBookingsByOwner(userId, state, from, size);
    }


    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(HeaderUtil.USER_HEADER) Long userId,
                                           @RequestBody @Valid BookingDto bookingDto) {
        log.info("Creating booking {}, userId={}", bookingDto, userId);
        return bookingClient.bookItem(userId, bookingDto);
    }


    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(HeaderUtil.USER_HEADER) Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }


    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(@RequestHeader(HeaderUtil.USER_HEADER) Long userId,
                                               @PathVariable long bookingId, @RequestParam boolean approved) {
        log.info("Patch booking {}, userId={}", bookingId, userId);
        return bookingClient.updateStatus(userId, bookingId, approved);
    }

    private State getStateFromString(String stringState) {
        State state;
        try {
            state = State.valueOf(stringState);
        } catch (IllegalArgumentException e) {
            throw new InvalidArguments("Unknown state: " + stringState);
        }
        return state;
    }


}
