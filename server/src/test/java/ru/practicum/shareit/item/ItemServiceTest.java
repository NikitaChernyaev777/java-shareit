package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.request.NewBookingRequestDto;
import ru.practicum.shareit.booking.dto.response.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.request.NewCommentRequestDto;
import ru.practicum.shareit.item.dto.request.NewItemRequestDto;
import ru.practicum.shareit.item.dto.request.UpdateItemRequestDto;
import ru.practicum.shareit.item.dto.response.CommentResponseDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.request.NewItemRequestRequestDto;
import ru.practicum.shareit.request.dto.response.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.request.NewUserRequestDto;
import ru.practicum.shareit.user.dto.response.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemRequestService itemRequestService;

    NewUserRequestDto ownerDto;
    NewUserRequestDto requestorDto;
    NewItemRequestDto itemDto;
    NewItemRequestDto anotherItemDto;

    @BeforeEach
    void setUp() {
        ownerDto = NewUserRequestDto.builder()
                .name("GoodUser")
                .email("gooduser@example.com")
                .build();

        requestorDto = NewUserRequestDto.builder()
                .name("SuperUser")
                .email("superuser@example.com")
                .build();

        itemDto = NewItemRequestDto.builder()
                .name("GoodItem")
                .description("Wonderful description")
                .available(true).build();

        anotherItemDto = NewItemRequestDto.builder()
                .name("SuperItem")
                .description("Super description")
                .available(true)
                .build();
    }

    @Test
    void shouldCreateItemSuccessfully() {
        UserResponseDto owner = userService.createUser(ownerDto);
        ItemResponseDto createdItem = itemService.createItem(owner.getId(), itemDto);
        ItemResponseDto retrievedItem = itemService.getItemById(owner.getId(), createdItem.getId());

        assertThat(createdItem.getId()).isEqualTo(retrievedItem.getId());
        assertThat(createdItem.getName()).isEqualTo(retrievedItem.getName());
        assertThat(createdItem.getDescription()).isEqualTo(retrievedItem.getDescription());
        assertThat(createdItem.getAvailable()).isEqualTo(retrievedItem.getAvailable());
        assertThat(createdItem.getRequestId()).isNull();
    }

    @Test
    void shouldCreateItemAndLinkToItemRequestCorrectly() {
        UserResponseDto owner = userService.createUser(ownerDto);
        UserResponseDto requestor = userService.createUser(requestorDto);
        NewItemRequestRequestDto newRequestDto = NewItemRequestRequestDto.builder()
                .description("Super description")
                .build();
        ItemRequestResponseDto createdRequest = itemRequestService.createItemRequest(requestor.getId(), newRequestDto);
        NewItemRequestDto newItemDto = NewItemRequestDto.builder()
                .name("NewItemRequest")
                .description("Super item request")
                .available(true)
                .requestId(createdRequest.getId())
                .build();
        ItemResponseDto createdItem = itemService.createItem(owner.getId(), newItemDto);
        ItemResponseDto retrievedItem = itemService.getItemById(owner.getId(), createdItem.getId());

        assertThat(createdItem.getId()).isEqualTo(retrievedItem.getId());
        assertThat(createdItem.getName()).isEqualTo(retrievedItem.getName());
        assertThat(createdItem.getDescription()).isEqualTo(retrievedItem.getDescription());
        assertThat(createdItem.getAvailable()).isEqualTo(retrievedItem.getAvailable());
        assertThat(createdItem.getRequestId()).isEqualTo(retrievedItem.getRequestId());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        Long nonExistentUserId = 777L;

        assertThatThrownBy(() -> itemService.createItem(nonExistentUserId, itemDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowInvalidDataAccessExceptionWhenUserIdIsNull() {
        assertThatThrownBy(() -> itemService.createItem(null, itemDto))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    void shouldUpdateItemWhenAllFieldsAreProvided() {
        UpdateItemRequestDto updateRequest = UpdateItemRequestDto.builder()
                .name("SuperItem")
                .description("Super description")
                .available(true)
                .build();
        UserResponseDto owner = userService.createUser(ownerDto);
        ItemResponseDto createdItem = itemService.createItem(owner.getId(), itemDto);
        ItemResponseDto updatedItem = itemService.updateItem(owner.getId(), createdItem.getId(), updateRequest);

        assertThat(updatedItem.getId()).isEqualTo(createdItem.getId());
        assertThat(updatedItem.getName()).isEqualTo(anotherItemDto.getName());
        assertThat(updatedItem.getDescription()).isEqualTo(anotherItemDto.getDescription());
        assertThat(updatedItem.getAvailable()).isEqualTo(anotherItemDto.getAvailable());
    }

    @Test
    void shouldUpdateItemWhenNameIsNullAndOtherFieldsAreProvided() {
        UserResponseDto owner = userService.createUser(ownerDto);
        UpdateItemRequestDto updateRequest = UpdateItemRequestDto.builder()
                .description("Mega update description")
                .available(false)
                .build();
        ItemResponseDto createdItem = itemService.createItem(owner.getId(), itemDto);
        ItemResponseDto updatedItem = itemService.updateItem(owner.getId(), createdItem.getId(), updateRequest);

        assertThat(updatedItem.getId()).isEqualTo(createdItem.getId());
        assertThat(updatedItem.getName()).isEqualTo(itemDto.getName());
        assertThat(updatedItem.getDescription()).isEqualTo(updateRequest.getDescription());
        assertThat(updatedItem.getAvailable()).isEqualTo(updateRequest.getAvailable());
    }

    @Test
    void shouldUpdateItemWhenDescriptionIsNull() {
        UserResponseDto owner = userService.createUser(ownerDto);
        UpdateItemRequestDto partialUpdateRequest = UpdateItemRequestDto.builder()
                .name("Super name")
                .available(false)
                .build();
        ItemResponseDto createdItem = itemService.createItem(owner.getId(), itemDto);
        ItemResponseDto updatedItem = itemService.updateItem(owner.getId(), createdItem.getId(), partialUpdateRequest);

        assertThat(updatedItem.getId()).isEqualTo(createdItem.getId());
        assertThat(updatedItem.getName()).isEqualTo(partialUpdateRequest.getName());
        assertThat(updatedItem.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(updatedItem.getAvailable()).isEqualTo(partialUpdateRequest.getAvailable());
    }

    @Test
    void shouldUpdateItemWhenAvailableIsNull() {
        UserResponseDto owner = userService.createUser(ownerDto);
        UpdateItemRequestDto updateRequest = UpdateItemRequestDto.builder()
                .name("Good name")
                .description("Good description")
                .build();
        ItemResponseDto createdItem = itemService.createItem(owner.getId(), itemDto);
        ItemResponseDto updatedItem = itemService.updateItem(owner.getId(), createdItem.getId(), updateRequest);

        assertThat(updatedItem.getId()).isEqualTo(createdItem.getId());
        assertThat(updatedItem.getName()).isEqualTo(updateRequest.getName());
        assertThat(updatedItem.getDescription()).isEqualTo(updateRequest.getDescription());
        assertThat(updatedItem.getAvailable()).isEqualTo(itemDto.getAvailable());
    }

    @Test
    void shouldThrowExceptionWhenNonOwnerTriesToUpdateItem() {
        UpdateItemRequestDto updateRequest = UpdateItemRequestDto.builder()
                .name("UpdateITem")
                .description("super description")
                .available(true)
                .build();
        UserResponseDto owner = userService.createUser(ownerDto);
        UserResponseDto newOwner = userService.createUser(requestorDto);
        ItemResponseDto createdItem = itemService.createItem(owner.getId(), itemDto);

        assertThatThrownBy(() -> itemService.updateItem(newOwner.getId(), createdItem.getId(), updateRequest))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void shouldReturnAllItemsWhenQueriedByOwnerId() {
        UserResponseDto owner = userService.createUser(ownerDto);
        itemService.createItem(owner.getId(), itemDto);
        ItemResponseDto lastCreatedItem = itemService.createItem(owner.getId(), anotherItemDto);

        List<ItemResponseDto> retrievedItems = itemService.getItemsByOwnerId(owner.getId()).stream().toList();

        assertThat(retrievedItems).hasSize(2);
        assertThat(retrievedItems.get(1).getId()).isEqualTo(lastCreatedItem.getId());
        assertThat(retrievedItems.get(1).getName()).isEqualTo(lastCreatedItem.getName());
        assertThat(retrievedItems.get(1).getDescription()).isEqualTo(lastCreatedItem.getDescription());
        assertThat(retrievedItems.get(1).getAvailable()).isEqualTo(lastCreatedItem.getAvailable());
    }

    @Test
    void shouldAddCommentToItemAfterCompletedBooking() {
        UserResponseDto booker = userService.createUser(requestorDto);
        ItemResponseDto createdItem = itemService.createItem(booker.getId(), itemDto);

        NewBookingRequestDto bookingRequest = NewBookingRequestDto.builder()
                .start(LocalDateTime.of(2025, 3, 10, 12, 0))
                .end(LocalDateTime.of(2025, 3, 10, 13, 0))
                .itemId(createdItem.getId())
                .build();
        BookingResponseDto createdBooking = bookingService.createBooking(booker.getId(), bookingRequest);
        bookingService.updateBookingStatus(booker.getId(), createdBooking.getId(), true);

        NewCommentRequestDto commentRequest = NewCommentRequestDto.builder().text("Text").build();
        CommentResponseDto createdComment = itemService.createComment(booker.getId(), createdItem.getId(),
                commentRequest);

        assertThat(createdComment.getText()).isEqualTo(commentRequest.getText());
        assertThat(createdComment.getAuthorName()).isEqualTo(booker.getName());
    }

    @Test
    void shouldThrowExceptionWhenOwnerAttemptsToCommentOnOwnItem() {
        UserResponseDto owner = userService.createUser(ownerDto);
        ItemResponseDto item = itemService.createItem(owner.getId(), itemDto);
        NewBookingRequestDto bookingRequest = NewBookingRequestDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(1))
                .itemId(item.getId())
                .build();
        UserResponseDto booker = userService.createUser(requestorDto);
        bookingService.createBooking(booker.getId(), bookingRequest);
        NewCommentRequestDto commentRequest = NewCommentRequestDto.builder().text("Text").build();

        assertThatThrownBy(() -> itemService.createComment(owner.getId(), item.getId(), commentRequest))
                .isInstanceOf(ValidationException.class);
    }
}