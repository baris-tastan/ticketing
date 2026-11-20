package com.ticketcompany.booking_service.config;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ticketcompany.booking_service.domain.Event;
import com.ticketcompany.booking_service.domain.Seat;
import com.ticketcompany.booking_service.repository.EventRepository;
import com.ticketcompany.booking_service.repository.SeatRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    public DataSeeder(EventRepository eventRepository, SeatRepository seatRepository) {
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public void run(String... args) {
        if (eventRepository.count() > 0) {
            System.out.println(">>> Sample data already exists, skipping");
            return;
        }

        Event concert = eventRepository.save(
            new Event("Tarkan Concert", "Harbiye Open Air Theatre",
                      Instant.parse("2026-12-20T18:00:00Z")));

        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            seats.add(new Seat(concert, "A" + i, new BigDecimal("500.00")));
        }
        seatRepository.saveAll(seats);

        System.out.println(">>> Created event " + concert.getId() + " with " + seats.size() + " seats");
    }
}