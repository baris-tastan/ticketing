package com.ticketcompany.booking_service.service;

import com.ticketcompany.booking_service.repository.BookingRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.ticketcompany.booking_service.domain.Seat;
import com.ticketcompany.booking_service.dto.SeatResponse;
import com.ticketcompany.booking_service.exception.EventNotFoundException;
import com.ticketcompany.booking_service.repository.EventRepository;
import com.ticketcompany.booking_service.repository.SeatRepository;

@Service
public class SeatService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;
    private final SeatHoldService seatHoldService;

    public SeatService(EventRepository eventRepository, SeatRepository seatRepository,
                       SeatHoldService seatHoldService, BookingRepository bookingRepository) {
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
        this.seatHoldService = seatHoldService;
        this.bookingRepository = bookingRepository;
    }

    public List<SeatResponse> getSeatsForEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException(eventId);
        }

        List<Seat> seats = seatRepository.findByEventIdOrderBySeatNumberAsc(eventId);

        List<Long> seatIds = new ArrayList<>();
        for (Seat seat : seats) {
            seatIds.add(seat.getId());
        }
        Set<Long> heldSeatIds = seatHoldService.findHeldSeatIds(seatIds);
        heldSeatIds.addAll(bookingRepository.findSeatIdsWithActivePendingBooking(seatIds, Instant.now()));

        List<SeatResponse> result = new ArrayList<>();
        for (Seat seat : seats) {
            boolean held = heldSeatIds.contains(seat.getId());
            result.add(SeatResponse.from(seat, held));
        }
        return result;
    }
}