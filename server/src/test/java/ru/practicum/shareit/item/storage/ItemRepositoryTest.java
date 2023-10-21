package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {
    private final TestEntityManager entityManager;
    private final ItemRepository itemRepository;

    @Test
    void findByRequestIdIn() {
        User user = new User(null, "tUserName", "mail@mail.ru");
        User user2 = new User(null, "tUserName2", "mail2@mail.ru");
        entityManager.persist(user);
        entityManager.persist(user2);
        ItemRequest itemRequest = new ItemRequest(null, "d", user2, LocalDateTime.now().minusMinutes(30));
        entityManager.persist(itemRequest);
        Item item = new Item(null, "tName", "tDescription", true, user, itemRequest);
        entityManager.persist(item);
        List<Item> items = itemRepository.findByRequestIdIn(List.of(itemRequest.getId()));
        assertFalse(items.isEmpty());
        assertEquals(item.getDescription(), items.get(0).getDescription());
    }
}
