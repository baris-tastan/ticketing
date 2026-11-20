package com.ticketcompany.booking_service.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.ticketcompany.booking_service.domain.Seat;
import com.ticketcompany.booking_service.domain.SeatStatus;
import com.ticketcompany.booking_service.dto.HoldResponse;
import com.ticketcompany.booking_service.exception.SeatNotFoundException;
import com.ticketcompany.booking_service.exception.SeatUnavailableException;
import com.ticketcompany.booking_service.repository.SeatRepository;

@Service
public class SeatHoldService {

    private final StringRedisTemplate redis;
    private final SeatRepository seatRepository;
    private final Duration holdDuration;

    public SeatHoldService(StringRedisTemplate redis, SeatRepository seatRepository, @Value("${booking.hold-duration}") Duration holdDuration) {
        this.redis = redis;
        this.holdDuration = holdDuration;
        this.seatRepository = seatRepository;
    }

    public HoldResponse holdSeat(Long seatId, String customerEmail) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        if (seat.getStatus() == SeatStatus.SOLD) {
            throw new SeatUnavailableException(seatId);
        }

        String key = holdKey(seatId);
        Instant now = Instant.now();
        Boolean acquired = redis.opsForValue().setIfAbsent(key, customerEmail, holdDuration);

        if (Boolean.TRUE.equals(acquired)) {
            return new HoldResponse(seatId, now.plus(holdDuration));      // a new hold: we know the deadline
        }

        String currentHolder = redis.opsForValue().get(key);
        if (!customerEmail.equals(currentHolder)) {
            throw new SeatUnavailableException(seatId);                    // someone else holds it
        }

        return new HoldResponse(seatId, remainingDeadline(key, seatId));   // already ours: ask Redis
    }

    private Instant remainingDeadline(String key, Long seatId) {
        Long millisLeft = redis.getExpire(key, TimeUnit.MILLISECONDS);
        if (millisLeft == null || millisLeft <= 0) {
            throw new SeatUnavailableException(seatId);                    // it expired a moment ago
        }
        return Instant.now().plusMillis(millisLeft);
    }

    public Set<Long> findHeldSeatIds(List<Long> seatIds) {
        Set<Long> held = new HashSet<>();
        if (seatIds.isEmpty()) {
            return held;
        }

        List<String> keys = new ArrayList<>();
        for (Long seatId : seatIds) {
            keys.add(holdKey(seatId));
        }

        List<String> holders = redis.opsForValue().multiGet(keys);
        if (holders == null) {
            return held;
        }

        for (int i = 0; i < seatIds.size(); i++) {
            if (holders.get(i) != null) {
                held.add(seatIds.get(i));
            }
        }
        return held;
    }

    public String findHolder(Long seatId) {
        return redis.opsForValue().get(holdKey(seatId));
    }

    private String holdKey(Long seatId) {
        return "hold:seat:" + seatId;
    }
}