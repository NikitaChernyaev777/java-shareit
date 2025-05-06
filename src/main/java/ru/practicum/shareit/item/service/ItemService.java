package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.request.NewItemRequestDto;
import ru.practicum.shareit.item.dto.request.UpdateItemRequestDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemResponseDto create(Long userId, NewItemRequestDto newItemDto);

    List<ItemResponseDto> findAllByOwnerId(Long ownerId);

    List<ItemResponseDto> searchByNameOrDescription(String text);

    ItemResponseDto findById(Long itemId);

    ItemResponseDto update(Long userId, Long itemId, UpdateItemRequestDto updateItemDto);

    Item getExistingItem(Long itemId);
}