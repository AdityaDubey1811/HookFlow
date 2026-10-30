package com.hookflow.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
public record CreateWebhookEndpointRequest(
        @NotBlank(message = "URL is required")
        @Size(max = 2048,message = "URl must not exceed 2048 characters")
        String url,
        @Size(max = 255,message = "Description must not exceed 255 characters")
        String description,
        @NotEmpty(message = "At least one subscribed event type is required")
        List<
                @NotBlank(message = "Event types must not be blank")
                @Size(max = 255,message = "Event types must not exceed 255 characters")
                String
                > subscribedEventTypes
) {

}
