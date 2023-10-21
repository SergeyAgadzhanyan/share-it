package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(long ownerId, Pageable pageable);

    @Query("select i from Item as i where i.request.id in :requestIds" +
            " order by i.request.created desc ")
    List<Item> findByRequestIdIn(List<Long> requestIds);

    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(String nameT,
                                                                                           String descriptionT,
                                                                                           Boolean b, Pageable pageable);
}
