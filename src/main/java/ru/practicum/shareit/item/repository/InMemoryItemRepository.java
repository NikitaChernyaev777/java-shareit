package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> itemsById = new HashMap<>();
    private final AtomicLong newItemId = new AtomicLong(0);

    @Override
    public Item save(Item item) {
        item.setId(generateItemId());
        itemsById.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> findAllByOwnerId(Long ownerId) {
        return itemsById.values().stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchByNameOrDescription(String text) {
        String searchText = text.toLowerCase();
        return itemsById.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(searchText)
                        || item.getDescription().toLowerCase().contains(searchText))
                        && item.getAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        return Optional.ofNullable(itemsById.get(itemId));
    }

    @Override
    public Item update(Item item) {
        Long itemId = item.getId();
        if (!itemsById.containsKey(itemId)) {
            return null;
        }
        itemsById.put(itemId, item);
        return item;
    }

    private Long generateItemId() {
        return newItemId.incrementAndGet();
    }
}