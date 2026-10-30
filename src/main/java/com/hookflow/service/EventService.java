package com.hookflow.service;
import com.hookflow.repository.EventRepository;
import com.hookflow.repository.DeliveryRepository;
import com.hookflow.repository.WebhookEndpointRepository;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.hookflow.dto.CreateEventRequest;
import com.hookflow.dto.EventResponse;
import java.util.UUID;
import com.hookflow.entity.Event;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final WebhookEndpointRepository webhookEndpointRepository;
    private final DeliveryRepository deliveryRepository;
    private final ObjectMapper objectMapper;
    public EventService(
            EventRepository eventRepository,
            WebhookEndpointRepository webhookEndpointRepository,
            DeliveryRepository deliveryRepository,
            ObjectMapper objectMapper
    ){
        this.eventRepository = eventRepository;
        this.webhookEndpointRepository = webhookEndpointRepository;
        this.deliveryRepository = deliveryRepository;
        this.objectMapper = objectMapper;
    }
    private void validateJsonPayload(String payload) {
        try {
            objectMapper.readTree(payload);
        } catch (JacksonException | IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Payload must contain valid JSON"
            );
        }
    }
    @Transactional
    public EventResponse createEvent(CreateEventRequest request) {
        validateJsonPayload(request.payload());
        if (request.idempotencyKey() != null
                && !request.idempotencyKey().isBlank()) {

            Event existingEvent = eventRepository
                    .findByIdempotencyKey(request.idempotencyKey())
                    .orElse(null);

            if (existingEvent != null) {
                if (!existingEvent.getEventType().equals(request.eventType())
                        || !existingEvent.getPayload().equals(request.payload())) {
                    throw new IllegalArgumentException(
                            "Idempotency key was already used with a different event"
                    );
                }
                return new EventResponse(
                        existingEvent.getId(),
                        existingEvent.getEventType(),
                        existingEvent.getPayload(),
                        existingEvent.getIdempotencyKey(),
                        existingEvent.getCreatedAt()
                );
            }
        }
        UUID eventId = UUID.randomUUID();
        Event event = new Event(
                eventId,
                request.eventType(),
                request.payload(),
                request.idempotencyKey()
        );
        Event savedEvent = eventRepository.save(event);
        return new EventResponse(
                savedEvent.getId(),
                savedEvent.getEventType(),
                savedEvent.getPayload(),
                savedEvent.getIdempotencyKey(),
                savedEvent.getCreatedAt()
        );
    }

}
