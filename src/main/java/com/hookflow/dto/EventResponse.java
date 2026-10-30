package com.hookflow.dto;
import java.time.OffsetDateTime;
import java.util.UUID;
public record EventResponse(
        UUID id,
        String eventType,
        String payload,
        String idempotencyKey,
        OffsetDateTime createdAt
) {
}
