package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.request.NewUserRequestDto;
import ru.practicum.shareit.user.dto.request.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.response.UserResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto create(NewUserRequestDto newUserDto) {
        log.info("Создание нового пользователя с email={}", newUserDto.getEmail());

        userRepository.findByEmail(newUserDto.getEmail()).ifPresent(userWithSameEmail -> {
            throw new DuplicatedDataException(String.format("Пользователь с email=%s уже существует",
                    newUserDto.getEmail()));
        });

        User user = userMapper.toNewUser(newUserDto);
        User savedUser = userRepository.save(user);

        log.info("Пользователь с id={} успешно создан", savedUser.getId());
        return userMapper.toUserResponseDto(savedUser);
    }

    @Override
    public List<UserResponseDto> findAll() {
        log.info("Запрошен список всех пользователей");
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto findById(Long userId) {
        log.info("Запрошен пользователь с id={}", userId);
        return userMapper.toUserResponseDto(getExistingUser(userId));
    }

    @Override
    public UserResponseDto update(Long userId, UpdateUserRequestDto updateUserDto) {
        log.info("Обновление пользователя с id={}", userId);

        User existingUser = getExistingUser(userId);

        if (updateUserDto.hasEmail()) {
            userRepository.findByEmail(updateUserDto.getEmail()).ifPresent(userWithSameEmail -> {
                if (!userWithSameEmail.getId().equals(userId)) {
                    throw new DuplicatedDataException(String.format("email=%s уже используется другим пользователем",
                            updateUserDto.getEmail()));
                }
            });
        }

        userMapper.updateUser(updateUserDto, existingUser);
        userRepository.update(existingUser);

        log.info("Пользователь с id={} успешно обновлен", existingUser.getId());
        return userMapper.toUserResponseDto(existingUser);
    }

    @Override
    public void deleteById(Long userId) {
        log.info("Удаление пользователя c id={}", userId);
        getExistingUser(userId);
        userRepository.deleteById(userId);
        log.info("Пользователь с id={} успешно удален", userId);
    }

    @Override
    public User getExistingUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId)));
    }
}