package com.ticketcompany.booking_service.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticketcompany.booking_service.dto.BookingResponse;
import com.ticketcompany.booking_service.dto.CreateBookingRequest;
import com.ticketcompany.booking_service.dto.PayRequest;
import com.ticketcompany.booking_service.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request,
                                                         @AuthenticationPrincipal Jwt jwt) {
        BookingResponse booking = bookingService.createBooking(request.seatId(), jwt.getSubject());
        URI location = URI.create("/bookings/" + booking.id());
        return ResponseEntity.created(location).body(booking);
    }

    @PostMapping("/{bookingId}/pay")
    public BookingResponse pay(@PathVariable Long bookingId,
                               @Valid @RequestBody PayRequest request,
                               @AuthenticationPrincipal Jwt jwt) {
        return bookingService.pay(bookingId, jwt.getSubject(), request.cardNumber());
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getBooking(@PathVariable Long bookingId,
                                      @AuthenticationPrincipal Jwt jwt) {
        return bookingService.getBooking(bookingId, jwt.getSubject());
    }
}