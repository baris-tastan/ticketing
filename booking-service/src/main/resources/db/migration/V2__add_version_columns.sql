-- V2: version columns for optimistic locking (@Version)

ALTER TABLE booking ADD COLUMN version bigint NOT NULL DEFAULT 0;
ALTER TABLE seat    ADD COLUMN version bigint NOT NULL DEFAULT 0;