package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.request.NewCommentRequestDto;
import ru.practicum.shareit.item.dto.request.NewItemRequestDto;
import ru.practicum.shareit.item.dto.request.UpdateItemRequestDto;
import ru.practicum.shareit.item.dto.response.CommentResponseDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemResponseDto createItem(Long userId, NewItemRequestDto newItemDto);

    List<ItemResponseDto> getItemsByOwnerId(Long ownerId);

    ItemResponseDto getItemById(Long userId, Long itemId);

    List<ItemResponseDto> search(Long userId, String text);

    ItemResponseDto updateItem(Long userId, Long itemId, UpdateItemRequestDto updateItemDto);

    Item getExistingItem(Long itemId);

    CommentResponseDto createComment(Long userId, Long itemId, NewCommentRequestDto newCommentDto);
}