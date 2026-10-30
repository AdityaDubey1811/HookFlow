package com.hookflow.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record CreateEventRequest(
        @NotBlank(message = "Event type is required")
        @Size(max = 255, message = "Event type must not exceed 255 characters")
        String eventType,

        @NotBlank(message = "Payload is required")
        String payload,

        @Size(max = 255, message = "Idempotency key must not exceed 255 characters")
        String idempotencyKey
) {

}
