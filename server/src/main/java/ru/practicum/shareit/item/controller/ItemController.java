package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.dto.request.NewCommentRequestDto;
import ru.practicum.shareit.item.dto.request.NewItemRequestDto;
import ru.practicum.shareit.item.dto.request.UpdateItemRequestDto;
import ru.practicum.shareit.item.dto.response.CommentResponseDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.constant.Headers.USER_ID_HEADER_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemResponseDto> getItemsByOwnerId(@RequestHeader(USER_ID_HEADER_NAME) Long ownerId) {
        return itemService.getItemsByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> getByNameOrDescription(@RequestHeader(USER_ID_HEADER_NAME) Long userId,
                                                        @RequestParam(value = "text", required = false) String text) {
        return itemService.search(userId, text);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(@RequestHeader(USER_ID_HEADER_NAME) Long userId,
                                       @PathVariable("itemId") Long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto createItem(@RequestHeader(USER_ID_HEADER_NAME) Long userId,
                                      @RequestBody NewItemRequestDto newItemDto) {
        return itemService.createItem(userId, newItemDto);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createComment(@RequestHeader(USER_ID_HEADER_NAME) Long userId,
                                            @PathVariable("itemId") Long itemId,
                                            @RequestBody NewCommentRequestDto newCommentDto) {
        return itemService.createComment(userId, itemId, newCommentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader(USER_ID_HEADER_NAME) Long userId,
                                      @PathVariable("itemId") Long itemId,
                                      @RequestBody UpdateItemRequestDto updateItemDto) {
        return itemService.updateItem(userId, itemId, updateItemDto);
    }
}