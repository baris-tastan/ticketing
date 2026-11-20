CREATE UNIQUE INDEX IF NOT EXISTS one_active_booking_per_seat
    ON booking (seat_id)
    WHERE status IN ('PENDING', 'CONFIRMED');