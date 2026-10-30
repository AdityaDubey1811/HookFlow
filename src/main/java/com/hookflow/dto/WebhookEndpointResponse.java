package com.hookflow.dto;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
public record WebhookEndpointResponse(
        UUID id,
        String url,
        String description,
        List<String> subscribedEventTypes,
        boolean active,
        OffsetDateTime createdAt
) {
}
