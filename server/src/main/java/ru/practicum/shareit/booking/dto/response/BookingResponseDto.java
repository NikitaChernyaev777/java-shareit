package ru.practicum.shareit.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.response.ItemShortResponseDto;
import ru.practicum.shareit.user.dto.response.UserResponseDto;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class BookingResponseDto {
    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final ItemShortResponseDto item;
    private final UserResponseDto booker;
    private final BookingStatus status;
}