package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.request.NewItemRequestDto;
import ru.practicum.shareit.item.dto.request.UpdateItemRequestDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {

    public static Item toNewItem(NewItemRequestDto newItemDto) {
        return Item.builder()
                .name(newItemDto.getName())
                .description(newItemDto.getDescription())
                .available(newItemDto.getAvailable())
                .build();
    }

    public static Item updateItem(Item item, UpdateItemRequestDto updateItemDto) {
        if (updateItemDto.hasName()) {
            item.setName(updateItemDto.getName());
        }
        if (updateItemDto.hasDescription()) {
            item.setDescription(updateItemDto.getDescription());
        }
        if (updateItemDto.hasAvailable()) {
            item.setAvailable(updateItemDto.getAvailable());
        }
        return item;
    }

    public static ItemResponseDto toItemResponseDto(final Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }
}