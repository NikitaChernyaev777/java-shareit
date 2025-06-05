package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.request.NewUserRequestDto;
import ru.practicum.shareit.user.dto.request.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.response.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    final NewUserRequestDto newUserRequestDto = new NewUserRequestDto(
            "GoodName",
            "goodname@mail.com"
    );

    final UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto(
            1L,
            "SuperName",
            "supername@mail.com"
    );

    final UserResponseDto userResponseDtoBeforeUpdate = new UserResponseDto(
            1L,
            "GoodName",
            "goodname@mail.com"
    );

    final UserResponseDto userResponseDtoAfterUpdate = new UserResponseDto(
            1L,
            "SuperName",
            "supername@mail.com"
    );

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        when(userService.createUser(newUserRequestDto))
                .thenReturn(userResponseDtoBeforeUpdate);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userResponseDtoBeforeUpdate.getId()))
                .andExpect(jsonPath("$.name").value(userResponseDtoBeforeUpdate.getName()))
                .andExpect(jsonPath("$.email").value(userResponseDtoBeforeUpdate.getEmail()));
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userResponseDtoBeforeUpdate));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(userResponseDtoBeforeUpdate.getId()))
                .andExpect(jsonPath("$[0].name").value(userResponseDtoBeforeUpdate.getName()))
                .andExpect(jsonPath("$[0].email").value(userResponseDtoBeforeUpdate.getEmail()));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        when(userService.getUserById(1L))
                .thenReturn(userResponseDtoBeforeUpdate);

        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDtoBeforeUpdate.getId()))
                .andExpect(jsonPath("$.name").value(userResponseDtoBeforeUpdate.getName()))
                .andExpect(jsonPath("$.email").value(userResponseDtoBeforeUpdate.getEmail()));
    }

    @Test
    void shouldUpdateUserById() throws Exception {
        when(userService.updateUser(1L, updateUserRequestDto))
                .thenReturn(userResponseDtoAfterUpdate);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updateUserRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponseDtoAfterUpdate.getId()))
                .andExpect(jsonPath("$.name").value(userResponseDtoAfterUpdate.getName()))
                .andExpect(jsonPath("$.email").value(userResponseDtoAfterUpdate.getEmail()));
    }

    @Test
    void shouldDeleteUserByIdSuccessfully() throws Exception {
        doNothing().when(userService).deleteUserById(1L);

        mvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserById(1L);
    }
}