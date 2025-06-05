package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.request.NewUserRequestDto;
import ru.practicum.shareit.user.dto.request.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.response.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserResponseDto createUser(NewUserRequestDto newUserDto);

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(Long userId);

    UserResponseDto updateUser(Long userId, UpdateUserRequestDto updateUserDto);

    void deleteUserById(Long userId);

    User getExistingUser(Long userId);
}