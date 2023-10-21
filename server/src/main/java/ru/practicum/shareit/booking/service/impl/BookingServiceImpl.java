package ru.practicum.shareit.booking.service.impl;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.InvalidArguments;
import ru.practicum.shareit.exception.Messages;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Setter
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private BookingMapper bookingMapper;
    private ItemDtoMapper itemDtoMapper;
    private UserDtoMapper userDtoMapper;

    @Override
    public BookingDtoGet addBooking(long bookerId, BookingDto bookingDto) {

        Item item = (Item) findByIdOrThrowError(bookingDto.getItemId(), itemRepository);
        if (item.getOwner().getId() == bookerId)
            throw new ResourceNotFoundException(Messages.ITEM_NOT_FOUND.getMessage());
        if (!item.getAvailable()) throw new InvalidArguments(Messages.INVALID_ARGUMENTS.getMessage());
        User user = (User) findByIdOrThrowError(bookerId, userRepository);
        Booking booking = bookingRepository.save(bookingMapper.mapFromDto(user, item, bookingDto));
        return bookingMapper.mapToBookingDtoGet(booking, itemDtoMapper.mapToItemDtoGet(item), userDtoMapper.mapToUserDtoGet(user));

    }

    @Override
    public BookingDtoGet updateStatus(long userId, long bookingId, boolean approved) {
        Booking booking = (Booking) findByIdOrThrowError(bookingId, bookingRepository);
        if (booking.getItem().getOwner().getId() != userId)
            throw new ResourceNotFoundException(Messages.NOT_ITEM_OWNER.getMessage());
        if ((booking.getStatus() == Status.APPROVED && approved) || (booking.getStatus() == Status.REJECTED && !approved))
            throw new InvalidArguments(Messages.BOOKING_STATUS_ALREADY_UPDATED.getMessage());
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        bookingRepository.save(booking);
        return bookingMapper.mapToBookingDtoGet(booking, itemDtoMapper.mapToItemDtoGet(booking.getItem()),
                userDtoMapper.mapToUserDtoGet(booking.getBooker()));
    }

    @Override
    public BookingDtoGet getBookingById(long userId, long bookingId) {
        findByIdOrThrowError(userId, userRepository);
        Booking booking = (Booking) findByIdOrThrowError(bookingId, bookingRepository);
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId)
            throw new ResourceNotFoundException(Messages.RESOURCE_NOT_FOUND.getMessage());
        return bookingMapper.mapToBookingDtoGet(booking, itemDtoMapper.mapToItemDtoGet(booking.getItem()),
                userDtoMapper.mapToUserDtoGet(booking.getBooker()));
    }

    @Override
    public List<BookingDtoGet> getBookings(Long userId, String stringState, boolean areFindById, int from, int size) {
        Pageable pageableWithSort = PageRequest.of(from, size, Sort.by("start").descending());
        findByIdOrThrowError(userId, userRepository);
        List<Booking> bookings;
        State state;
        try {
            state = State.valueOf(stringState);
        } catch (IllegalArgumentException e) {
            throw new InvalidArguments("Unknown state: " + stringState);
        }
        LocalDateTime dateTime = LocalDateTime.now();
        switch (state) {
            case CURRENT:
                bookings = areFindById ?
                        bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(userId, dateTime,
                                dateTime, pageableWithSort)
                        : bookingRepository.findAllByItemOwnerIdAndEndAfterAndStartBeforeOrStart(userId, dateTime,
                        dateTime, dateTime, pageableWithSort);
                break;
            case PAST:
                bookings = areFindById ?
                        bookingRepository.findByBookerIdAndEndBefore(userId, dateTime, pageableWithSort)
                        : bookingRepository.findAllByItemOwnerIdAndEndBefore(userId, dateTime, pageableWithSort);

                break;
            case FUTURE:
                bookings = areFindById ?
                        bookingRepository.findByBookerIdAndStartAfter(userId, dateTime, pageableWithSort)
                        : bookingRepository.findAllByItemOwnerIdAndStartAfter(userId, dateTime, pageableWithSort);
                break;
            case WAITING:
                bookings = areFindById ?
                        bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING, pageableWithSort)
                        : bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.WAITING, pageableWithSort);
                break;
            case REJECTED:
                bookings = areFindById ?
                        bookingRepository.findByBookerIdAndStatus(userId, Status.REJECTED, pageableWithSort)
                        : bookingRepository.findAllByItemOwnerIdAndStatus(userId, Status.REJECTED, pageableWithSort);
                break;
            default:
                bookings = areFindById ?
                        bookingRepository.findByBookerId(userId, pageableWithSort)
                        : bookingRepository.findAllByItemOwnerId(userId, pageableWithSort);
        }
        return bookings.stream().map(b -> bookingMapper.mapToBookingDtoGet(b, itemDtoMapper.mapToItemDtoGet(b.getItem()), userDtoMapper.mapToUserDtoGet(b.getBooker()))).collect(Collectors.toList());
    }

    private Object findByIdOrThrowError(Long id, JpaRepository repository) {
        Optional<Object> o = repository.findById(id);
        if (o.isEmpty()) throw new ResourceNotFoundException(Messages.RESOURCE_NOT_FOUND.getMessage());
        return o.get();
    }

}
