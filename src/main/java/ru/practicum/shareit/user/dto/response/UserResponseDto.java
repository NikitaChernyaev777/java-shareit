package ru.practicum.shareit.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class UserResponseDto {
    private final Long id;
    private final String name;
    private final String email;
}