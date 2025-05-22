package ru.practicum.shareit.item.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.response.BookingResponseDto;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class ItemResponseDto {
    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final BookingResponseDto lastBooking;
    private final BookingResponseDto nextBooking;
    private List<CommentResponseDto> comments;
}