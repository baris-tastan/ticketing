package com.ticketcompany.booking_service.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Booking {



    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Instant expiresAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @Column(nullable = false)
    private String customerEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    protected Booking() {}

    public Booking(Seat seat, String customerEmail, Instant expiresAt) {
        this.seat = seat;
        this.customerEmail = customerEmail;
        this.status = BookingStatus.PENDING;
        this.createdAt = Instant.now();
        this.expiresAt = expiresAt;
    }
    public Long getId() { return id; }
    public Seat getSeat() { return seat; }
    public String getCustomerEmail() { return customerEmail; }
    public BookingStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public void confirm() {
        requirePending("confirm");
        this.status = BookingStatus.CONFIRMED;
    }
    public Instant getExpiresAt() { return expiresAt; }

    public void cancel() {
        requirePending("cancel");
        this.status = BookingStatus.CANCELLED;
    }

    private void requirePending(String action) {
        if (status != BookingStatus.PENDING) {
            throw new IllegalStateException(
                "Cannot " + action + " booking " + id + " because it is " + status);
        }
    }
}