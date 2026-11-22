package com.ticketcompany.booking_service.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ticketcompany.booking_service.domain.Booking;
import com.ticketcompany.booking_service.domain.BookingStatus;

import jakarta.transaction.Transactional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, Instant cutoff);
    List<Booking> findByStatusAndExpiresAtBefore(BookingStatus status, Instant now);
    @Query("select b.seat.id from Booking b "
     + "where b.seat.id in :seatIds and b.status = :pending and b.expiresAt > :now")
    List<Long> findSeatIdsWithPendingBooking(@Param("seatIds") List<Long> seatIds,
                                             @Param("now") Instant now,
                                             @Param("pending") BookingStatus pending);

    default List<Long> findSeatIdsWithActivePendingBooking(List<Long> seatIds, Instant now) {
        return findSeatIdsWithPendingBooking(seatIds, now, BookingStatus.PENDING);
    }
    @EntityGraph(attributePaths = "seat")
    Optional<Booking> findWithSeatById(Long id);

    @Modifying
    @Transactional
    @Query("update Booking b set b.status = :cancelled, b.version = b.version + 1 "
         + "where b.seat.id = :seatId and b.status = :pending and b.expiresAt <= :now")
    int cancelExpiredPendingBooking(@Param("seatId") Long seatId,
                                    @Param("now") Instant now,
                                    @Param("pending") BookingStatus pending,
                                    @Param("cancelled") BookingStatus cancelled);

    default int cancelExpiredPendingBooking(Long seatId, Instant now) {
        return cancelExpiredPendingBooking(seatId, now, BookingStatus.PENDING, BookingStatus.CANCELLED);
    }
}