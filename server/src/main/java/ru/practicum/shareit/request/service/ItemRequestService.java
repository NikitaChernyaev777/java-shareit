package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.request.NewItemRequestRequestDto;
import ru.practicum.shareit.request.dto.response.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponseDto createItemRequest(Long userId, NewItemRequestRequestDto newItemRequestDto);

    List<ItemRequestResponseDto> getOwnItemRequests(Long requestorId);

    List<ItemRequestResponseDto> getAllOtherUsersItemRequests(Long userId, Integer from, Integer size);

    ItemRequestResponseDto getItemRequestById(Long userId, Long requestId);
}