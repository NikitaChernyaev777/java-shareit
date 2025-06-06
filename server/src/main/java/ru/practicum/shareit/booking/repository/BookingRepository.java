package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByItemOwnerId(Long ownerId, Sort sortOrder);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sortOrder);

    List<Booking> findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqual(Long ownerId, LocalDateTime now,
                                                                               LocalDateTime now1, Sort sortOrder);

    List<Booking> findByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime now, Sort sortOrder);

    List<Booking> findByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime now, Sort sortOrder);

    List<Booking> findByBookerId(Long bookerId, Sort sortOrder);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sortOrder);

    List<Booking> findByBookerIdAndStatusIn(Long bookerId, List<BookingStatus> statuses, Sort sortOrder);

    List<Booking> findByBookerIdAndStartLessThanEqualAndEndGreaterThanEqual(Long bookerId, LocalDateTime now,
                                                                            LocalDateTime now1, Sort sortOrder);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Sort sortOrder);

    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime now, Sort sortOrder);

    boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime now);
}