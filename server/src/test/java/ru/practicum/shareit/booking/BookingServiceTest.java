package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.request.NewBookingRequestDto;
import ru.practicum.shareit.booking.dto.response.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.request.NewItemRequestDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
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
public class BookingServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    NewItemRequestDto firstItem;
    NewUserRequestDto firstUser;
    NewUserRequestDto secondUser;

    @BeforeEach
    void setUp() {
        firstItem = NewItemRequestDto.builder()
                .name("Item1")
                .description("Description1")
                .available(true)
                .build();

        firstUser = NewUserRequestDto.builder()
                .name("User1")
                .email("user1@mail.com")
                .build();

        secondUser = NewUserRequestDto.builder()
                .name("User2")
                .email("user2@mail.com")
                .build();
    }

    @Test
    void shouldCreateBookingSuccessfully() {
        UserResponseDto owner = userService.createUser(firstUser);
        UserResponseDto booker = userService.createUser(secondUser);
        ItemResponseDto item = itemService.createItem(owner.getId(), firstItem);
        NewBookingRequestDto bookingRequest = NewBookingRequestDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(item.getId())
                .build();

        BookingResponseDto createdBooking = bookingService.createBooking(booker.getId(), bookingRequest);

        assertThat(createdBooking.getId()).isNotNull();
        assertThat(createdBooking.getStart()).isEqualTo(bookingRequest.getStart());
        assertThat(createdBooking.getEnd()).isEqualTo(bookingRequest.getEnd());
        assertThat(createdBooking.getItem().getId()).isEqualTo(bookingRequest.getItemId());
        assertThat(createdBooking.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(createdBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        UserResponseDto user3 = userService.createUser(firstUser);
        ItemResponseDto item = itemService.createItem(user3.getId(), firstItem);
        NewBookingRequestDto bookingRequest = NewBookingRequestDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(item.getId())
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(null, bookingRequest))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    void shouldThrowExceptionWhenItemIsNotAvailable() {
        UserResponseDto owner = userService.createUser(firstUser);
        UserResponseDto booker = userService.createUser(secondUser);
        NewItemRequestDto unavailableItemRequest = NewItemRequestDto.builder()
                .name("Yandex")
                .description("YandexPracticum")
                .available(false)
                .build();
        ItemResponseDto unavailableItem = itemService.createItem(owner.getId(), unavailableItemRequest);
        NewBookingRequestDto bookingRequest = NewBookingRequestDto.builder()
                .itemId(unavailableItem.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(booker.getId(), bookingRequest))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void shouldThrowExceptionWhenBookingStartIsAfterEnd() {
        UserResponseDto owner = userService.createUser(firstUser);
        UserResponseDto booker = userService.createUser(secondUser);
        ItemResponseDto item = itemService.createItem(owner.getId(), firstItem);
        NewBookingRequestDto bookingRequest = NewBookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusHours(1))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(booker.getId(), bookingRequest))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void shouldThrowExceptionWhenOwnerCreateBooking() {
        UserResponseDto owner = userService.createUser(firstUser);
        NewBookingRequestDto bookingRequest = NewBookingRequestDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(owner.getId(), bookingRequest))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    void shouldUpdateBookingStatus() {
        UserResponseDto owner = userService.createUser(firstUser);
        UserResponseDto booker = userService.createUser(secondUser);
        ItemResponseDto item = itemService.createItem(owner.getId(), firstItem);
        NewBookingRequestDto bookingRequest = NewBookingRequestDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(item.getId())
                .build();

        BookingResponseDto createdBooking = bookingService.createBooking(booker.getId(), bookingRequest);
        BookingResponseDto approvedBooking = bookingService.updateBookingStatus(owner.getId(), createdBooking.getId(),
                true);

        assertThat(approvedBooking.getId()).isEqualTo(createdBooking.getId());
        assertThat(approvedBooking.getStart()).isEqualTo(createdBooking.getStart());
        assertThat(approvedBooking.getEnd()).isEqualTo(createdBooking.getEnd());
        assertThat(approvedBooking.getItem()).isEqualTo(createdBooking.getItem());
        assertThat(approvedBooking.getBooker()).isEqualTo(booker);
        assertThat(approvedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotOwnerWhenUpdateBooking() {
        UserResponseDto owner = userService.createUser(firstUser);
        UserResponseDto booker = userService.createUser(secondUser);
        ItemResponseDto item = itemService.createItem(owner.getId(), firstItem);
        NewBookingRequestDto bookingRequest = NewBookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingResponseDto createdBooking = bookingService.createBooking(booker.getId(), bookingRequest);

        assertThatThrownBy(() -> bookingService.updateBookingStatus(booker.getId(), createdBooking.getId(),
                true))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void shouldGetBookingById() {
        UserResponseDto owner = userService.createUser(firstUser);
        UserResponseDto booker = userService.createUser(secondUser);
        ItemResponseDto item = itemService.createItem(owner.getId(), firstItem);
        NewBookingRequestDto bookingRequest = NewBookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingResponseDto createdBooking = bookingService.createBooking(booker.getId(), bookingRequest);
        BookingResponseDto retrievedBookingByBooker = bookingService.getBookingById(booker.getId(),
                createdBooking.getId());
        BookingResponseDto retrievedBookingByOwner = bookingService.getBookingById(owner.getId(),
                createdBooking.getId());

        assertThat(retrievedBookingByBooker.getId()).isEqualTo(createdBooking.getId());
        assertThat(retrievedBookingByBooker.getStart()).isEqualTo(createdBooking.getStart());
        assertThat(retrievedBookingByBooker.getEnd()).isEqualTo(createdBooking.getEnd());
        assertThat(retrievedBookingByBooker.getItem()).isEqualTo(createdBooking.getItem());
        assertThat(retrievedBookingByBooker.getBooker()).isEqualTo(booker);
        assertThat(retrievedBookingByBooker.getStatus()).isEqualTo(BookingStatus.WAITING);

        assertThat(retrievedBookingByOwner.getId()).isEqualTo(createdBooking.getId());
        assertThat(retrievedBookingByOwner.getStart()).isEqualTo(createdBooking.getStart());
        assertThat(retrievedBookingByOwner.getEnd()).isEqualTo(createdBooking.getEnd());
        assertThat(retrievedBookingByOwner.getItem()).isEqualTo(createdBooking.getItem());
        assertThat(retrievedBookingByOwner.getBooker()).isEqualTo(booker);
        assertThat(retrievedBookingByOwner.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void shouldReturnAllBookingsByBookerId() {
        UserResponseDto owner = userService.createUser(firstUser);
        UserResponseDto booker = userService.createUser(secondUser);
        ItemResponseDto item = itemService.createItem(owner.getId(), firstItem);
        NewBookingRequestDto bookingRequest = NewBookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingResponseDto createdBooking = bookingService.createBooking(booker.getId(), bookingRequest);
        List<BookingResponseDto> retrievedBookingsByBooker = bookingService.getBookingsByBookerId(booker.getId(),
                BookingState.ALL).stream().toList();

        assertThat(retrievedBookingsByBooker.getFirst().getId()).isEqualTo(createdBooking.getId());
        assertThat(retrievedBookingsByBooker.getFirst().getStart()).isEqualTo(createdBooking.getStart());
        assertThat(retrievedBookingsByBooker.getFirst().getEnd()).isEqualTo(createdBooking.getEnd());
        assertThat(retrievedBookingsByBooker.getFirst().getItem()).isEqualTo(createdBooking.getItem());
        assertThat(retrievedBookingsByBooker.getFirst().getBooker()).isEqualTo(booker);
        assertThat(retrievedBookingsByBooker.getFirst().getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void shouldReturnAllBookingsByOwnerId() {
        UserResponseDto owner = userService.createUser(firstUser);
        UserResponseDto booker = userService.createUser(secondUser);
        ItemResponseDto item = itemService.createItem(owner.getId(), firstItem);
        NewBookingRequestDto bookingRequest = NewBookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingResponseDto createdBooking = bookingService.createBooking(booker.getId(), bookingRequest);
        List<BookingResponseDto> retrievedBookingsByOwner = bookingService.getBookingsByOwnerId(owner.getId(),
                BookingState.ALL).stream().toList();

        assertThat(retrievedBookingsByOwner.getFirst().getId()).isEqualTo(createdBooking.getId());
        assertThat(retrievedBookingsByOwner.getFirst().getStart()).isEqualTo(createdBooking.getStart());
        assertThat(retrievedBookingsByOwner.getFirst().getEnd()).isEqualTo(createdBooking.getEnd());
        assertThat(retrievedBookingsByOwner.getFirst().getItem()).isEqualTo(createdBooking.getItem());
        assertThat(retrievedBookingsByOwner.getFirst().getBooker()).isEqualTo(booker);
        assertThat(retrievedBookingsByOwner.getFirst().getStatus()).isEqualTo(BookingStatus.WAITING);
    }
}