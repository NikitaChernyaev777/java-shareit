package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.request.dto.request.NewItemRequestRequestDto;
import ru.practicum.shareit.request.dto.response.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {UserMapper.class},
        imports = java.time.LocalDateTime.class)
public interface ItemRequestMapper {

    @Mapping(target = "created", expression = "java(LocalDateTime.now())")
    ItemRequest toNewItemRequest(NewItemRequestRequestDto newItemRequestDto);

    default ItemRequestResponseDto toItemRequestResponseDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }

        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorName(itemRequest.getRequestor().getName())
                .created(itemRequest.getCreated())
                .build();
    }
}