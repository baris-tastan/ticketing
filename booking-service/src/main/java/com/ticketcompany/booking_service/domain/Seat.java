package com.ticketcompany.booking_service.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "seat_number"}))

public class Seat {

    protected Seat() {}

    public Seat(Event event, String seatNumber, BigDecimal price) {
        this.event = event;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = SeatStatus.AVAILABLE;
    }
        public void markSold() {
        if (status == SeatStatus.SOLD) {
            throw new IllegalStateException("Seat " + seatNumber + " is already sold");
        }
        this.status = SeatStatus.SOLD;
    }

    @Id 
    @GeneratedValue 
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;
    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    public Long getId() { return id; }
    public Event getEvent() { return event; }
    public String getSeatNumber() { return seatNumber; }
    public BigDecimal getPrice() { return price; }
    public SeatStatus getStatus() { return status; }



}
