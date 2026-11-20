package com.ticketcompany.booking_service.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Event {
    protected Event(){}
    public Event(String name, String venue, Instant startsAt){
        this.name = name;
        this.venue = venue;
        this.startsAt = startsAt;

    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getVenue() { return venue; }
    public Instant getStartsAt() { return startsAt; }

    @Id 
    @GeneratedValue 
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String venue;
    @Column(nullable = false)
    private Instant startsAt;

    
    
}
