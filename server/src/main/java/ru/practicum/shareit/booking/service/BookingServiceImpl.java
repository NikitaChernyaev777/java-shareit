package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.request.NewBookingRequestDto;
import ru.practicum.shareit.booking.dto.response.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.model.BookingStatus.CANCELED;
import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingResponseDto createBooking(Long userId, NewBookingRequestDto newBookingDto) {
        log.info("Создание нового бронирования для вещи c itemId={} пользователем с userId={}",
                newBookingDto.getItemId(), userId);

        validateDate(newBookingDto);
        User booker = userService.getExistingUser(userId);
        Item item = getExistingItem(newBookingDto.getItemId());

        if (!item.getAvailable()) {
            throw new ValidationException("Данная вещь недоступна для бронирования");
        }

        Booking booking = bookingMapper.toNewBooking(newBookingDto, item, booker);
        log.info("Бронирование для Вещи с itemId={} успешно создана", item.getId());

        return bookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingResponseDto> getBookingsByOwnerId(Long ownerId, BookingState bookingState) {
        log.info("Владелец с ownerId={} запросил список {} бронирований", ownerId,
                bookingState == BookingState.ALL ? "всех" : "со статусом " + bookingState);

        userService.getExistingUser(ownerId);
        List<Item> userItems = itemRepository.findByOwnerIdOrderByIdAsc(ownerId);

        if (userItems.isEmpty()) {
            throw new ValidationException(String.format("У владельца с id=%d нет вещей для бронирования", ownerId));
        }

        LocalDateTime now = LocalDateTime.now();
        Sort sortOrder = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings;

        switch (bookingState) {
            case WAITING -> bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, WAITING, sortOrder);
            case REJECTED -> bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, REJECTED, sortOrder);
            case CURRENT -> bookings = bookingRepository.findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqual(
                    ownerId, now, now, sortOrder);
            case FUTURE -> bookings = bookingRepository.findByItemOwnerIdAndStartAfter(ownerId, now, sortOrder);
            case PAST -> bookings = bookingRepository.findByItemOwnerIdAndEndBefore(ownerId, now, sortOrder);
            case ALL -> bookings = bookingRepository.findByItemOwnerId(ownerId, sortOrder);
            default -> throw new ValidationException("Неизвестное состояние бронирования: " + bookingState);
        }

        return bookings.stream()
                .map(bookingMapper::toBookingResponseDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> getBookingsByBookerId(Long bookerId, BookingState bookingState) {
        log.info("Арендатор с bookerId={} запросил список {} бронирований", bookerId,
                bookingState == BookingState.ALL ? "всех" : "со статусом " + bookingState);

        userService.getExistingUser(bookerId);

        LocalDateTime now = LocalDateTime.now();
        Sort sortOrder = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookings;

        switch (bookingState) {
            case WAITING -> bookings = bookingRepository.findByBookerIdAndStatus(bookerId, WAITING, sortOrder);
            case REJECTED -> bookings = bookingRepository.findByBookerIdAndStatusIn(bookerId,
                    List.of(REJECTED, CANCELED), sortOrder);
            case CURRENT -> bookings = bookingRepository.findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqual(
                    bookerId, now, now, sortOrder);
            case FUTURE -> bookings = bookingRepository.findByBookerIdAndStartAfter(bookerId, now, sortOrder);
            case PAST -> bookings = bookingRepository.findByBookerIdAndEndBefore(bookerId, now, sortOrder);
            case ALL -> bookings = bookingRepository.findByBookerId(bookerId, sortOrder);
            default -> throw new ValidationException("Неизвестное состояние бронирования: " + bookingState);
        }

        return bookings.stream()
                .map(bookingMapper::toBookingResponseDto)
                .toList();
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        log.info("Пользователь с userId={} запросил бронирование с bookingId={}", userId, bookingId);
        return bookingMapper.toBookingResponseDto(validateBooking(userId, bookingId));
    }

    @Override
    @Transactional
    public BookingResponseDto updateBookingStatus(Long userId, Long bookingId, boolean approved) {
        log.info("Изменение пользователем с userId={} статуса бронирования с bookingId={}", userId, bookingId);

        Booking booking = getExistingBooking(bookingId);
        Item item = booking.getItem();

        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Пользователь не является владельцем вещи " +
                    "и не может изменять статус бронирования");
        }

        if (!booking.getStatus().equals(WAITING)) {
            throw new ValidationException("Статус бронирования уже установлен и не подлежит изменению");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        log.info("Статус бронирования с bookingId={} обновлен на {}", bookingId, booking.getStatus());

        return bookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    private Booking validateBooking(Long userId, Long bookingId) {
        User user = userService.getExistingUser(userId);
        Booking booking = getExistingBooking(bookingId);
        Item item = booking.getItem();

        boolean isOwner = item.getOwner().getId().equals(userId);
        boolean isBooker = booking.getBooker().getId().equals(userId);

        if (!isBooker && !isOwner) {
            throw new ValidationException("Пользователь не имеет доступа к данному бронированию");
        }

        return booking;
    }

    private void validateDate(NewBookingRequestDto newBookingDto) {
        if (!newBookingDto.getEnd().isAfter(newBookingDto.getStart())) {
            throw new ValidationException("Некорректные даты начала и окончания бронирования");
        }
    }

    private Booking getExistingBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с id=%d не найдено", bookingId)));
    }

    private Item getExistingItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id=%d не найдена", itemId)));
    }
}