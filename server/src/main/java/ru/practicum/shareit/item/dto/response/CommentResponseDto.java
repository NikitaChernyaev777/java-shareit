package ru.practicum.shareit.item.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class CommentResponseDto {
    private final Long id;
    private final String text;
    private final String authorName;
    private final LocalDateTime created;
}