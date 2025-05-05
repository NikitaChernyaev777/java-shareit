package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.request.NewUserRequestDto;
import ru.practicum.shareit.user.dto.request.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.response.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserResponseDto create(NewUserRequestDto newUserDto);

    List<UserResponseDto> findAll();

    UserResponseDto findById(Long userId);

    UserResponseDto update(Long userId, UpdateUserRequestDto updateUserDto);

    void deleteById(Long userId);

    User getExistingUser(Long userId);
}