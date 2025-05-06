package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    List<Item> findAllByOwnerId(Long ownerId);

    List<Item> searchByNameOrDescription(String text);

    Optional<Item> findById(Long itemId);

    Item update(Item item);
}