package ru.practicum.shareit.item.service.impl;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.InvalidArguments;
import ru.practicum.shareit.exception.Messages;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoCreate;
import ru.practicum.shareit.item.mapper.CommentDtoMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Setter
public class CommentServiceImpl implements CommentService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private CommentDtoMapper commentDtoMapper;

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDtoCreate commentDtoCreate) {
        checkBookingByBookerIdAndItemId(userId, itemId);
        Item item = (Item) findByIdOrThrowError(itemId, itemRepository);
        User user = (User) findByIdOrThrowError(userId, userRepository);
        Comment comment = new Comment(null, commentDtoCreate.getText(), item, user, LocalDateTime.now());
        return commentDtoMapper.mapToDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository.findByItemId(itemId, Sort.by("created")).stream().map(commentDtoMapper::mapToDto).collect(Collectors.toList());
    }

    private Object findByIdOrThrowError(Long id, JpaRepository repository) {
        Optional<Object> o = repository.findById(id);
        if (o.isEmpty()) throw new ResourceNotFoundException(Messages.RESOURCE_NOT_FOUND.getMessage());
        return o.get();
    }

    private void checkBookingByBookerIdAndItemId(long bookerId, long itemId) {
        if (bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(bookerId, itemId, Status.APPROVED,
                LocalDateTime.now()).isEmpty())
            throw new InvalidArguments(Messages.COMMENT_WITHOUT_BOOKING.getMessage());
    }
}
