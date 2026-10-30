
CREATE UNIQUE INDEX uq_event_idempotency_key
    ON event (idempotency_key)
    WHERE idempotency_key IS NOT NULL;

