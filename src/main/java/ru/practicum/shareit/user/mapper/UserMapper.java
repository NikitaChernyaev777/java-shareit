package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.request.NewUserRequestDto;
import ru.practicum.shareit.user.dto.request.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.response.UserResponseDto;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static User toNewUser(NewUserRequestDto newUserDto) {
        return User.builder()
                .name(newUserDto.getName())
                .email(newUserDto.getEmail())
                .build();
    }

    public static User updateUser(User user, UpdateUserRequestDto updateUserDto) {
        if (updateUserDto.hasEmail()) {
            user.setEmail(updateUserDto.getEmail());
        }
        if (updateUserDto.hasName()) {
            user.setName(updateUserDto.getName());
        }
        return user;
    }

    public static UserResponseDto toUserResponseDto(final User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}