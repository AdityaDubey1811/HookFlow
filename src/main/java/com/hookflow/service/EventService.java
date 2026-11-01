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
import com.hookflow.entity.WebhookEndpoint;
import java.util.List;
import com.hookflow.entity.Delivery;
import jakarta.persistence.EntityManager;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final WebhookEndpointRepository webhookEndpointRepository;
    private final DeliveryRepository deliveryRepository;
    private final ObjectMapper objectMapper;
    private final EntityManager entityManager;
    public EventService(
            EventRepository eventRepository,
            WebhookEndpointRepository webhookEndpointRepository,
            DeliveryRepository deliveryRepository,
            ObjectMapper objectMapper,
            EntityManager entityManager
    ){
        this.eventRepository = eventRepository;
        this.webhookEndpointRepository = webhookEndpointRepository;
        this.deliveryRepository = deliveryRepository;
        this.objectMapper = objectMapper;
        this.entityManager = entityManager;
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
    private boolean areJsonPayloadsEqual(String first, String second) {
        try {
            var firstJson = objectMapper.readTree(first);
            var secondJson = objectMapper.readTree(second);

            return firstJson.equals(secondJson);

        } catch (JacksonException | IllegalArgumentException exception) {
            return false;
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
                        || !areJsonPayloadsEqual(
                        existingEvent.getPayload(),
                        request.payload()
                )) {
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
        Event savedEvent = eventRepository.saveAndFlush(event);
        entityManager.refresh(savedEvent);
        List<WebhookEndpoint> endpoints =
                webhookEndpointRepository.findActiveSubscribedEndpoints(
                        savedEvent.getEventType()
                );
        List<Delivery> deliveries = endpoints.stream()
                .map(endpoint -> new Delivery(
                        UUID.randomUUID(),
                        savedEvent,
                        endpoint
                ))
                .toList();
        if (!deliveries.isEmpty()) {
            deliveryRepository.saveAll(deliveries);
        }
        return new EventResponse(
                savedEvent.getId(),
                savedEvent.getEventType(),
                savedEvent.getPayload(),
                savedEvent.getIdempotencyKey(),
                savedEvent.getCreatedAt()
        );
    }

}
