package ru.practicum.shareit.request;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.request.NewItemRequestRequestDto;
import ru.practicum.shareit.request.dto.response.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.request.NewUserRequestDto;
import ru.practicum.shareit.user.dto.response.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class ItemRequestServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestService itemRequestService;

    NewUserRequestDto newUserDto;
    NewItemRequestRequestDto newItemRequestDto;

    @BeforeEach
    void setUp() {
        newUserDto = NewUserRequestDto.builder()
                .name("User")
                .email("user@mail.com")
                .build();

        newItemRequestDto = NewItemRequestRequestDto.builder()
                .description("ItemRequestDescription")
                .build();
    }

    @Test
    void shouldCreateAndFetchItemRequestById() {
        UserResponseDto user = userService.createUser(newUserDto);
        ItemRequestResponseDto createdRequest = itemRequestService.createItemRequest(user.getId(), newItemRequestDto);
        ItemRequestResponseDto fetchedRequest = itemRequestService.getItemRequestById(user.getId(),
                createdRequest.getId());

        assertThat(createdRequest.getId()).isEqualTo(fetchedRequest.getId());
        assertThat(createdRequest.getDescription()).isEqualTo(fetchedRequest.getDescription());
        assertThat(createdRequest.getRequestorName()).isEqualTo(fetchedRequest.getRequestorName());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        assertThatThrownBy(() -> itemRequestService.createItemRequest(1000L, newItemRequestDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        assertThatThrownBy(() -> itemRequestService.createItemRequest(null, newItemRequestDto))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    void shouldReturnRequestsCreatedByUser() {
        UserResponseDto user = userService.createUser(newUserDto);
        ItemRequestResponseDto itemRequest = itemRequestService.createItemRequest(user.getId(), newItemRequestDto);

        List<ItemRequestResponseDto> itemRequests = itemRequestService.getOwnItemRequests(user.getId()).stream()
                .toList();

        assertThat(itemRequests).hasSize(1);
        assertThat(itemRequests.getFirst().getId()).isEqualTo(itemRequest.getId());
        assertThat(itemRequests.getFirst().getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(itemRequests.getFirst().getRequestorName()).isEqualTo(itemRequest.getRequestorName());
    }

    @Test
    void shouldReturnRequestsCreatedByOtherUsersOnly() {
        UserResponseDto requestor = userService.createUser(NewUserRequestDto.builder()
                .name("Requestor")
                .email("requestor@mail.com")
                .build());
        UserResponseDto viewer = userService.createUser(NewUserRequestDto.builder()
                .name("Viewer")
                .email("viewer@mail.com")
                .build());
        ItemRequestResponseDto createdRequest = itemRequestService.createItemRequest(requestor.getId(),
                newItemRequestDto);

        List<ItemRequestResponseDto> requestsFromOthers = itemRequestService
                .getAllOtherUsersItemRequests(viewer.getId(), 0, 10);

        assertThat(requestsFromOthers).hasSize(1);
        assertThat(requestsFromOthers.getFirst().getId()).isEqualTo(createdRequest.getId());
        assertThat(requestsFromOthers.getFirst().getDescription()).isEqualTo(createdRequest.getDescription());
        assertThat(requestsFromOthers.getFirst().getRequestorName()).isEqualTo(createdRequest.getRequestorName());
    }
}