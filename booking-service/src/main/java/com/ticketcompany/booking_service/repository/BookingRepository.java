package com.ticketcompany.booking_service.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ticketcompany.booking_service.domain.Booking;
import com.ticketcompany.booking_service.domain.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, Instant cutoff);
    List<Booking> findByStatusAndExpiresAtBefore(BookingStatus status, Instant now);
    @Query("select b.seat.id from Booking b where b.seat.id in :seatIds and b.status = :status")
    List<Long> findSeatIdsWithStatus(@Param("seatIds") List<Long> seatIds,
                                     @Param("status") BookingStatus status);
}