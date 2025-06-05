package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.dto.request.NewItemRequestDto;
import ru.practicum.shareit.item.dto.request.UpdateItemRequestDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.dto.response.ItemShortResponseDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CommentMapper.class})
public interface ItemMapper {

    Item toNewItem(NewItemRequestDto newItemDto);

    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "comments", ignore = true)
    ItemResponseDto toItemResponseDto(Item item);

    ItemShortResponseDto toItemShortResponseDto(Item item);

    void updateItem(UpdateItemRequestDto updateItemDto, @MappingTarget Item item);
}