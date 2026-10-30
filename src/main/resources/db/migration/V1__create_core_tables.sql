
CREATE TABLE webhook_endpoint (
    id UUID PRIMARY KEY,
    url TEXT NOT NULL,
    description VARCHAR(255),
    subscribed_event_types TEXT[] NOT NULL,
    secret TEXT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE event (
    id UUID PRIMARY KEY,
    event_type VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    idempotency_key VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE delivery (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL REFERENCES event(id),
    endpoint_id UUID NOT NULL REFERENCES webhook_endpoint(id),
    status VARCHAR(30) NOT NULL,
    attempt_count INTEGER NOT NULL DEFAULT 0,
    next_attempt_at TIMESTAMPTZ,
    last_http_status INTEGER,
    last_error TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_delivery_attempt_count CHECK (attempt_count >= 0),
    CONSTRAINT chk_delivery_status CHECK (
        status IN ('PENDING', 'PROCESSING', 'DELIVERED', 'FAILED')
    ),
    CONSTRAINT uq_delivery_event_endpoint UNIQUE (event_id, endpoint_id)
);

CREATE INDEX idx_webhook_endpoint_active
    ON webhook_endpoint (active);

CREATE INDEX idx_event_type_created_at
    ON event (event_type, created_at DESC);

CREATE INDEX idx_delivery_status_next_attempt
    ON delivery (status, next_attempt_at)
    WHERE status IN ('PENDING', 'FAILED');

CREATE INDEX idx_delivery_event_id
    ON delivery (event_id);

CREATE INDEX idx_delivery_endpoint_id
    ON delivery (endpoint_id);