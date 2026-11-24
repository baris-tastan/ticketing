CREATE SEQUENCE payment_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE payment (
    id            bigint        PRIMARY KEY,
    booking_id    bigint        NOT NULL UNIQUE,
    amount        numeric(38,2) NOT NULL,
    payment_token varchar(255)  NOT NULL,
    status        varchar(20)   NOT NULL CHECK (status IN ('PROCESSING', 'APPROVED', 'DECLINED')),
    created_at    timestamp(6) with time zone NOT NULL
);