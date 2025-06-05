package ru.practicum.shareit.item.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ItemShortResponseDto {
    private final Long id;
    private final String name;
    private final Boolean available;
    private final Long requestId;
}