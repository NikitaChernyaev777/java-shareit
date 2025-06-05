package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.request.NewUserRequestDto;
import ru.practicum.shareit.user.dto.request.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.response.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    NewUserRequestDto firstUserRequestDto;
    NewUserRequestDto secondUserRequestDto;

    @BeforeEach
    void setUp() {
        firstUserRequestDto = NewUserRequestDto.builder()
                .name("GoodUser")
                .email("gooduser@mail.com")
                .build();

        secondUserRequestDto = NewUserRequestDto.builder()
                .name("SuperUser")
                .email("superuser@mail.com")
                .build();
    }

    @Test
    void shouldReturnAllUsers() {
        userService.createUser(firstUserRequestDto);
        UserResponseDto newUser = userService.createUser(secondUserRequestDto);
        List<UserResponseDto> users = userService.getAllUsers().stream().toList();

        assertThat(users.get(1).getId()).isEqualTo(newUser.getId());
        assertThat(users.get(1).getName()).isEqualTo(newUser.getName());
        assertThat(users.get(1).getEmail()).isEqualTo(newUser.getEmail());
    }

    @Test
    void shouldCreateUserAndRetrieveItById() {
        UserResponseDto createdUser = userService.createUser(firstUserRequestDto);
        UserResponseDto fetchedUser = userService.getUserById(createdUser.getId());

        assertThat(createdUser.getId()).isEqualTo(fetchedUser.getId());
        assertThat(createdUser.getName()).isEqualTo(fetchedUser.getName());
        assertThat(createdUser.getEmail()).isEqualTo(fetchedUser.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenCreatingUserWithDuplicateEmail() {
        userService.createUser(firstUserRequestDto);
        NewUserRequestDto newUser = NewUserRequestDto.builder()
                .name("SuperUser")
                .email("gooduser@mail.com")
                .build();

        assertThatThrownBy(() -> userService.createUser(newUser)).isInstanceOf(DuplicatedDataException.class);
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        assertThatThrownBy(() -> userService.getUserById(null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        UserResponseDto createdUser = userService.createUser(firstUserRequestDto);
        UpdateUserRequestDto updateUserDto = UpdateUserRequestDto.builder()
                .name(secondUserRequestDto.getName())
                .email(secondUserRequestDto.getEmail())
                .build();

        UserResponseDto updatedUser = userService.updateUser(createdUser.getId(), updateUserDto);

        assertThat(updatedUser.getId()).isEqualTo(createdUser.getId());
        assertThat(updatedUser.getName()).isEqualTo(secondUserRequestDto.getName());
        assertThat(updatedUser.getEmail()).isEqualTo(secondUserRequestDto.getEmail());
    }

    @Test
    void shouldUpdateUserWhenNameIsNull() {
        UserResponseDto createdUser = userService.createUser(firstUserRequestDto);
        UpdateUserRequestDto userUpdateDto = UpdateUserRequestDto.builder()
                .email("superuser@mail.com")
                .build();

        UserResponseDto updatedUser = userService.updateUser(createdUser.getId(), userUpdateDto);

        assertThat(updatedUser.getId()).isEqualTo(createdUser.getId());
        assertThat(updatedUser.getName()).isEqualTo(createdUser.getName());
        assertThat(updatedUser.getEmail()).isEqualTo(userUpdateDto.getEmail());
    }

    @Test
    void shouldUpdateUserWhenEmailIsNull() {
        UserResponseDto createdUser = userService.createUser(firstUserRequestDto);
        UpdateUserRequestDto userUpdateDto = UpdateUserRequestDto.builder()
                .name("SuperUser")
                .build();

        UserResponseDto updateUser = userService.updateUser(createdUser.getId(), userUpdateDto);

        assertThat(updateUser.getId()).isEqualTo(createdUser.getId());
        assertThat(updateUser.getName()).isEqualTo(userUpdateDto.getName());
        assertThat(updateUser.getEmail()).isEqualTo(createdUser.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingUserWithDuplicateEmail() {
        UserResponseDto createdUser = userService.createUser(firstUserRequestDto);
        UserResponseDto user3 = userService.createUser(secondUserRequestDto);
        UpdateUserRequestDto updateUser = UpdateUserRequestDto.builder()
                .name("SuperUser")
                .email("gooduser@mail.com")
                .build();

        assertThatThrownBy(() -> userService.updateUser(user3.getId(), updateUser))
                .isInstanceOf(DuplicatedDataException.class);
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        UserResponseDto createdUser = userService.createUser(firstUserRequestDto);
        userService.deleteUserById(createdUser.getId());

        assertThatThrownBy(() -> userService.getUserById(createdUser.getId()))
                .isInstanceOf(NotFoundException.class);
    }
}