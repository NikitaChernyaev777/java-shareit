package ru.practicum.shareit.request.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ItemRequestResponseDto {
    private final Long id;
    private final String description;
    private final String requestorName;
    private final LocalDateTime created;
    private List<ItemResponseDto> items;
}