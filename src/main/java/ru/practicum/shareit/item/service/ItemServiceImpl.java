package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.request.NewItemRequestDto;
import ru.practicum.shareit.item.dto.request.UpdateItemRequestDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Override
    public ItemResponseDto create(Long userId, NewItemRequestDto newItemDto) {
        log.info("Создание новой вещи c name={} пользователем с userId={}", newItemDto.getName(), userId);

        User owner = userService.getExistingUser(userId);
        Item item = itemMapper.toNewItem(newItemDto);
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);

        log.info("Вещь успешно создана: id={}, name={}, ownerId={}", savedItem.getId(), savedItem.getName(), userId);
        return itemMapper.toItemResponseDto(savedItem);
    }

    @Override
    public List<ItemResponseDto> findAllByOwnerId(Long ownerId) {
        log.info("Запрошен список всех вещей владельца с ownerId={}", ownerId);
        userService.getExistingUser(ownerId);
        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(itemMapper::toItemResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> searchByNameOrDescription(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        log.info("Запрошен список вещей по соответствию тексту: '{}'", text);
        return itemRepository.searchByNameOrDescription(text).stream()
                .map(itemMapper::toItemResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto findById(Long itemId) {
        log.info("Запрошена вещь с id={}", itemId);
        return itemMapper.toItemResponseDto(getExistingItem(itemId));
    }

    @Override
    public ItemResponseDto update(Long userId, Long itemId, UpdateItemRequestDto updateItemDto) {
        log.info("Обновление существующей вещи c id={} пользователем с userId={}", itemId, userId);

        userService.getExistingUser(userId);
        Item existingItem = getExistingItem(itemId);

        if (!existingItem.getOwner().getId().equals(userId)) {
            log.warn("Попытка обновления вещи с id={}, которая не принадлежит userId={}", itemId, userId);
            throw new ValidationException(String.format("Вещь с id=%d не принадлежит данному пользователю",
                    existingItem.getId()));

        }

        itemMapper.updateItem(updateItemDto, existingItem);
        itemRepository.update(existingItem);

        log.info("Вещь с id={} успешно обновлена", existingItem.getId());
        return itemMapper.toItemResponseDto(existingItem);
    }

    @Override
    public Item getExistingItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id=%d не найдена", itemId)));
    }
}