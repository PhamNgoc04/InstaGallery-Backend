-- Links a booking to the working window it was accepted against.
-- Dev startup outside production also adds this column via Exposed.
-- Cancelling a booking frees the time because only PENDING, CONFIRMED,
-- and IN_PROGRESS rows block a new reservation.

ALTER TABLE bookings
    ADD COLUMN availability_id BIGINT NULL,
    ADD INDEX idx_bookings_availability_id (availability_id),
    ADD CONSTRAINT fk_bookings_availability
        FOREIGN KEY (availability_id) REFERENCES availability_schedules (id)
        ON DELETE SET NULL;
