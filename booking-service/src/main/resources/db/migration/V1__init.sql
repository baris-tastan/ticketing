-- V1: the initial structure of booking-service

-- Sequences: the counters Hibernate uses to generate ids
CREATE SEQUENCE event_seq   START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE seat_seq    START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE booking_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE users_seq   START WITH 1 INCREMENT BY 50;

CREATE TABLE event (
    id         bigint                      NOT NULL,
    name       character varying(255)      NOT NULL,
    venue      character varying(255)      NOT NULL,
    starts_at  timestamp(6) with time zone NOT NULL,
    CONSTRAINT event_pkey PRIMARY KEY (id)
);

CREATE TABLE seat (
    id          bigint                 NOT NULL,
    event_id    bigint                 NOT NULL,
    seat_number character varying(255) NOT NULL,
    price       numeric(38,2)          NOT NULL,
    status      character varying(255) NOT NULL,
    CONSTRAINT seat_pkey PRIMARY KEY (id),
    CONSTRAINT seat_event_fk FOREIGN KEY (event_id) REFERENCES event (id),
    CONSTRAINT seat_event_number_unique UNIQUE (event_id, seat_number),
    CONSTRAINT seat_status_check CHECK (status IN ('AVAILABLE', 'SOLD'))
);

CREATE TABLE booking (
    id             bigint                      NOT NULL,
    seat_id        bigint                      NOT NULL,
    customer_email character varying(255)      NOT NULL,
    status         character varying(255)      NOT NULL,
    created_at     timestamp(6) with time zone NOT NULL,
    expires_at     timestamp(6) with time zone NOT NULL,
    CONSTRAINT booking_pkey PRIMARY KEY (id),
    CONSTRAINT booking_seat_fk FOREIGN KEY (seat_id) REFERENCES seat (id),
    CONSTRAINT booking_status_check CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED'))
);

-- At most one PENDING or CONFIRMED booking per seat
CREATE UNIQUE INDEX one_active_booking_per_seat
    ON booking (seat_id)
    WHERE status IN ('PENDING', 'CONFIRMED');

CREATE TABLE users (
    id            bigint                      NOT NULL,
    email         character varying(255)      NOT NULL,
    password_hash character varying(255)      NOT NULL,
    created_at    timestamp(6) with time zone NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_email_unique UNIQUE (email)
);