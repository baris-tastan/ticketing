package com.ticketcompany.booking_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.ticketcompany.booking_service.domain.Event;
import com.ticketcompany.booking_service.domain.Seat;
import com.ticketcompany.booking_service.exception.SeatUnavailableException;
import com.ticketcompany.booking_service.repository.EventRepository;
import com.ticketcompany.booking_service.repository.SeatRepository;
import com.ticketcompany.booking_service.service.BookingService;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class DoubleBookingTest {

    @Autowired BookingService bookingService;
    @Autowired EventRepository eventRepository;
    @Autowired SeatRepository seatRepository;
    @Autowired StringRedisTemplate redisTemplate;
    @Autowired JdbcTemplate jdbcTemplate;

    @Test
    void manyUsersBookingTheSameSeatAtOnceOnlyOneWins() throws Exception {
        Seat seat = createSeat();
        int users = 20;

        ExecutorService pool = Executors.newFixedThreadPool(users);
        CountDownLatch startGate = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        AtomicInteger rejections = new AtomicInteger();
        List<Throwable> unexpected = new CopyOnWriteArrayList<>();

        for (int i = 0; i < users; i++) {
            String email = "user" + i + "@example.com";
            pool.submit(() -> {
                try {
                    startGate.await();
                    bookingService.createBooking(seat.getId(), email);
                    successes.incrementAndGet();
                } catch (SeatUnavailableException e) {
                    rejections.incrementAndGet();
                } catch (Throwable e) {
                    unexpected.add(e);
                }
            });
        }

        startGate.countDown();
        pool.shutdown();
        assertThat(pool.awaitTermination(30, TimeUnit.SECONDS)).isTrue();

        assertThat(unexpected).isEmpty();
        assertThat(successes.get()).isEqualTo(1);
        assertThat(rejections.get()).isEqualTo(users - 1);
        assertThat(activeBookingsFor(seat)).isEqualTo(1);
    }

    @Test
    void databaseRejectsSecondBookingEvenIfRedisHoldIsLost() {
        Seat seat = createSeat();
        bookingService.createBooking(seat.getId(), "ali@example.com");

        redisTemplate.delete("hold:seat:" + seat.getId());

        assertThatThrownBy(() -> bookingService.createBooking(seat.getId(), "ayse@example.com"))
                .isInstanceOf(SeatUnavailableException.class);
        assertThat(activeBookingsFor(seat)).isEqualTo(1);
    }

    private Seat createSeat() {
        Event event = eventRepository.save(
                new Event("Test Concert", "Test Hall", Instant.now().plus(30, ChronoUnit.DAYS)));
        return seatRepository.save(new Seat(event, "T1", new BigDecimal("100.00")));
    }

    private long activeBookingsFor(Seat seat) {
        return jdbcTemplate.queryForObject(
                "select count(*) from booking where seat_id = ? and status in ('PENDING', 'CONFIRMED')",
                Long.class, seat.getId());
    }
}