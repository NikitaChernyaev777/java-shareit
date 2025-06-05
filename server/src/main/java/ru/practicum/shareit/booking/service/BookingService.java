package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.request.NewBookingRequestDto;
import ru.practicum.shareit.booking.dto.response.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(Long userId, NewBookingRequestDto newBookingDto);

    List<BookingResponseDto> getBookingsByOwnerId(Long ownerId, BookingState bookingState);

    List<BookingResponseDto> getBookingsByBookerId(Long bookerId, BookingState bookingState);

    BookingResponseDto getBookingById(Long userId, Long bookingId);

    BookingResponseDto updateBookingStatus(Long userId, Long bookingId, boolean approved);
}