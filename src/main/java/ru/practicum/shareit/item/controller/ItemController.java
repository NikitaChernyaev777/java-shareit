package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.request.NewItemRequestDto;
import ru.practicum.shareit.item.dto.request.UpdateItemRequestDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import static ru.practicum.shareit.constant.Headers.USER_ID_HEADER_NAME;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemResponseDto> getAllByOwnerId(@RequestHeader(USER_ID_HEADER_NAME) Long ownerId) {
        return itemService.findAllByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> getByNameOrDescription(@RequestParam(value = "text", required = false) String text) {
        return itemService.searchByNameOrDescription(text);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getById(@PathVariable("itemId") Long itemId) {
        return itemService.findById(itemId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto create(@RequestHeader(USER_ID_HEADER_NAME) Long userId,
                                  @Valid @RequestBody NewItemRequestDto newItemDto) {
        return itemService.create(userId, newItemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto update(@RequestHeader(USER_ID_HEADER_NAME) Long userId,
                                  @PathVariable("itemId") Long itemId,
                                  @RequestBody UpdateItemRequestDto updateItemDto) {
        return itemService.update(userId, itemId, updateItemDto);
    }
}