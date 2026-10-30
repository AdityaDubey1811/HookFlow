
package com.hookflow.service;

import com.hookflow.dto.CreateWebhookEndpointRequest;
import com.hookflow.dto.WebhookEndpointResponse;
import com.hookflow.entity.WebhookEndpoint;
import com.hookflow.repository.WebhookEndpointRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import com.hookflow.exception.ResourceNotFoundException;

@Service
public class WebhookEndpointService {

    private final WebhookEndpointRepository webhookEndpointRepository;
    private final EntityManager entityManager;
    private final SecureRandom secureRandom = new SecureRandom();

    public WebhookEndpointService(
            WebhookEndpointRepository webhookEndpointRepository,
            EntityManager entityManager
    ) {
        this.webhookEndpointRepository = webhookEndpointRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public WebhookEndpointResponse createEndpoint(
            CreateWebhookEndpointRequest request
    ) {
        URI uri;

        try {
            uri = URI.create(request.url().trim());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid endpoint URL");
        }

        String scheme = uri.getScheme();

        if (scheme == null
                || !(scheme.equalsIgnoreCase("https")
                || scheme.equalsIgnoreCase("http"))
                || uri.getHost() == null
                || uri.getUserInfo() != null) {
            throw new IllegalArgumentException(
                    "Endpoint URL must be a valid HTTP or HTTPS URL"
            );
        }

        byte[] secretBytes = new byte[32];
        secureRandom.nextBytes(secretBytes);

        String secret = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(secretBytes);

        UUID id = UUID.randomUUID();

        WebhookEndpoint endpoint = new WebhookEndpoint(
                id,
                uri.toString(),
                request.description(),
                request.subscribedEventTypes().toArray(String[]::new),
                secret
        );

        WebhookEndpoint saved = webhookEndpointRepository.saveAndFlush(
                endpoint
        );

        entityManager.refresh(saved);

        return new WebhookEndpointResponse(
                saved.getId(),
                saved.getUrl(),
                saved.getDescription(),
                List.copyOf(Arrays.asList(saved.getSubscribedEventTypes())),
                saved.isActive(),
                saved.getCreatedAt()
        );
    }
    @Transactional(readOnly = true)
    public List<WebhookEndpointResponse> getAllEndpoints() {
        return webhookEndpointRepository.findAll()
                .stream()
                .map(endpoint -> new WebhookEndpointResponse(
                        endpoint.getId(),
                        endpoint.getUrl(),
                        endpoint.getDescription(),
                        List.of(endpoint.getSubscribedEventTypes()),
                        endpoint.isActive(),
                        endpoint.getCreatedAt()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public WebhookEndpointResponse getEndpointById(UUID id) {
        WebhookEndpoint endpoint = webhookEndpointRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Webhook endpoint not found: " + id
                ));

        return new WebhookEndpointResponse(
                endpoint.getId(),
                endpoint.getUrl(),
                endpoint.getDescription(),
                List.of(endpoint.getSubscribedEventTypes()),
                endpoint.isActive(),
                endpoint.getCreatedAt()
        );
    }
    @Transactional
    public void deactivateEndpoint(UUID id) {
        WebhookEndpoint endpoint = webhookEndpointRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Webhook endpoint not found: " + id
                ));

        endpoint.deactivate();
    }

    @Transactional
    public void activateEndpoint(UUID id) {
        WebhookEndpoint endpoint = webhookEndpointRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Webhook endpoint not found: " + id
                ));

        endpoint.activate();
    }

}
