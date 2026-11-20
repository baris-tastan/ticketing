package com.ticketcompany.booking_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticketcompany.booking_service.domain.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByOrderByStartsAtAsc();
}