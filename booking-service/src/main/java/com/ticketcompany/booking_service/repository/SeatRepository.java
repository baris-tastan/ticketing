package com.ticketcompany.booking_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticketcompany.booking_service.domain.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByEventIdOrderBySeatNumberAsc(Long eventId);
}