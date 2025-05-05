package ru.practicum.shareit.item.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ItemResponseDto {
    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
}